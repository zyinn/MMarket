package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.messagebus.MessageBusException;
import com.sumscope.optimus.commons.messagebus.MessageBusInitialException;
import com.sumscope.optimus.commons.messagebus.MessageBusPublisher;
import com.sumscope.optimus.commons.util.DateUtil;
import com.sumscope.optimus.commons.util.JsonUtil;
import com.sumscope.optimus.moneymarket.MessageBusConfiguration;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteSource;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.commons.util.CollectionsUtil;
import com.sumscope.optimus.moneymarket.commons.util.QuoteDateUtils;
import com.sumscope.optimus.moneymarket.commons.util.UUIDUtils;
import com.sumscope.optimus.moneymarket.dao.MmQuoteDao;
import com.sumscope.optimus.moneymarket.dao.MmQuoteDetailsDao;
import com.sumscope.optimus.moneymarket.dao.MmQuoteTagDao;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.wiring.ClassNameBeanWiringInfoResolver;

import java.util.*;

/**
 * Created by fan.bai on 2016/8/10.
 * MmQuoteManagementService 实现类
 */
abstract class AbstractMmQuoteManagementServiceImpl implements MmQuoteManagementService {
    private static final String CHANGEDTAGS = "CHANGEDTAGS";
    private static final String SAMETAGS = "SAMETAGS";

    @Autowired
    private MmQuoteQueryService quoteQueryService;

    @Autowired
    @Qualifier(value = MessageBusConfiguration.APPLICATION_MESSAGE_BUS_QUOTE_PUBLISHER)
    private MessageBusPublisher quoteSetupPublisher;

    protected abstract MmQuoteDao getMmQuoteDao();

    protected abstract MmQuoteDetailsDao getMmQuoteDetailsDao();

    protected abstract MmQuoteTagDao getQuoteTagDao();

    /**
     * 将来自于QQ消息的报价单写入数据库。
     * 更新规则为若本日已经有过完全一致报价内容的报价单（QQ发布内容完全一致，QQ发布内容存在memo中），则对该报价单进行
     * 更新操作，将本次收到的数据添加入明细表中，但是不删除原明细表数据，这是与QB报价不同的地方。如此逻辑的原因在于：
     * 解析程序可能对一条QQ报价分解为不同的期限数据发布给总线，因此一条报了三个期限的QQ报价将被分成3次解析数据，每次
     * 一个期限的方式发送至总线。我们需要对此进行合并。
     * 使用该逻辑带来的问题在于，如果一个用户在一天中多次发布相同内容的QQ报价，则我们仅仅更新该报价的最后生成日期，并
     * 不生成多条报价单。这在一定程度上减少了报价单数量。
     */
    private void processQuotesForQQ(List<MmQuote> quoteList, List<MmQuote> quotesWasUpdated, List<MmQuote> quotesToBeInsert, List<MmQuoteDetails> detailsToBeInsert, List<MmQuoteTag> tagsToBeInsert, List<String> quoteToDeleteTags) {
        if (quoteList == null || quoteList.size() == 0) {
            return;
        }
        for (MmQuote quote : quoteList) {
            //取当日该用户的已发布过的内容完全一致的报价单
            MmQuote currentMmQuoteInDB = getCurrentQQQuote(quote);
            processSingleMmQuote(quote, currentMmQuoteInDB, quotesWasUpdated, quotesToBeInsert, detailsToBeInsert, tagsToBeInsert, quoteToDeleteTags, QuoteSource.QQ);
        }
    }

    private MmQuote getCurrentQQQuote(MmQuote quote) {
        List<MmQuote> mmQuotes = quoteQueryService.queryMmQuoteMain(getParameterForQQSearch(quote),true, false);
        if (CollectionsUtil.isEmptyOrNullCollection(mmQuotes)) {
            return null;
        } else {
            for (MmQuote mmQuote : mmQuotes) {
                String dbMemo = mmQuote.getMemo();
                String memo = quote.getMemo();
                if (StringUtils.equals(memo, dbMemo)) {
                    return mmQuote;
                }
            }
        }
        return null;
    }

