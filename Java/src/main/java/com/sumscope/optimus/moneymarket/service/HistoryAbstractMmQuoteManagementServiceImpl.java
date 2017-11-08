package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by fan.bai on 2016/8/30.
 */
@Component
public class HistoryAbstractMmQuoteManagementServiceImpl extends AbstractMmQuoteManagementServiceImpl implements HistoryMmQuoteManagementService {
    @Autowired
    private HistoryMmQuoteDao mmQuoteDao;

    @Autowired
    private HistoryMmQuoteDetailsDao mmQuoteDetailsDao;

    @Autowired
    private HistoryMmQuoteTagDao mmQuoteTagDao;

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
        return false;
    }

    @Override
    protected MmQuoteTagDao getQuoteTagDao() {
        return mmQuoteTagDao;
    }
}
