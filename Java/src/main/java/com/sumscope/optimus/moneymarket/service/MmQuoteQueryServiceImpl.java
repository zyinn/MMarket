package com.sumscope.optimus.moneymarket.service;
import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.commons.util.CollectionsUtil;
import com.sumscope.optimus.moneymarket.commons.util.Pinyin4jUtil;
import com.sumscope.optimus.moneymarket.commons.util.Utils;
import com.sumscope.optimus.moneymarket.dao.BusinessMmQuoteDao;
import com.sumscope.optimus.moneymarket.dao.BusinessMmQuoteDetailsDao;
import com.sumscope.optimus.moneymarket.dao.BusinessMmQuoteTagDao;
import com.sumscope.optimus.moneymarket.dao.UserPreferenceDao;
import com.sumscope.optimus.moneymarket.facade.QbBaseFacadeImpl;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by fan.bai on 2016/8/6.
 * MmQuoteQueryService接口实现类，仅对业务数据库进行查询
 */
@Component
public class MmQuoteQueryServiceImpl implements MmQuoteQueryService {
    @Autowired
    private BusinessMmQuoteDao mmQuoteDao;

    @Autowired
    private BusinessMmQuoteDetailsDao mmQuoteDetailsDao;

    @Autowired
    private BusinessMmQuoteTagDao quoteTagDao;

    @Autowired
    private UserBaseService userBaseService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private UserPreferenceDao userPreferenceDao;

    @Override
    public List<MmQuoteDetailsQueryResult> queryMmQuoteDetails(MmQuoteDetailsQueryParameters parameters) {
        return mmQuoteDao.queryMmQuoteWithDetails(parameters);
    }

    @Override
    public List<MmQuote> queryMmQuoteDetails(MmQuoteDetailsQueryParameters parameters, boolean withTags) {
        List<MmQuoteDetailsQueryResult> mmQuoteDetailsQueryResults = queryMmQuoteDetails(parameters);
        List<MmQuote> result = convertDetailsQueryResultToMmQuote(mmQuoteDetailsQueryResults);
        if (withTags) {
            Map<String, MmQuote> quoteMap = buildMapForMmQuotes(result);
            retrieveTagsForQuotes(quoteMap);
        }
        return result;
    }


    /**
     * 转换MmQuoteDetailsQueryResult至MmQuote
     */
    private List<MmQuote> convertDetailsQueryResultToMmQuote(List<MmQuoteDetailsQueryResult> mmQuoteDetailsQueryResults) {
        Map<String, MmQuote> tempMap = new HashMap<>();

        for (MmQuoteDetailsQueryResult queryResult : mmQuoteDetailsQueryResults) {
            String quoteId = queryResult.getQuoteId();
            MmQuote mmQuote = tempMap.get(quoteId);
            if (mmQuote == null) {
                mmQuote = buildMmQuoteFromQueryResult(queryResult);
                tempMap.put(quoteId, mmQuote);
            }
            MmQuoteDetails details = buildMmQuoteDetailsFromQueryResult(queryResult);
            if (mmQuote.getMmQuoteDetails() == null) {
                mmQuote.setMmQuoteDetails(new ArrayList<>());
            }
            mmQuote.getMmQuoteDetails().add(details);
        }
        List<MmQuote> result = new ArrayList<>();
        result.addAll(tempMap.values());
        return result;
    }

    private MmQuoteDetails buildMmQuoteDetailsFromQueryResult(MmQuoteDetailsQueryResult queryResult) {
        MmQuoteDetails result = new MmQuoteDetails();
        BeanUtils.copyProperties(queryResult, result);
        result.setId(queryResult.getQuoteDetailId());
        result.setQuoteId(queryResult.getQuoteId());
        return result;
    }

    private MmQuote buildMmQuoteFromQueryResult(MmQuoteDetailsQueryResult queryResult) {
        MmQuote mmQuote = new MmQuote();
        BeanUtils.copyProperties(queryResult, mmQuote);
        mmQuote.setId(queryResult.getQuoteId());
        mmQuote.setLastUpdateTime(queryResult.getQuoteLastUpdateTime());
        return mmQuote;
    }

    private void retrieveTagsForQuotes(Map<String, MmQuote> quoteMap) {
        List<String> quoteIdList = new ArrayList<>();
        quoteIdList.addAll(quoteMap.keySet());
        List<MmQuoteTag> mmQuoteTags = quoteTagDao.queryMmQuoteTags(quoteIdList);
        for (MmQuoteTag quoteTag : mmQuoteTags) {
            String quoteId = quoteTag.getQuoteId();
            MmQuote mmQuote = quoteMap.get(quoteId);
            if (mmQuote.getTags() == null) {
                mmQuote.setTags(new HashSet<>());
            }
            mmQuote.getTags().add(quoteTag);
        }

    }

    /**
     * 将MmQuote列表按MmQuote.id为关键字放入Map，方便下一步计算使用
     */
    private Map<String, MmQuote> buildMapForMmQuotes(List<MmQuote> mmQuoteList) {
        Map<String, MmQuote> result = new HashMap<>();
        for (MmQuote mmQuote : mmQuoteList) {
            result.put(mmQuote.getId(), mmQuote);
        }
        return result;
    }