    private MmQuoteMainQueryParameters getParameterForQQSearch(MmQuote quote) {
        MmQuoteMainQueryParameters parameters = new MmQuoteMainQueryParameters();
        parameters.setQuoteUserId(quote.getQuoteUserId());
        parameters.setCreateTime(new Date());
        parameters.setDirection(quote.getDirection());
        List<QuoteType> types = new ArrayList<>();
        types.add(quote.getQuoteType());
        parameters.setQuoteTypes(types);
        parameters.setPaging(false);
        parameters.setOnlyValidQuotes(true);
        return parameters;
    }


    /**
     * 将QB客户端创建的报价列表写入数据库。
     * 写入逻辑描述如下：
     * 首先根据业务主键从数据库中获取当前有效报价单，根据是否能找到对应有效报价来决定是新增的报价还是需要更新的报价。
     * 若是新增报价，则使用insert语句将报价主表，报价明细表，标签列表写入数据库。日期取当前日期。注意，写入时不做重复
     * 检查，也就是说允许用户对一个机构重复报价。
     * 若是包含有效ID的报价，按ID取出现有报价，先判断是否有修改（包括主表修改及明细表修改），若无修改则不做任何处理。若有修改，
     * 1. 若该报价不是当日报价，将现有报价的过期日期设置为昨日，并将明细表active设置为0.将该报价去除ID作为本日新报价写入
     * 数据库。这样做的原因在于形成历史数据。否则大量报价会不断的延期造成历史数据不存在。
     * 2. 若该报价是当日报价，则进行报价的更新。
     * 报价更新的逻辑是：
     * 比较现有数据及传入数据的主表信息，若有变化，更新现有主表信息
     * 再比较两者的明细表信息。比较之前先检查传入数据业务主键无重复。再按ID匹配明细数据。匹配时需要检查传入数据的业务主键
     * 与现有数据的业务主键是一致的，否则说明ID有错误，抛出数据错误。若业务主键一致，但其余数据不一致，则更新明细数据。
     * 将新增，删除（作废）及修改的数据发布到总线上。
     */
    @Override
    public void setupMmQuotesInTransaction(List<MmQuote> mmQuoteList, Source source) {
        long t1 = System.currentTimeMillis();
        writeQuoteListToDB(mmQuoteList, source);
        long t2 = System.currentTimeMillis();

        System.out.println("Save used time: " + (t2 - t1));
    }

