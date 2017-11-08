package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.util.QuoteDateUtils;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteDetailsQueryParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fan.bai on 2016/5/10.
 * 定时导出数据库数据服务
 */
@Service
public class DatabaseManagementService {
    @Autowired
    private MmQuoteQueryService mmQuoteQueryService;

    @Autowired
    private BusinessMmQuoteManagementService businessMmQuoteManagementService;

    @Autowired
    private HistoryMmQuoteManagementService historyMmQuoteManagementService;

    @Autowired
    private MatrixCalcuationAndQueryService matrixCalcuationAndQueryService;

    void takeSnapshotForPriceMatrix(){
        LogManager.info("记录本日报价价格趋势。");
        matrixCalcuationAndQueryService.takeSnapshotForMatrix(QuoteDateUtils.getMatrixCalculationTimeOfDate(new Date()));
    }


    void archiveExpiredData() {
        LogManager.info("开始数据归档操作。");
        MmQuoteDetailsQueryParameters parameters = new MmQuoteDetailsQueryParameters();
        parameters.setOnlyExpiredQuotes(true);
        parameters.setActive(false);
        List<MmQuote> mmQuotes = mmQuoteQueryService.queryMmQuoteDetails(parameters, true);
        if (mmQuotes != null) {
            LogManager.info("归档操作获取" + mmQuotes.size() + "条需要归档的报价单。");
        }
        List<String> successList = new ArrayList<>();
        if (mmQuotes != null && mmQuotes.size() > 0) {
            for (MmQuote mmQuote : mmQuotes) {
                List<MmQuote> quoteList = new ArrayList<>();
                quoteList.add(mmQuote);
                try {
                    historyMmQuoteManagementService.setupMmQuotesInTransaction(quoteList, MmQuoteManagementService.Source.ARCHIVE);
                    successList.add(mmQuote.getId());
                } catch (Exception e) {
                    LogManager.error("归档操作遭遇错误: \r\n" +
                            "报价单ID:" + mmQuote.getId() + "\r\n " +
                            e.getLocalizedMessage()
                    );
                }
            }
            businessMmQuoteManagementService.deleteQuotesFromDB(successList);
            LogManager.info("归档操作结束，成功将" + successList.size() + "条报价单归档。");
        }
    }

    void deactivateExpiredMmQuoteDetails() {
        LogManager.info("报价明细按日期进行过期作废。");
        businessMmQuoteManagementService.deactivateExpiredMmQuoteDetails();
    }


}
