package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.enums.*;
import com.sumscope.optimus.moneymarket.commons.util.CollectionsUtil;
import com.sumscope.optimus.moneymarket.commons.util.TimePeroidUtils;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import com.sumscope.optimus.moneymarket.model.dto.*;
import com.sumscope.optimus.moneymarket.service.InstitutionService;
import com.sumscope.optimus.moneymarket.service.MmTagService;
import com.sumscope.optimus.moneymarket.service.UserBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by fan.bai on 2016/8/3.
 * MmQuoteQueryListResultDto转换器。用于MmQuoteQueryListResultDto 与MmQuote数据模型的相互转换
 */
@Component
public class MmQuoteQueryListResultDtoConverter {
    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private ContactUserInfoDtoConverter contactUserInfoDtoConverter;

    @Autowired
    private UserBaseService userBaseService;

    @Autowired
    private MmTagService mmTagService;

    /**
     * 转换model至dto
     *
     * @param mmQuoteList a list of MmQuote
     * @return dto MmQuoteQueryListResultDto
     */
    public List<MmQuoteQueryListResultDto> convertModelToDto(List<MmQuote> mmQuoteList) {
        List<MmQuoteQueryListResultDto> result = new ArrayList<>();
        if (mmQuoteList != null && mmQuoteList.size() > 0) {
            for (MmQuote quote : mmQuoteList) {
                //前端页面仅展示仍包含有效明细数据的报价单
                if (containsActiveDetails(quote)) {
                    result.add(convertSingleModelToDto(quote));
                }
            }
        }
        return result;
    }

    private boolean containsActiveDetails(MmQuote quote) {
        if (quote.getSource() == QuoteSource.QQ) {
            //当QQ报价的明细数据完全不存在时，也在前端显示该数据
            return true;
        } else {
            if (CollectionsUtil.isEmptyOrNullCollection(quote.getMmQuoteDetails())) {
                return false;
            }
            for (MmQuoteDetails details : quote.getMmQuoteDetails()) {
                if (details.isActive()) {
                    return true;
                }
            }
            return false;
        }
    }

    public MmQuoteQueryListResultDto convertSingleModelToDto(MmQuote quote) {
        MmQuoteQueryListResultDto dto = new MmQuoteQueryListResultDto();
        //build properties for main dto
        buildSimpleProperties(dto, quote);

        buildQuoteSourceAndPrimeListInfo(quote, dto);
        buildMarketListRelatedInfo(dto, quote);
        buildMemo(dto, quote);
        buildQuoteDetailsDto(dto, quote);

        return dto;
    }

    /**
     * 根据查询后返回的结果数据排序
     * 排序 按照日期 有数字的排在OFR或BID的上面 ，OFR或者BID的报价要排在 “-”的报价上面，有区间的报价取区间的最小值排
     * 即：有报价的排在上面，然后是OFR或者BID 最后是“-”的报价数据 ，有区间的报价取区间的最小值排，日期区间自定义的不用排
     * @param parameterDto
     * @param mmQuoteResultDto
     * @return
     */
    public  List<MmQuoteQueryListResultDto> MmQuoteMainQueryParameters(MmQuoteQueryParameterDto parameterDto, List<MmQuoteQueryListResultDto> mmQuoteResultDto){
        List<MmQuoteQueryListResultDto> noSortList = new ArrayList<>();
        List<MmQuoteQueryListResultDto> bidOfrList = new ArrayList<>();
        List<MmQuoteQueryListResultDto> needSortList = new ArrayList<>();

        String reqPeriod = parameterDto.getOrderByPeriod().getDisplayName();
        for (int i = 0; i < mmQuoteResultDto.size(); i++) {
            MmQuoteQueryListResultDto item = mmQuoteResultDto.get(i);
            List<MmQuoteQueryResultDetailsDto> list = item.getQuoteDetails();
            boolean isPeriodPrice = false;
            for(MmQuoteQueryResultDetailsDto detailsDto : list) {
                List<MmQuoteQueryPriceDetailsDto> priceDetailsList = detailsDto.getPriceDetails();
                for(MmQuoteQueryPriceDetailsDto priceDetail : priceDetailsList) {
                    String period = priceDetail.getPeriodDisplayValue();
                    if(period.contains("-")) {
                        period = period.substring(0,period.indexOf("-"));
                    }

                    if(reqPeriod.equals(period) && !detailsDto.getPriceDisplayString().equals("-")) {
                        isPeriodPrice = true;
                        if(detailsDto.getPriceDisplayString().equals("OFR") || detailsDto.getPriceDisplayString().equals("BID")){
                            bidOfrList.add(item);
                        }
                        else
                        {
                            needSortList.add(item);
                        }
                        break;
                    }
                }
                if (isPeriodPrice) {
                    break;
                }
            }
            if(!isPeriodPrice){
                noSortList.add(item);
            }
        }

        sortMmQuoteResultDto(parameterDto, needSortList, reqPeriod);
        mmQuoteResultDto.clear();
        mmQuoteResultDto.addAll(needSortList);
        mmQuoteResultDto.addAll(bidOfrList);
        mmQuoteResultDto.addAll(noSortList);
        return mmQuoteResultDto;
    }

