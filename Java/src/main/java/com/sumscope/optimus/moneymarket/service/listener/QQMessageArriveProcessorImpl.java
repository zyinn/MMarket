package com.sumscope.optimus.moneymarket.service.listener;

import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.service.BusinessMmQuoteManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan.bai on 2016/5/7.
 * QQMessageArriveProcessor 的实现类
 */
@Service
public class QQMessageArriveProcessorImpl implements QQMessageArriveProcessor {

    @Autowired
    private BusinessMmQuoteManagementService quoteManageService;


    @Override
    public void processQQQuoteInTransaction(MmQuote mmQuote) {
        writeToDB(mmQuote);
    }

    private void writeToDB(MmQuote mmQuote) {
        if (mmQuote != null) {
            List<MmQuote> quotes = new ArrayList<>();
            quotes.add(mmQuote);
            quoteManageService.setupMmQuotesInTransaction(quotes, BusinessMmQuoteManagementService.Source.QQ);
        }
    }

}
