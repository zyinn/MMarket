package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.dao.*;
import com.sumscope.optimus.moneymarket.model.dbmodel.UserPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by fan.bai on 2016/8/30.
 */
@Component
public class BusinessAbstractMmQuoteManagementServiceImpl extends AbstractMmQuoteManagementServiceImpl implements BusinessMmQuoteManagementService {
    @Autowired
    private BusinessMmQuoteDao mmQuoteDao;

    @Autowired
    private BusinessMmQuoteDetailsDao mmQuoteDetailsDao;

    @Autowired
    private BusinessMmQuoteTagDao mmQuoteTagDao;

    @Autowired
    private UserPreferenceDao userPreferenceDao;

    @Override
    protected MmQuoteDao getMmQuoteDao() {
        return mmQuoteDao;
    }

    @Override
    protected MmQuoteDetailsDao getMmQuoteDetailsDao() {
        return mmQuoteDetailsDao;
    }

    @Override
    protected boolean isPublishNeeded() {
        return true;
    }

    @Override
    protected MmQuoteTagDao getQuoteTagDao() {
        return mmQuoteTagDao;
    }

    @Override
    public void deleteQuotesFromDB(List<String> quoteIdList) {
        mmQuoteDao.deleteDataFromDB(quoteIdList);
        mmQuoteDetailsDao.deleteDataFromDB(quoteIdList);
        mmQuoteTagDao.deleteTagsFromQuotes(quoteIdList);
    }

    @Override
    public void deactivateExpiredMmQuoteDetails() {
        mmQuoteDetailsDao.deactivateExpiredMmQuoteDetails();
    }


    @Override
    public void setupInstitutionUserMappingTransaction(List<UserPreference> list) {
        userPreferenceDao.insertInstitutionUserMapping(list);
    }

    @Override
    public void updateInstitutionUserMapping(List<UserPreference> userPreferences) {
        if(userPreferences!=null && userPreferences.size()>0){
            userPreferenceDao.updateInstitutionUserMapping(userPreferences);
        }
    }

}