    private void sortMmQuoteResultDto(final MmQuoteQueryParameterDto parameterDto, List<MmQuoteQueryListResultDto> mmQuoteResultDto, final String request) {
        Collections.sort(mmQuoteResultDto,new Comparator<MmQuoteQueryListResultDto>(){
            @Override
            public int compare(MmQuoteQueryListResultDto b1, MmQuoteQueryListResultDto b2) {
                List<MmQuoteQueryResultDetailsDto> list1 =b1.getQuoteDetails();
                List<MmQuoteQueryResultDetailsDto> list2 =b2.getQuoteDetails();
                String b1Val = getPriceDisplayString(list1);
                String b2Val = getPriceDisplayString(list2);
                if(b1Val.contains("-") && !b1Val.matches("^(\\-?)\\d+(\\.\\d+)?$"))b1Val = b1Val.substring(0,b1Val.indexOf("-"));
                if(b2Val.contains("-") && !b2Val.matches("^(\\-?)\\d+(\\.\\d+)?$"))b2Val = b2Val.substring(0,b2Val.indexOf("-"));
                b1Val= b1Val==null? "" : b1Val;
                b2Val= b2Val==null? "" : b2Val;
                if("ASC".equals(parameterDto.getOrderSeq())){
                    return (b1Val=="" && b2Val=="") ? 0 : ((b1Val=="" && b2Val!="") ? 1:((b1Val!="" && b2Val=="")? -1:new BigDecimal(b1Val).compareTo(new BigDecimal(b2Val))));
                }else{
                    return (b1Val=="" && b2Val=="") ? 0 : ((b1Val=="" && b2Val!="") ? -1:((b1Val!="" && b2Val=="")? 1:new BigDecimal(b2Val).compareTo(new BigDecimal(b1Val))));
                }

            }
            private String getPriceDisplayString(List<MmQuoteQueryResultDetailsDto> list) {
                String priceDisplayString = "";
                if(list!=null && list.size()>0){
                    for(MmQuoteQueryResultDetailsDto m : list){
                        List<MmQuoteQueryPriceDetailsDto> priceDetailsList = m.getPriceDetails();
                        for(MmQuoteQueryPriceDetailsDto p : priceDetailsList){
                            String actual = p.getPeriodDisplayValue();
                            if(actual.contains("-"))actual = actual.substring(0,actual.indexOf("-"));
                            if(request.equals(actual)){
                                priceDisplayString = m.getPriceDisplayString();
                                break;
                            }
                        }
                    }
                    return priceDisplayString;
                }else{
                    return "";
                }
            }
        });
    }