    @Override
    public List<MmQuote> queryMmQuoteMain(MmQuoteMainQueryParameters parameters, boolean withDetails, boolean withTags) {
        checkQuoteMainQueryParameterValid(parameters);
        List<MmQuote> mmQuotes = mmQuoteDao.queryMmQuoteMain(parameters);
        if (withDetails || withTags) {
            Map<String, MmQuote> quoteMap = buildMapForMmQuotes(mmQuotes);
            if (withDetails) {
                retrieveQuoteDetailsForQuote(quoteMap);
            }
            if (withTags) {
                retrieveTagsForQuotes(quoteMap);
            }
        }
        return mmQuotes;
    }

    private void checkQuoteMainQueryParameterValid(MmQuoteMainQueryParameters parameter) {
        if(parameter.isPaging()){
            Integer pageSize = parameter.getPageSize();
            if (!Utils.isPositiveInteger(pageSize.toString())) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "pageSize不合法");
            }
        }
    }

    private void retrieveQuoteDetailsForQuote(Map<String, MmQuote> quoteMap) {
        Set<String> quoteIds = quoteMap.keySet();
        List<String> parameter = new ArrayList<>();
        parameter.addAll(quoteIds);
        List<MmQuoteDetails> mmQuoteDetailsList = mmQuoteDetailsDao.queryQuoteDetails(parameter);
        for (MmQuoteDetails details : mmQuoteDetailsList) {
            String quoteId = details.getQuoteId();
            MmQuote mmQuote = quoteMap.get(quoteId);
            if (mmQuote.getMmQuoteDetails() == null) {
                mmQuote.setMmQuoteDetails(new ArrayList<>());
            }
            mmQuote.getMmQuoteDetails().add(details);
        }
    }

    @Override
    public List<MmQuote> queryMyValidQuotes(String userId,MethodType methodType) {
        MmQuoteDetailsQueryParameters parameters = new MmQuoteDetailsQueryParameters();
        parameters.setPaging(false);
        if(methodType==MethodType.SEF){
            parameters.setQuoteUserId(userId);
        }else{
            parameters.setQuoteOperatorId(userId);
        }
        parameters.setOnlyValidQuotes(true);
        return queryMmQuoteDetails(parameters, true);
    }

    @Override
    public List<MmQuote> queryValidQuotesByInstitutions(List<String> institutionIdList) {
        MmQuoteDetailsQueryParameters parameters = new MmQuoteDetailsQueryParameters();
        parameters.setPaging(false);
        parameters.setInstitutionIdList(institutionIdList);
        parameters.setOnlyValidQuotes(true);
        return queryMmQuoteDetails(parameters, true);
    }

    @Override
    public List<MmQuote> queryMmQuotesByIdList(List<String> quoteIdList) {
        MmQuoteDetailsQueryParameters parameters = new MmQuoteDetailsQueryParameters();
        parameters.setPaging(false);
        parameters.setActive(false);
        if (quoteIdList == null || quoteIdList.size() == 0) {
            return new ArrayList<>();
        }
        parameters.setQuoteIdList(quoteIdList);
        parameters.setPaging(false);
        return queryMmQuoteDetails(parameters, true);
    }

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false, notifyResolver = QuoteUsersCacheResolver.class)
    public Map<String, User> retrieveQuoteUsers() {
        List<String> usersIDList = mmQuoteDao.retrieveQuoteUsersID();
        Map<String, User> allUsers = userBaseService.retreiveAllUsersGroupByUserID();
        Map<String, User> result = new HashMap<>();
        if (!CollectionsUtil.isEmptyOrNullCollection(usersIDList)) {
            for (String userID : usersIDList) {
                User user = allUsers.get(userID);
                if (user != null) {
                    if (StringUtils.isEmpty(user.getDisplayNamePinYin())) {
                        user.setDisplayNamePinYin(Pinyin4jUtil.getPinYin(user.getDisplayName()));
                    }
                    if (StringUtils.isEmpty(user.getDisplayNamePY())) {
                        user.setDisplayNamePY(Pinyin4jUtil.getPinYinHeadChar(user.getDisplayName()));
                    }
                    result.put(user.getUserId(), user);
                }
            }
        }
        return result;
    }

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false, notifyResolver = QuoteInstitutionCacheResolver.class)
    public Map<String, Institution> retrieveQuoteInstitutions() {
        List<String> institutionsIDList = mmQuoteDao.retrieveQuoteInstitutionsID();
        Map<String, Institution> institutionMap = institutionService.retrieveInstitutionsById();
        Map<String, Institution> result = new HashMap<>();
        if (!CollectionsUtil.isEmptyOrNullCollection(institutionsIDList)) {
            for (String institutionID : institutionsIDList) {
                Institution institution = institutionMap.get(institutionID);
                if (institution != null) {
                    result.put(institution.getId(), institution);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, UserPreference> retrieveInstitutionUserMappingByUserID() {
        List<UserPreference> userPreference = userPreferenceDao.getInstitutionUserMapping();
        Map<String,UserPreference> map=new HashMap<>();
        if(userPreference!=null && userPreference.size()>0){
            for(UserPreference userPreferences:userPreference){
                map.put(userPreferences.getUserId(),userPreferences);
            }
        }
        return map;
    }

    @Override
    public Map<String, String> retrieveAllUserIdMappingByInstitutionId(String userId) {
        Map<String,String> map=new HashMap<>();
       if(getUserPreference(userId)!=null){
           String jsonMap=getUserPreference(userId).getPreferenceValue();
           map = Utils.jsonToObject(jsonMap);
       }
        return map;
    }

    private UserPreference getUserPreference(String userId){
        List<UserPreference> userPreference = userPreferenceDao.getInstitutionUserMapping();
        for(UserPreference preference: userPreference){
            if(preference.getUserId().equals(userId)){
                return preference;
            }
        }
        return null;
    }
}