    private void writeQuoteListToDB(List<MmQuote> mmQuoteList, Source source) {
        if (mmQuoteList == null || mmQuoteList.size() <= 0) {
            return;
        }
        //列表记录发生过更新的报价单，出参
        List<MmQuote> quotesWasUpdated = new ArrayList<>();
        //列表记录需要新增的报价单，出参
        List<MmQuote> quotesToBeInsert = new ArrayList<>();
        //列表记录需要新增的报价单明细。此处记录的是需要更新的报价单中新增的明细数据，而不是新增报价单中的明细数据。出参
        List<MmQuoteDetails> detailsToBeInsert = new ArrayList<>();
        //列表记录需要新增的标签。此处记录的是是需要更新的报价单的标签明细。不记录新增报价单的标签。
        //对于标签记录，我们使用简单的一次性删除所有标签再增加全部新标签的方式更新数据。因为标签数据本身
        //只有删除和新增的操作，对单一数据新增、删除的效率不如一次性处理效率高。出参
        List<MmQuoteTag> tagsToBeInsert = new ArrayList<>();
        //列表记录需要删除标签项的报价单ID信息，标签的更新方式见上一行说明。出参
        List<String> quoteToDeleteTags = new ArrayList<>();

        if (source == Source.QB_CLIENT) {
            processQuotesForQBClient(mmQuoteList, quotesWasUpdated, quotesToBeInsert, detailsToBeInsert, tagsToBeInsert, quoteToDeleteTags);
        }

        if (source == Source.EXCEL) {
            processQuotesForEXCEL(mmQuoteList, quotesWasUpdated, quotesToBeInsert);
        }

        if (source == Source.QQ) {
            processQuotesForQQ(mmQuoteList, quotesWasUpdated, quotesToBeInsert, detailsToBeInsert, tagsToBeInsert, quoteToDeleteTags);
        }

        if (source == Source.ARCHIVE) {
            quotesToBeInsert.addAll(mmQuoteList);
        }

        if (quoteToDeleteTags.size() > 0) {
            deleteTagsFromQuotes(quoteToDeleteTags);
        }
        if (tagsToBeInsert.size() > 0) {
            insertTags(tagsToBeInsert);
        }
        if (detailsToBeInsert.size() > 0) {
            insertNewQuoteDetailsToDB(detailsToBeInsert);
        }
        //将报价单以及其包括的报价明细及标签一次性插入数据库
        if (quotesToBeInsert.size() > 0) {
            insertNewQuotesToDB(quotesToBeInsert);
        }
        if (isPublishNeeded() && (quotesWasUpdated.size() > 0 || quotesToBeInsert.size() > 0)) {
            publishMmQuotes(quotesToBeInsert, quotesWasUpdated);
        }
    }

    /**
     * @return true 需要向总线发布改动的数据，一般仅用于业务数据库的处理
     */
    protected abstract boolean isPublishNeeded();

    /**
     * 对于EXCEL导入的数据，没有ID值，由于存在业务主键重复的数据，因此很难根据主键判断对应的报价单。
     * 解决方式是用简单粗暴的删除新增模式。既对于EXCEL导入模式生成的报价单，首先作废该用户所有从属于输入报价单机构的报价单
     * （例如输入报价单列表包含A,B,C3个机构，则删除该用户这三个用户的有效报价单）
     * 再重新插入新增的报价单。
     */
    private void processQuotesForEXCEL(List<MmQuote> mmQuoteList, List<MmQuote> quotesWasUpdated, List<MmQuote> quotesToBeInsert) {
        if (mmQuoteList == null || mmQuoteList.size() == 0) {
            return;
        }

        //获取当前用户所有从属于输入报价单机构的报价单并作废
        deactivateCurrentExcelQuotes(mmQuoteList, quotesWasUpdated);

        if (!isEmptyList(mmQuoteList)) {
            for (MmQuote quote : mmQuoteList) {
                processInsertMmQuote(quote, quotesToBeInsert);
            }
        }

    }

    private void deactivateCurrentExcelQuotes(List<MmQuote> mmQuoteList, List<MmQuote> quotesWasUpdated) {
        String currentUserId = mmQuoteList.get(0).getQuoteUserId();
        List<String> institutionIdList = new ArrayList<>();
        for (MmQuote quote : mmQuoteList) {
            institutionIdList.add(quote.getInstitutionId());
        }

        MmQuoteDetailsQueryParameters parameters = new MmQuoteDetailsQueryParameters();
        parameters.setQuoteUserId(currentUserId);
        parameters.setInstitutionIdList(institutionIdList);
        parameters.setActive(false);
        parameters.setOnlyValidQuotes(true);
        List<MmQuote> mmQuotes = quoteQueryService.queryMmQuoteDetails(parameters, true);

        if (!isEmptyList(mmQuotes)) {
            List<String> idList = new ArrayList<>();
            Date yesterdayDate = QuoteDateUtils.getYesterdayExpiredTime();
            for (MmQuote quote : mmQuotes) {
                deactivateMmQuote(quote, quotesWasUpdated, yesterdayDate, false);
                idList.add(quote.getId());
            }
            //不逐次作废报价单而是一次性作废
            getMmQuoteDao().deactivateMmQuotes(yesterdayDate, idList);
        }
    }

