package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by fan.bai on 2016/8/30.
 * 用于历史数据库的报价单管理服务，主要用于数据的归档
 */
public interface HistoryMmQuoteManagementService extends MmQuoteManagementService {
    @Override
    @Transactional(value = Constant.HISTORY_TRANSACTION_MANAGER)
    void setupMmQuotesInTransaction(List<MmQuote> mmQuoteList, Source source);
}