    private void buildQuoteDetailsDto(MmQuoteQueryListResultDto dto, MmQuote mmQuote) {
        dto.setQuoteDetails(new ArrayList<>());
        if (CollectionsUtil.isEmptyOrNullCollection(mmQuote.getMmQuoteDetails())) {
            return;
        }

//        根据标准期限以及报价单类型，初始化生成报价明细dto
        Map<QuoteTimePeriod, MmQuoteQueryResultDetailsDto> detailsMap = initDetailsByQuoteType(mmQuote.getQuoteType());
        dto.getQuoteDetails().addAll(detailsMap.values());

//      根据实际的报价明细，将model映射为对应的Dto。由于数据库存在非标准期限，但是前端页面只能展示标准期限，有可能出现
//      一个报价明细数据对应多条报价明细Dto的情况。
        for (MmQuoteDetails details : mmQuote.getMmQuoteDetails()) {
            List<QuoteTimePeriod> affectTimePeroid = TimePeroidUtils.getAffectTimePeroid(details.getDaysLow(), details.getDaysHigh());
            for (QuoteTimePeriod timePeriod : affectTimePeroid) {
                MmQuoteQueryResultDetailsDto mmQuoteQueryResultDetailsDto;
                if ((timePeriod == QuoteTimePeriod.T7D || timePeriod == QuoteTimePeriod.T14D)
                        && ContactQuoteAttribute.FI == mmQuote.getQuoteType().getContactQuoteAttribute()) {
                    //同业理财不显示7D，14D数据，合并入1月
                    mmQuoteQueryResultDetailsDto = detailsMap.get(QuoteTimePeriod.T1M);
                } else {
                    mmQuoteQueryResultDetailsDto = detailsMap.get(timePeriod);
                }
//                将报价明细数据写入Dto
                mapDetailsToMmQuoteQueryResultDetailsDto(mmQuote.getDirection(), mmQuoteQueryResultDetailsDto, details);
            }
        }

        //设置期限明细dto的价格显示值，需要根据所有可能包括的明细数据获取极限值
        Iterator<MmQuoteQueryResultDetailsDto> iterator = dto.getQuoteDetails().iterator();
        while (iterator.hasNext()) {
            MmQuoteQueryResultDetailsDto detailsDto = iterator.next();
            if (!containsDetails(detailsDto)) {
                //如果没有明细数据，删除当前Detailsdto
                iterator.remove();
            }
        }


    }

    private Map<QuoteTimePeriod, MmQuoteQueryResultDetailsDto> initDetailsByQuoteType(QuoteType quoteType) {
        Map<QuoteTimePeriod, MmQuoteQueryResultDetailsDto> result = new HashMap<>();
        for (QuoteTimePeriod timePeriod : QuoteTimePeriod.values()) {
            if ((quoteType.getContactQuoteAttribute() == ContactQuoteAttribute.CD && timePeriod.isCDSupported()) ||
                    (quoteType.getContactQuoteAttribute() == ContactQuoteAttribute.FI && timePeriod.isFISupported())) {
                MmQuoteQueryResultDetailsDto dto = new MmQuoteQueryResultDetailsDto();
                dto.setPriceDisplayString("-");
                dto.setQuoteTimePeriod(timePeriod);
                result.put(timePeriod, dto);
            }
        }
        return result;
    }

    private boolean containsDetails(MmQuoteQueryResultDetailsDto detailsDto) {
        return !CollectionsUtil.isEmptyOrNullCollection(detailsDto.getPriceDetails());

    }