    private boolean isEmptyList(List list) {
        return list == null || list.size() == 0;
    }

    private void processQuotesForQBClient(List<MmQuote> mmQuoteList, List<MmQuote> quotesWasUpdated, List<MmQuote> quotesToBeInsert, List<MmQuoteDetails> detailsToBeInsert, List<MmQuoteTag> tagsToBeInsert, List<String> quoteToDeleteTags) {
        //一次性按ID获取所有当前报价单，一次性获取的性能远高于循环内逐次获取
        Map<String, MmQuote> currentQuotesMap = getCurrentMmQuotesFromDB(mmQuoteList);
        for (MmQuote quote : mmQuoteList) {
//            逐一处理报价单。对于需要新增的报价单写入quotestToBeInsert列表，之后一次性写入数据库。需要更新的报价单则
//            在逐一处理时逐次更新数据库，并写入quotesWasUpdated，用于向总线发布。
            MmQuote currentMmQuoteInDB = currentQuotesMap.get(quote.getId());
            processSingleMmQuote(quote, currentMmQuoteInDB, quotesWasUpdated, quotesToBeInsert, detailsToBeInsert, tagsToBeInsert, quoteToDeleteTags, QuoteSource.QB);
        }
    }

    private void insertNewQuoteDetailsToDB(List<MmQuoteDetails> detailsToBeInsert) {
        getMmQuoteDetailsDao().insertQuoteDetails(detailsToBeInsert);
    }

    private void insertTags(List<MmQuoteTag> tagsToBeInsert) {
        getQuoteTagDao().insertMmQuoteTags(tagsToBeInsert);
    }

    private void deleteTagsFromQuotes(List<String> quoteToDeleteTags) {
        getQuoteTagDao().deleteTagsFromQuotes(quoteToDeleteTags);
    }

