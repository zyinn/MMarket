package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.util.DateUtil;
import com.sumscope.optimus.moneymarket.commons.enums.*;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import com.sumscope.optimus.moneymarket.model.dto.MockRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by fan.bai on 2016/5/26.
 * 用于生成模拟数据的服务。
 */
@Component
public class MockDataService {

    @Autowired
    private BusinessMmQuoteManagementService quoteManageService;

    @Autowired
    private UserBaseService userBaseService;
    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private MmTagService mmTagService;

    private Random detailsPeroidRandom;


    @Value("${application.enableMockData}")
    private boolean enableMockData;


    private static Date getNextDay(Date date, int nextDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.add(Calendar.DAY_OF_MONTH, nextDays);
        date = calendar.getTime();
        return date;
    }

    private MmQuote createMmQuote( QuoteType quoteType, User user) {
        Random random = new Random();

        MmQuote result = new MmQuote();
        result.setQuoteType(quoteType);
        boolean b = random.nextBoolean();
        if(b){
            result.setDirection(Direction.OUT);
        }else{
            result.setDirection(Direction.IN);
        }

        result.setMemo("虚拟测试数据");
        result.setMethodType(MethodType.SEF);
        result.setQuoteOperatorId(user.getUserId());


        if ("QB".equals(user.getSource())) {
            result.setQuoteUserId(user.getUserId());
            setupQBQuote(result);

        } else {
            result.setQuoteUserId(user.getUserId());
            result.setInstitutionId("-1");
            result.setExpiredDate(getNextDay(new Date(),0));
            result.setProvince(user.getProvince());
            result.setCustodianQualification(false);
            result.setBankNature(user.getBankNature());
            result.setSource(QuoteSource.QQ);
        }
        return result;
    }

    private void setupQBQuote( MmQuote mmQuote) {
        Random r = new Random();
        Institution institution;
        boolean prime = r.nextBoolean();
        if(prime){
            institution = getRandomPrimeInstitution(r);
        }else{
            institution = getRandomInstitution(r);
        }
        if(institution != null){
            setupMmQuoteByInstitution(institution,mmQuote);
        }else{
            int i = r.nextInt(institutionService.retrieveInstitutionsById().size());
            List<Institution> institutions = new ArrayList<>(institutionService.retrieveInstitutionsById().values());
            institution = institutions.get(i);
            setupMmQuoteByInstitution(institution,mmQuote);
        }

        mmQuote.setTags(getRandomTags());
    }

    private Set<MmQuoteTag> getRandomTags() {
        List<MmTag> tags = new ArrayList<>(mmTagService.retrieveAllTags().values());
        Random r = new Random();
        Set<MmQuoteTag> createdTag = new HashSet<>();
        //创建3个随机标签
        while(createdTag.size()<3){
            int i = r.nextInt(tags.size());
            MmTag mmTag = tags.get(i);
            if(!containsTag(createdTag,mmTag)){
                MmQuoteTag mmQuoteTag = new MmQuoteTag();
                mmQuoteTag.setTagCode(mmTag.getTagCode());
                createdTag.add(mmQuoteTag);
            }
        }

        return createdTag;
    }

    private boolean containsTag(Set<MmQuoteTag> createdTags, MmTag mmTag) {
        for(MmQuoteTag t: createdTags){
            if(t.getTagCode().equals(mmTag.getTagCode())){
                return true;
            }
        }
        return false;
    }

    private void setupMmQuoteByInstitution(Institution institution, MmQuote mmQuote) {
        mmQuote.setCustodianQualification("1".equals(institution.getQualification()));
        mmQuote.setInstitutionId(institution.getId());
        mmQuote.setFundSize(institution.getFundSize());
        mmQuote.setBankNature(institution.getBankNature());
        mmQuote.setExpiredDate(getNextDay(new Date(),2));
        mmQuote.setProvince(institution.getProvince());
        mmQuote.setSource(QuoteSource.QB);
    }

    private Institution getRandomPrimeInstitution(Random r) {
        List<PrimeCompany> institutions = new ArrayList<>(institutionService.retrievePrimeInstitutions().values());
        PrimeCompany primeCompany = institutions.get( r.nextInt(institutionService.retrievePrimeInstitutions().size()));
        return institutionService.retrieveInstitutionsById().get(primeCompany.getId());
    }

    private Institution getRandomInstitution(Random r) {
        int i = r.nextInt(institutionService.retrieveInstitutionsById().size());
        List<Institution> institutions = new ArrayList<>(institutionService.retrieveInstitutionsById().values());
        return institutions.get(i);
    }

    private static MmQuoteDetails getMmQuoteDetails(Random random, QuoteTimePeriod period) {
        MmQuoteDetails details = new MmQuoteDetails();
        details.setActive(true);
        details.setDaysLow(period.getDaysLow());
        details.setDaysHigh(period.getDaysHigh());
        double dFirst = random.nextDouble();
        double dSecond = random.nextDouble();
        details.setPrice(dFirst > dSecond ? BigDecimal.valueOf(dFirst) : BigDecimal.valueOf(dSecond));
        details.setPriceLow(dFirst > dSecond ? BigDecimal.valueOf(dSecond) : BigDecimal.valueOf(dFirst));

        final int randomNum = 100000;
        int firstI = random.nextInt(randomNum);
        int secondI = random.nextInt(randomNum);
        details.setQuantity(firstI > secondI ? BigDecimal.valueOf(firstI) : BigDecimal.valueOf(secondI));
        details.setQuantityLow(firstI > secondI ? BigDecimal.valueOf(secondI) : BigDecimal.valueOf(firstI));
        details.setLastUpdateTime(DateUtil.getCurrentDatetime());
        return details;
    }