    private void mapDetailsToMmQuoteQueryResultDetailsDto(Direction direction, MmQuoteQueryResultDetailsDto mmQuoteQueryResultDetailsDto, MmQuoteDetails details) {
        if (!containsDetails(mmQuoteQueryResultDetailsDto.getPriceDetails(), details)) {
            if (details.isActive()) {
                if (mmQuoteQueryResultDetailsDto.getPriceDetails() == null) {
                    mmQuoteQueryResultDetailsDto.setPriceDetails(new ArrayList<>());
                }
                MmQuoteQueryPriceDetailsDto detailsDto = convertToMmQuoteQueryPriceDetailsDto(direction, details);
                mmQuoteQueryResultDetailsDto.getPriceDetails().add(detailsDto);
                //如果该明细是对应的标准期限，将数据值设置入mmQuoteQueryResultDetailsDto
                QuoteTimePeriod periodFromValue = TimePeroidUtils.getPeriodFromValue(details.getDaysLow(), details.getDaysHigh());
                if (mmQuoteQueryResultDetailsDto.getQuoteTimePeriod() == periodFromValue) {
                    setPriceDisplayStringToDto(detailsDto, mmQuoteQueryResultDetailsDto, details);
                } else {
                    //对于QQ报价，days_low = days_high = 60,既表明是2M的标准报价，因此需要在此特别判断
                    QuoteTimePeriod timePeriod = canBeSeenAsStandardTimePeriod(details.getDaysLow(), details.getDaysHigh());
                    if (mmQuoteQueryResultDetailsDto.getQuoteTimePeriod() == timePeriod) {
                        setPriceDisplayStringToDto(detailsDto, mmQuoteQueryResultDetailsDto, details);
                    } else {
                        //对于非常规期限报价，则列表只要有数值就需要使用下拉列表显示，因此设置标志位
                        if (mmQuoteQueryResultDetailsDto.getPriceDetails().size() > 0) {
                            mmQuoteQueryResultDetailsDto.setMultipeRecords(true);
                        }
                    }
                }
            }
        }
    }

    private void setPriceDisplayStringToDto(MmQuoteQueryPriceDetailsDto priceDetailsDto, MmQuoteQueryResultDetailsDto mmQuoteQueryResultDetailsDto, MmQuoteDetails details) {
        mmQuoteQueryResultDetailsDto.setPriceDisplayString(removePercentageSignal(priceDetailsDto.getPriceDisplayValue()));
        for (MmQuoteQueryPriceDetailsDto  priceDetails :mmQuoteQueryResultDetailsDto.getPriceDetails()) {
            if (mmQuoteQueryResultDetailsDto.getPriceDetails().size() > 1 || priceDetails.getQuantityDisplayValue()!=null) {
                mmQuoteQueryResultDetailsDto.setMultipeRecords(true);
            }
        }
    }

    private String removePercentageSignal(String priceDisplayString) {
        if (priceDisplayString != null && priceDisplayString.contains("%")) {
            return priceDisplayString.replace("%", "");
        }
        return priceDisplayString;
    }

    private QuoteTimePeriod canBeSeenAsStandardTimePeriod(Integer daysLow, Integer daysHigh) {
        if (daysHigh != null && daysLow != null && daysHigh.intValue() == daysLow.intValue() && daysLow > 0) {
            String timeString = TimePeroidUtils.convertNumberOfDaysToString(daysLow);
            for (QuoteTimePeriod period : QuoteTimePeriod.values()) {
                if (period.getDisplayName().equals(timeString)) {
                    return period;
                }
            }
        }
        return null;
    }

    private MmQuoteQueryPriceDetailsDto convertToMmQuoteQueryPriceDetailsDto(Direction direction, MmQuoteDetails details) {
        MmQuoteQueryPriceDetailsDto result = new MmQuoteQueryPriceDetailsDto();
        result.setQuoteDetailsID(details.getId());
        result.setPriceLow(getScaledBigDecimalAndStripTrailingZeros(4, details.getPriceLow()));
        result.setPriceHigh(getScaledBigDecimalAndStripTrailingZeros(4, details.getPrice()));
        result.setPriceDisplayValue(getRangeDisplayString(direction, result.getPriceHigh(), result.getPriceLow(), false));
        result.setQuantityDisplayValue(getRangeDisplayString(direction, details.getQuantity(), details.getQuantityLow(), true));
        result.setPeriodDisplayValue(getPeriodDisplayString(details.getDaysLow(), details.getDaysHigh()));
        return result;
    }