    private void publishMmQuotes(List<MmQuote> quotesToBeInsert, List<MmQuote> quotesWasUpdated) {
        List<MmQuote> list = new ArrayList<>();
        list.addAll(quotesToBeInsert);
        list.addAll(quotesWasUpdated);
        if (list.size() > 0) {
            try {
                String message = JsonUtil.writeValueAsString(list);
                quoteSetupPublisher.publishBusinessMessage(message.getBytes());
            } catch (MessageBusException | MessageBusInitialException e) {
                LogManager.error(new BusinessRuntimeException(BusinessRuntimeExceptionType.MESSAGE_BUS_ERROR, e));
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.MESSAGE_BUS_ERROR, e);
            }
        }
    }

    private void insertNewQuotesToDB(List<MmQuote> quotesToBeInsert) {
        List<MmQuoteDetails> detailsList = new ArrayList<>();
        List<MmQuoteTag> quoteTagList = new ArrayList<>();
        for (MmQuote quote : quotesToBeInsert) {
            quote.setLastUpdateTime(DateUtil.getCurrentDatetime());
            if(quote.getCreateTime()==null){
                //当进行报价单归档时,归档的报价单是有创建日期的,因此不需要设置.
                quote.setCreateTime(DateUtil.getCurrentDatetime());
            }
            if (!CollectionsUtil.isEmptyOrNullCollection(quote.getMmQuoteDetails())) {
                detailsList.addAll(quote.getMmQuoteDetails());
            }
            if (!CollectionsUtil.isEmptyOrNullCollection(quote.getTags())) {
                quoteTagList.addAll(quote.getTags());
            }
        }
        getMmQuoteDao().insertMmQuotes(quotesToBeInsert);
        getMmQuoteDetailsDao().insertQuoteDetails(detailsList);
        getQuoteTagDao().insertMmQuoteTags(quoteTagList);

    }

    private Map<String, MmQuote> getCurrentMmQuotesFromDB(List<MmQuote> mmQuoteList) {
        List<String> idList = new ArrayList<>();
        for (MmQuote mmQuote : mmQuoteList) {
            if (StringUtils.isNotBlank(mmQuote.getId())) {
                idList.add(mmQuote.getId());
            }
        }
        List<MmQuote> mmQuotesInDB = quoteQueryService.queryMmQuotesByIdList(idList);
        Map<String, MmQuote> result = new HashMap<>();
        for (MmQuote quote : mmQuotesInDB) {
            result.put(quote.getId(), quote);
        }
        return result;
    }

    /**
     * 按QB规则逐一处理报价单。QB规则与QQ规则的不同点在于：
     * 当修改报价单时，若是QB报价，则根据是否是今日已报价决定是更新还是创建（昨日QB报价将作废，本期报价作为新报价录入）
     * 若是QQ报价，则只与本日报价比较并更新
     * 同时，QB客户端报价将对删除的明细数据作废，而QQ报价则只会更新及增加，不会删除报价明细。
     */
    private void processSingleMmQuote(MmQuote quote, MmQuote currentMmQuoteInDB, List<MmQuote> quotesWasUpdated,
                                      List<MmQuote> quotesToBeInsert, List<MmQuoteDetails> detailsToBeInsert,
                                      List<MmQuoteTag> tagsToBeInsert, List<String> quoteToDeleteTags, QuoteSource source) {
        if (currentMmQuoteInDB != null) {
            quote.setId(currentMmQuoteInDB.getId());
            setQuoteIDToFields(quote);//将报价单主表ID设置入明细和标签属性。此举是为了避免传入的报价单有维护错误。
            processUpdateMmQuote(quote, currentMmQuoteInDB, quotesWasUpdated, quotesToBeInsert, detailsToBeInsert, tagsToBeInsert, quoteToDeleteTags, source);
        } else {
            processInsertMmQuote(quote, quotesToBeInsert);
        }
    }

    private void setQuoteIDToFields(MmQuote quote) {
        if (StringUtils.isNotBlank(quote.getId())) {
            if (quote.getMmQuoteDetails() != null) {
                for (MmQuoteDetails details : quote.getMmQuoteDetails()) {
                    details.setQuoteId(quote.getId());
                }
            }
            if (quote.getTags() != null) {
                for (MmQuoteTag tag : quote.getTags()) {
                    tag.setQuoteId(quote.getId());
                }
            }
        }
    }

    private void processUpdateMmQuote(MmQuote quote, MmQuote currentMmQuoteInDB, List<MmQuote> quotesWasUpdated,
                                      List<MmQuote> quotesToBeInsert, List<MmQuoteDetails> quoteDetailsToBeInsert,
                                      List<MmQuoteTag> tagsToBeInsert, List<String> quoteToDeleteTags, QuoteSource source) {
        //该列表记录需要修改的明细数据。在比较两个报价是否一致时可以得到不同的报价明细数据，写入此列表用于后期数据库修改
        //否则就需要比较两次影响性能
        List<MmQuoteDetails> detailsNeedsUpdate = new ArrayList<>();
        List<MmQuoteDetails> detailsNeedsInsert = new ArrayList<>();
        //该参数为出参，在比较方法中设置数值用于本方法内的判断
        StringBuffer changedTags = new StringBuffer("");
        if (isQuoteNeedsUpdate(quote, currentMmQuoteInDB, detailsNeedsUpdate, detailsNeedsInsert, changedTags, source)) {
            //若报价单为当天新增，则更新数据。更新的方法是：
            //更新主表信息，设置最新更新时间
            //对于明细数据使用从比较方法中已经获得的明细比较数据，逐次更新已有的明细表，并将新增明细写入传入的列表参数以
            //支持一次性插入数据库
            //如果标签列表，将报价单ID写入删除标签列表以支持后续一次性删除。将当前标签写入新增标签列表以支持后续一次性插入
            // 若报价单为今日之前新增，设置当前数据库中的报价单过期日期为昨日，设置所属报价明细失效，将传入的报价单写入
            // 新增列表支持一次性插入数据库
            if (isCurrentDBQuoteCreatedToday(currentMmQuoteInDB)) {
                updateCurrentQuote(quote, detailsNeedsUpdate);
                if (changedTags.toString().equals(CHANGEDTAGS)) {
                    quoteToDeleteTags.add(quote.getId());
                    if (!CollectionsUtil.isEmptyOrNullCollection(quote.getTags())) {
                        tagsToBeInsert.addAll(quote.getTags());
                    }
                }
                quoteDetailsToBeInsert.addAll(detailsNeedsInsert);
                quotesWasUpdated.add(quote);
            } else {
                deactiveAndInsert(quote, currentMmQuoteInDB, quotesWasUpdated, quotesToBeInsert);
            }
        }


    }

    //将当前数据库中的报价单设置为过期报价单并更新.传入的报价单当作新报价单处理.由于此处不对过期日期做特别处理,意味着
    //当前端更新了数据时,应提示用户更新有效期.
    private void deactiveAndInsert(MmQuote quote, MmQuote currentMmQuoteInDB, List<MmQuote> quotesWasUpdated, List<MmQuote> quotesToBeInsert) {
        Date expiredDate = QuoteDateUtils.getYesterdayExpiredTime();
        deactivateMmQuote(currentMmQuoteInDB, quotesWasUpdated, expiredDate, true);
        //若用户删除了报价单，则报价单内无任何有效报价明细，因此无需新增该报价单。
        if (!containsNoActiveDetails(quote)) {
            processInsertMmQuote(quote, quotesToBeInsert);
        }
    }

    private void deactivateMmQuote(MmQuote currentMmQuoteInDB, List<MmQuote> quotesWasUpdated, Date expiredDate, boolean updateDB) {
        Date currentDatetime = DateUtil.getCurrentDatetime();
        currentMmQuoteInDB.setExpiredDate(expiredDate);
        currentMmQuoteInDB.setLastUpdateTime(currentDatetime);
        quotesWasUpdated.add(currentMmQuoteInDB);
        if (updateDB) {
            getMmQuoteDao().updateMmQuotes(currentMmQuoteInDB);
        }
    }


    private void updateCurrentQuote(MmQuote quote, List<MmQuoteDetails> detailsNeedsUpdate) {
        //如果需要更新的报价单没有仍旧有效的报价明细，意味着该报价单作废，设置过期日期为昨日
        //该检查仅针对QB报价单,对于QQ报价单不做设置,因为允许QQ报价不进行实际价格报价
        if (containsNoActiveDetails(quote) && quote.getSource() == QuoteSource.QB) {
            quote.setExpiredDate(QuoteDateUtils.getYesterdayExpiredTime());
        }
        quote.setLastUpdateTime(DateUtil.getCurrentDatetime());
        getMmQuoteDao().updateMmQuotes(quote);
        getMmQuoteDetailsDao().updateQuoteDetails(detailsNeedsUpdate);
    }

    private boolean containsNoActiveDetails(MmQuote quote) {
        if (CollectionsUtil.isEmptyOrNullCollection(quote.getMmQuoteDetails())) {
            return true;
        } else {
            for (MmQuoteDetails details : quote.getMmQuoteDetails()) {
                if (details.isActive()) {
                    return false;
                }
            }
        }
        return true;
    }

    //判断是否是当日新增数据
    private boolean isCurrentDBQuoteCreatedToday(MmQuote currentMmQuoteInDB) {
        Date createTime = currentMmQuoteInDB.getCreateTime();
        Calendar c1 = Calendar.getInstance();
        c1.setTime(createTime);
        Calendar c2 = Calendar.getInstance();
        int days1 = c1.get(Calendar.DAY_OF_YEAR);
        int days2 = c2.get(Calendar.DAY_OF_YEAR);
        return days1 - days2 == 0;
    }

    // 判断传入的报价单和数据库现有报价单是否一致。先判断主表是否一致，再判断明细表是否一致。不一致的明细
    // 写入detailsNeesUpdate参数中。需要新增的明细则写入detailsNeedsInsert中
    private boolean isQuoteNeedsUpdate(MmQuote quote, MmQuote currentMmQuoteInDB, List<MmQuoteDetails> detailsNeedsUpdate,
                                       List<MmQuoteDetails> detailsNeedsInsert, StringBuffer changedTags, QuoteSource source) {
        boolean same = false;
        List<MmQuoteDetails> detailsOnlyInDB = new ArrayList<>();

        if (source == QuoteSource.QB) {
            same = equalsQuoteMain(quote, currentMmQuoteInDB);
            //对于QB报价可能涉及需要删除明细的问题
            //根据前端数据的比较当前数据库明细完毕，若临时列表中仍然存在数据，表明该数据仅存在于数据库中而被前端删除，设置其为失效
            //该处理仅仅对QB本地报价有效，QQ报价不删除明细，原因在于有可能一个QQ报价被拆分成数个明细逐条接收到。
            same = same & equalsQuoteDetails(quote.getId(), quote.getMmQuoteDetails(), currentMmQuoteInDB.getMmQuoteDetails(), detailsNeedsUpdate, detailsNeedsInsert, detailsOnlyInDB);
            for (MmQuoteDetails details : detailsOnlyInDB) {
                if (details.isActive()) {
                    details.setActive(false);
                    detailsNeedsUpdate.add(details);
                    same = false;
                }
            }
        } else {
            //对于QQ报价存在一个用户多次报同一个报价的问题，此时需要修改主表的最后更新日期，所以不比较equalsQuoteMain而
            //直接设置为false,并且将仅存在与数据库的明细数据拷贝至当前报价单，因为需要向总线发布该条报价单信息时需要发布
            //所有的明细数据。
            equalsQuoteDetails(quote.getId(), quote.getMmQuoteDetails(), currentMmQuoteInDB.getMmQuoteDetails(), detailsNeedsUpdate, detailsNeedsInsert, detailsOnlyInDB);
            if (quote.getMmQuoteDetails() == null) {
                quote.setMmQuoteDetails(new ArrayList<>());
            }
            quote.getMmQuoteDetails().addAll(detailsOnlyInDB);
        }
        boolean sameTags = equalsQuoteTags(quote.getTags(), currentMmQuoteInDB.getTags());
        if (sameTags) {
            changedTags.append(SAMETAGS);
        } else {
            changedTags.append(CHANGEDTAGS);
        }
        same = same & sameTags;
        return !same;
    }

    private boolean equalsQuoteTags(Set<MmQuoteTag> tags, Set<MmQuoteTag> tagsInDB) {
        if (tags == null) {
            tags = new HashSet<>();
        }
        Set<MmQuoteTag> tagsInDBTmp = new HashSet<>();
        if (tagsInDB != null) {
            tagsInDBTmp.addAll(tagsInDB);
        }
        if (tags.size() != tagsInDBTmp.size()) {
            return false;
        }
        for (MmQuoteTag tag : tags) {
            //判断当前Tag是否存在于目标列表中，若存在删除目标列表内对应标签
            if (!existsInOtherSet(tag, tagsInDBTmp)) {
                return false;
            }
        }
        //目标列表仍有数据，表明有些标签仅存在于目标标签
        return tagsInDBTmp.size() == 0;
    }

    private boolean existsInOtherSet(MmQuoteTag tag, Set<MmQuoteTag> tagsInDBTmp) {
        for (MmQuoteTag target : tagsInDBTmp) {
            if (tag.equalsCode(target)) {
                tagsInDBTmp.remove(target);
                return true;
            }
        }
        return false;
    }

    private boolean equalsQuoteDetails(String quoteId, List<MmQuoteDetails> detailsList, List<MmQuoteDetails> detailsInDB,
                                       List<MmQuoteDetails> detailsNeedsUpdate, List<MmQuoteDetails> detailsNeedsInsert,
                                       List<MmQuoteDetails> detailsOnlyInDB) {
        //由于需要对列表进行删除操作，需要一个临时list
        List<MmQuoteDetails> detailsInDBTmp = new ArrayList<>();
        if (detailsInDB != null) {
            detailsInDBTmp.addAll(detailsInDB);
        }

        boolean same = equalsQuoteDetails(quoteId, detailsList, detailsNeedsUpdate, detailsNeedsInsert, detailsInDBTmp);

        if (detailsInDBTmp.size() > 0) {
            detailsOnlyInDB.addAll(detailsInDBTmp);
        }
        return same;
    }

    private boolean equalsQuoteDetails(String quoteId, List<MmQuoteDetails> detailsList, List<MmQuoteDetails> detailsNeedsUpdate, List<MmQuoteDetails> detailsNeedsInsert, List<MmQuoteDetails> detailsInDBTmp) {
        boolean same = true;
        if (detailsList == null) {
            detailsList = new ArrayList<>();
        }
        for (MmQuoteDetails details : detailsList) {
            //需要根据业务主键进行搜索，排除由于前端ID维护不对可能造成的主键重复错误
            MmQuoteDetails currentDetailsInDB = getSameBusinessKeyDetails(details, detailsInDBTmp);
            if (currentDetailsInDB == null) {
                //未在当前数据库找到相同业务主键的明细数据，则该明细数据应为新增数据
                //设置报价单主表ID,写入detailsNeedsInsert表
                details.setId(UUIDUtils.generatePrimaryKey());
                details.setQuoteId(quoteId);
                details.setLastUpdateTime(DateUtil.getCurrentDatetime());
                detailsNeedsInsert.add(details);
                same = false;
            } else {
                //从临时列表中删除已经找到的明细数据
                detailsInDBTmp.remove(currentDetailsInDB);
                //按当前数据库ID设置需要更改数据的ID
                details.setId(currentDetailsInDB.getId());
                details.setLastUpdateTime(currentDetailsInDB.getLastUpdateTime());
                if (!details.equalsForUpdate(currentDetailsInDB)) {
                    details.setLastUpdateTime(DateUtil.getCurrentDatetime());
                    detailsNeedsUpdate.add(details);
                    same = false;
                }
            }
        }
        return same;
    }

    //根据业务主键比较
    private MmQuoteDetails getSameBusinessKeyDetails(MmQuoteDetails details, List<MmQuoteDetails> detailsInDBTmp) {
        for (MmQuoteDetails detailsInDB : detailsInDBTmp) {
            if (details.equalsForBusinessKeysExceptQuoteID(detailsInDB)) {
                return detailsInDB;
            }
        }
        return null;
    }

    private boolean equalsQuoteMain(MmQuote quote, MmQuote currentMmQuoteInDB) {
        return quote.equalsForUpdate(currentMmQuoteInDB);
    }

    private void processInsertMmQuote(MmQuote quote, List<MmQuote> quotesToBeInsert) {
//        设置报价单ID至主表，明细表及标签表
        String quoteId = UUIDUtils.generatePrimaryKey();
        quote.setId(quoteId);
        if (quote.getMmQuoteDetails() != null && quote.getMmQuoteDetails().size() > 0) {
            for (MmQuoteDetails quoteDetails : quote.getMmQuoteDetails()) {
                quoteDetails.setId(UUIDUtils.generatePrimaryKey());
                quoteDetails.setQuoteId(quoteId);
            }
        }
        if (quote.getTags() != null && quote.getTags().size() > 0) {
            for (MmQuoteTag quoteTag : quote.getTags()) {
                quoteTag.setQuoteId(quoteId);
            }
        }
        quotesToBeInsert.add(quote);
    }


}