    @SuppressWarnings("static-access")
    private int createMmQuotes(MockRequestDto mockRequestDto) {
        Map<String, User> stringUserMap = userBaseService.retreiveAllUsersGroupByUserID();
        List<User> qbUsers = getUsers(stringUserMap.values(),"QB");
        List<User> qqUsers = getUsers(stringUserMap.values(),"QQ");

        int qqcount = 0;
        int qbcount = 0;
        int numberOfQuotes = 0;
        Random r = new Random();
        Iterator<User> qbUserIt = qbUsers.iterator();
        Iterator<User> qqUserIt = qqUsers.iterator();
        while((qqcount < mockRequestDto.getNumberOfQQUser() && qqUserIt.hasNext() )||
                (qbcount < mockRequestDto.getNumberOfQBUser() && qbUserIt.hasNext() ) ){
            User user = null;
            //随机获取QQ或者QB用户，需要考虑QQ或者QB用户不足的情况。
            if(r.nextBoolean()){
                if(qbcount < mockRequestDto.getNumberOfQBUser() && qbUserIt.hasNext() ){
                    user = qbUserIt.next();
                    qbcount++;
                }else{
                    if(qqUserIt.hasNext()){
                        user = qqUserIt.next();
                        qqcount++;
                    }
                }
            }else {
                if(qqcount < mockRequestDto.getNumberOfQQUser() && qqUserIt.hasNext() ){
                    user = qqUserIt.next();
                    qqcount++;
                }else{
                    if(qbUserIt.hasNext()){
                        user = qbUserIt.next();
                        qbcount++;
                    }
                }
            }
            if(user == null){
                return numberOfQuotes;
            }

            if("QQ".equals(user.getSource())){
                if(qqcount < mockRequestDto.getNumberOfQQUser()){
                    numberOfQuotes += createMmQuoteForUser(user);
                    qqcount ++;
                }
            }

            if("QB".equals(user.getSource())){
                if(qbcount < mockRequestDto.getNumberOfQBUser()){
                    numberOfQuotes += createMmQuoteForUser(user);
                    qbcount ++;
                }
            }
            try {
                Thread.currentThread().sleep(mockRequestDto.getDelayTime());
            } catch (InterruptedException e) {
                LogManager.info("创建虚拟数据现成sleep失败！" + e.toString());
            }
        }

        return numberOfQuotes;

    }

    private List<User> getUsers(Collection<User> values, String type) {
        List<User> result = new ArrayList<>();
        Iterator<User> iterator = values.iterator();
        while (iterator.hasNext()){
            User next = iterator.next();
            if(type.equals(next.getSource())){
               result.add(next);
            }
        }
        return result;
    }

    private int createMmQuoteForUser(User user) {
        Random random = new Random();

        List<MmQuote> results = new ArrayList<>();
        //每人创建2个类型的报价
        List<QuoteType> createdTypes = new ArrayList<>();
        QuoteType[] values = QuoteType.values();


        while (results.size() < 2 ) {
            //从4个报价类型中取随机值
            int index = random.nextInt(4);
            QuoteType quoteType = values[index];
            if (!createdTypes.contains(quoteType)) {
                MmQuote mmQuote = createMmQuote( quoteType, user);
                mmQuote.setMmQuoteDetails(createMmDetail(user.getSource()));
                createdTypes.add(quoteType);
                results.add(mmQuote);
            }
        }

        quoteManageService.setupMmQuotesInTransaction(results, MmQuoteManagementService.Source.QB_CLIENT);
        return results.size();

    }

    private List<MmQuoteDetails> createMmDetail(String source) {
        Random random = getDetailsPeroidRandom();
        List<MmQuoteDetails> result = new ArrayList<>();
        //每个报价单创建4个报价明细
        final int totalDetailsNumber = 4;
        final int indexNumber = 9;
        int createdDetails = 0;
        QuoteTimePeriod[] periods = QuoteTimePeriod.values();
        List<QuoteTimePeriod> createdPeriods = new ArrayList<>();
        while (createdDetails <= totalDetailsNumber) {
            int index = random.nextInt(indexNumber);
            QuoteTimePeriod period = periods[index];
            if (!createdPeriods.contains(period)) {
                createdPeriods.add(period);
                MmQuoteDetails details = getMmQuoteDetails(random, period);
                if (source.equals("QQ")) {
                    details.setQqMessage("QQ虚拟测试数据");
                }
                result.add(details);
                createdDetails++;
            }
        }
        return result;
    }

    public int startMock(MockRequestDto mockRequestDto) {
        if(enableMockData){
            return createMmQuotes(mockRequestDto);
        }else{
            return 0;
        }
    }

    private Random getDetailsPeroidRandom() {
        if (detailsPeroidRandom == null){
            detailsPeroidRandom = new Random();
        }
        return detailsPeroidRandom;
    }
}
