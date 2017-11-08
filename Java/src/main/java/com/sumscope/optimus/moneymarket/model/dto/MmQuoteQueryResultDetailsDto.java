package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by fan.bai on 2016/8/3.
 * 报价列表详细数据Dto。该dto记录一条报价对应的报价明细信息，包括报价期限以及价格的上下值
 */
public class MmQuoteQueryResultDetailsDto {
    /**
     * 期限，例如1M,2M,3M等
     */
    private QuoteTimePeriod quoteTimePeriod;

    /**
     * 价格显示字符串，例如：2.34 - 2.45, 2.35, 2.3456
     */
    private String priceDisplayString;

    /**
     * 是否包含多项价格信息
     */
    private boolean multipeRecords;

    /**
     * 用于前端下拉显示的价格明细信息
     */
    private List<MmQuoteQueryPriceDetailsDto> priceDetails;

    public QuoteTimePeriod getQuoteTimePeriod() {
        return quoteTimePeriod;
    }

    public void setQuoteTimePeriod(QuoteTimePeriod quoteTimePeriod) {
        this.quoteTimePeriod = quoteTimePeriod;
    }

    public boolean isMultipeRecords() {
        return multipeRecords;
    }

    public void setMultipeRecords(boolean multipeRecords) {
        this.multipeRecords = multipeRecords;
    }

    public List<MmQuoteQueryPriceDetailsDto> getPriceDetails() {
        return priceDetails;
    }

    public void setPriceDetails(List<MmQuoteQueryPriceDetailsDto> priceDetails) {
        this.priceDetails = priceDetails;
    }

    public String getPriceDisplayString() {
        return priceDisplayString;
    }

    public void setPriceDisplayString(String priceDisplayString) {
        this.priceDisplayString = priceDisplayString;
    }
}