    private String getPeriodDisplayString(int low, int high) {
        if (low == -1 || high == -1) {
            LogManager.warn("不合法的报价明细日期区间[ " + low + " - " + high + "] ");
            return "-";
        }
        QuoteTimePeriod period = TimePeroidUtils.getPeriodFromValue(low, high);
        if (period != null) {
            return period.getDisplayName();
        }
        String lowStr = TimePeroidUtils.convertNumberOfDaysToString(low);
        String highStr = TimePeroidUtils.convertNumberOfDaysToString(high);
        if (StringUtils.isBlank(lowStr) || StringUtils.isBlank(highStr)) {
            LogManager.warn("不合法的期限表达：[" + lowStr + " - " + highStr + "]");
            return "-";
        }
        if (lowStr.equals(highStr)) {
            return lowStr;
        } else {
            return lowStr + "-" + highStr;
        }
    }

    private String getQuantityOrPriceDisplayString(BigDecimal value, boolean isQuantity) {
        if (isQuantity) {
            final int quantityLimit = 10000;
            final int scale = 4;
            if (value.doubleValue() >= quantityLimit) {
                BigDecimal divide = value.divide(BigDecimal.valueOf(quantityLimit), scale, BigDecimal.ROUND_HALF_UP);
                divide = divide.stripTrailingZeros();
                if (divide.doubleValue() == divide.intValue()) {
                    return divide.intValue() + "亿";
                } else {
                    return divide.doubleValue() + "亿";
                }
            } else {
                return value.toString() + "万";
            }
        } else {
            if (value != null) {
                getScaledBigDecimalAndStripTrailingZeros(4, value);
                return value.toString() + "%";
            } else {
                return "";
            }

        }
    }

    private String getRangeDisplayString(Direction direction, BigDecimal high, BigDecimal low, boolean isQuantity) {

        // 这里包括两种情况，一种min和max是相同的价格，一种min和max都是"-"
        String result;
        if (high == null && low == null) {
            if (!isQuantity) {
                if (direction == Direction.OUT) {
                    result = "OFR";
                } else {
                    result = "BID";
                }
            } else {
                return null;
            }
        } else {
            if (low == null) {
                result = getQuantityOrPriceDisplayString(high, isQuantity) + "以下";
            } else if (high == null) {
                result = getQuantityOrPriceDisplayString(low, isQuantity) + "以上";
            } else {
                if (high.doubleValue() == low.doubleValue()) {
                    result = getQuantityOrPriceDisplayString(low, isQuantity);
                } else {
                    result = getQuantityOrPriceDisplayString(low, isQuantity) + "-" + getQuantityOrPriceDisplayString(high, isQuantity);
                }
            }
        }
        return result;
    }

    private BigDecimal getScaledBigDecimalAndStripTrailingZeros(int scale, BigDecimal value) {
        if (value != null) {
            value = value.setScale(scale, BigDecimal.ROUND_HALF_UP);
            value = value.stripTrailingZeros();//此方法会把10的倍数的数字转为十六进制显示
            //把10或100的倍数的值转换成阿拉伯数字，即把十六进制转成十进制展示
            if(value.remainder(new BigDecimal(10)).equals(new BigDecimal(0))||value.remainder(new BigDecimal(100)).equals(new BigDecimal(0))){
                value=value.setScale(BigDecimal.ROUND_UP, BigDecimal.ROUND_UP);
            }
        }
        return value;
    }

