package com.sumscope.optimus.moneymarket.model.dto;

import java.math.BigDecimal;

/**
 * Created by fan.bai on 2016/8/4.
 * 当一个期限有多个价格需要显示时，使用该dto记录各详细价格信息
 */
public class MmQuoteQueryPriceDetailsDto {
    private String quoteDetailsID;
    /**
     * 用于前端显示的关于期限的字符串，例如3M，100D
     */
    private String periodDisplayValue;
    /**
     * 用于前端显示的价格信息字符串，例如2.45%, 2.34%-2.78%
     */
    private String priceDisplayValue;
    /**
     * 用于前端显示的数量信息字符串，例如300万，30.3亿
     */
    private String quantityDisplayValue;

    private BigDecimal priceLow;

    private BigDecimal priceHigh;

    public String getPeriodDisplayValue() {
        return periodDisplayValue;
    }

    public void setPeriodDisplayValue(String periodDisplayValue) {
        this.periodDisplayValue = periodDisplayValue;
    }

    public String getPriceDisplayValue() {
        return priceDisplayValue;
    }

    public void setPriceDisplayValue(String priceDisplayValue) {
        this.priceDisplayValue = priceDisplayValue;
    }

    public String getQuantityDisplayValue() {
        return quantityDisplayValue;
    }

    public void setQuantityDisplayValue(String quantityDisplayValue) {
        this.quantityDisplayValue = quantityDisplayValue;
    }

    public String getQuoteDetailsID() {
        return quoteDetailsID;
    }

    public void setQuoteDetailsID(String quoteDetailsID) {
        this.quoteDetailsID = quoteDetailsID;
    }

    public BigDecimal getPriceLow() {
        return priceLow;
    }

    public void setPriceLow(BigDecimal priceLow) {
        this.priceLow = priceLow;
    }

    public BigDecimal getPriceHigh() {
        return priceHigh;
    }

    public void setPriceHigh(BigDecimal priceHigh) {
        this.priceHigh = priceHigh;
    }
}