    private boolean containsDetails(List<MmQuoteQueryPriceDetailsDto> priceDetails, MmQuoteDetails details) {
        if (CollectionsUtil.isEmptyOrNullCollection(priceDetails)) {
            return false;
        } else {
            for (MmQuoteQueryPriceDetailsDto dto : priceDetails) {
                if (dto.getQuoteDetailsID().equals(details.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void buildMemo(MmQuoteQueryListResultDto dto, MmQuote quote) {
        Set<MmQuoteTag> tags = quote.getTags();
        List<MmTag> result = new ArrayList<>();
         if (CollectionsUtil.isEmptyOrNullCollection(tags)) {
            dto.setMemo(quote.getMemo());
        } else {
             /**
              * 因为标签在备注中需要排序,set是无序的所以,先把set中的数据拿到Array中,
              * 在根据标签的seq字段进行升序排序显示到备注中
              */
             for (MmQuoteTag mmQuoteTag : tags){
                 MmTag mmTag = mmTagService.retrieveAllTags().get(mmQuoteTag.getTagCode());
                 result.add(mmTag);
             }
             Collections.sort(result, new Comparator<MmTag>() {
                 @Override
                 public int compare(MmTag o1, MmTag o2) {
                     if(o1.getSeq()>o2.getSeq()){
                         return 1;
                     }
                     return -1;
                 }
             });
            StringBuilder memoBuilder = new StringBuilder("");
            for (MmTag mmTag : result) {
                if (mmTag != null) {
                    memoBuilder.append(mmTag.getTagName()).append(",");
                } else {
                    LogManager.warn("找不到标签信息，Code：" + mmTag.getTagCode());
                }
            }
            if (quote.getMemo() != null) {
                memoBuilder.append(quote.getMemo());
            }
            dto.setMemo(memoBuilder.toString());
        }
    }

    private void buildMarketListRelatedInfo(MmQuoteQueryListResultDto dto, MmQuote quote) {

        QuoteSource quoteSource = dto.getQuoteSource();
        String quoteUserId = quote.getQuoteUserId();
        User user = userBaseService.retreiveAllUsersGroupByUserID().get(quoteUserId!=null ? quoteUserId: quote.getQuoteOperatorId());
        Institution institution = institutionService.retrieveInstitutionsById().get(quote.getInstitutionId());
        PrimeCompany primeInstitution = institutionService.retrievePrimeInstitutions().get(quote.getInstitutionId());
        StringBuilder quoterSBuilder = new StringBuilder("");
        StringBuilder quoterContactsSBuilder = new StringBuilder("");
        switch (quoteSource) {
            case QQ:
                appendUserName(quote.getQuoteUserId(), user, quoterSBuilder);
                appendContacts(QuoteSource.QQ, quoteUserId, user, quoterContactsSBuilder);
                break;
            case QB:
                appendInsititutionName(quote, institution, quoterSBuilder);
                quoterSBuilder.append("-");
                appendUserName(quoteUserId, user, quoterSBuilder);
                appendContacts(QuoteSource.QB, quoteUserId, user, quoterContactsSBuilder);
                break;
            case PRIME_QB:
                appendInsititutionName(quote, institution, quoterSBuilder);
                appendPrimeInsititutionTelephone(quote, primeInstitution, quoterContactsSBuilder);
                break;
        }
        dto.setMarketListQuoter(quoterSBuilder.toString());
        dto.setMarketListQuoterContacts(quoterContactsSBuilder.toString());


    }

    private void appendContacts(QuoteSource source, String quoteUserId, User user, StringBuilder quoterContactsSBuilder) {
        if (user != null) {
            if (QuoteSource.QQ == source || QuoteSource.QB == source) {
                String telephone = user.getTelephone();
                String mobile = user.getMobile();
                String contacts = "";
                if ((telephone != null && !"".equals(telephone) )&& (mobile != null && !"".equals(mobile))) {
                    contacts += telephone + "," + mobile;
                } else if (telephone != null&&!"".equals(telephone)) {
                    contacts += telephone;
                } else if (mobile != null&&!"".equals(mobile)) {
                    contacts += mobile;
                }
                quoterContactsSBuilder.append(contacts);
            }
        }
    }

    private void appendInsititutionName(MmQuote quote, Institution institution, StringBuilder sb) {
        if (institution != null) {
            sb.append(institution.getName());
        } else {
            LogManager.warn("找不到对应机构信息,ID：" + quote.getInstitutionId());
        }
    }

    private void appendPrimeInsititutionTelephone(MmQuote quote, PrimeCompany primeCompany, StringBuilder sb) {
        if (primeCompany != null) {
            sb.append(primeCompany.getTelephone());
        } else {
            LogManager.warn("找不到对应精品机构信息,ID：" + quote.getInstitutionId());
        }
    }

    private void appendUserName(String quoteUserId, User user, StringBuilder sb) {
        if (user != null) {
            sb.append(user.getDisplayName());
        } else {
            LogManager.warn("找不到对应用户信息,ID：" + quoteUserId);
            sb.append(quoteUserId);
        }
    }

    private void buildSimpleProperties(MmQuoteQueryListResultDto dto, MmQuote quote) {
        BeanUtils.copyProperties(quote, dto);
        dto.setQuoteId(quote.getId());
        dto.setLastUpdateTime(quote.getLastUpdateTime());
        dto.setBankNature(getBankNatureEnum(quote.getBankNature()));
        dto.setFundSizeEnum(getFundSizeEnum(quote.getFundSize()));
    }

    private QueryFundSize getFundSizeEnum(BigDecimal fundSize) {
        if (fundSize == null) {
            return QueryFundSize.SMALLER_ONE_H;
        }
        double fundSizeD = fundSize.doubleValue();
        for (QueryFundSize size : QueryFundSize.values()) {
            if (size.getFundSizeLow() <= fundSizeD && fundSizeD < size.getFundSizeHigh()) {
                return size;
            }
        }
        return QueryFundSize.SMALLER_ONE_H;
    }

    private String getBankNatureEnum(String bankNatureS) {
        for (CalculatedBankNature bankNature : CalculatedBankNature.values()) {
            if (bankNature != CalculatedBankNature.OTHERS && bankNature.getBankNatureCode().equals(bankNatureS)) {
                return bankNature.name();
            }
        }
        return CalculatedBankNature.OTHERS.name();
    }

    private void buildQuoteSourceAndPrimeListInfo(MmQuote quote, MmQuoteQueryListResultDto dto) {
        QuoteSource source = quote.getSource();
        if (QuoteSource.QQ == source) {
            dto.setQuoteSource(source);
        } else {
            PrimeCompany primeInstitution = institutionService.retrievePrimeInstitutions().get(quote.getInstitutionId());
            if (primeInstitution != null && primeInstitution.getActive()!=0) {
                buildPrimeInstitutionRelatedInfo(quote.getQuoteType().getContactQuoteAttribute(), dto, primeInstitution, quote);
                dto.setQuoteSource(QuoteSource.PRIME_QB);
            } else {
                dto.setQuoteSource(QuoteSource.QB);
            }
        }
    }

    private void buildPrimeInstitutionRelatedInfo(ContactQuoteAttribute contactQuoteAttribute, MmQuoteQueryListResultDto dto, PrimeCompany primeInstitution, MmQuote quote) {
        Institution institution = institutionService.retrieveInstitutionsById().get(quote.getInstitutionId());
        dto.setPrimeListInstitutionName(institution.getName());
        dto.setPrimeListInstitutionTelephone(primeInstitution.getTelephone());
        Map<String, User> userMap = userBaseService.retreiveAllUsersGroupByUserID();
        User user = userMap.get(quote.getQuoteUserId()!=null ? quote.getQuoteUserId() : null);
        QuoteUserInfoDto quoteUserInfoDto= contactUserInfoDtoConverter.convertQuoteUserToDto(user);
        quoteUserInfoDto.setQuoteAttribute(contactQuoteAttribute);
        List<QuoteUserInfoDto> quoteUserInfoDtos=new ArrayList<>();
        if(quoteUserInfoDto!=null) quoteUserInfoDtos.add(quoteUserInfoDto);
        dto.setPrimeListContacts(quoteUserInfoDtos);
    }

}
