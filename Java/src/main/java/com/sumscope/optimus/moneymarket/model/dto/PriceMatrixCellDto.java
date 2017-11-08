package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;

import java.math.BigDecimal;

/**
 * Created by fan.bai on 2016/9/19.
 * 矩阵结果某一行的某一个cell的结果dto，用于前端矩阵结果显示
 */
public class PriceMatrixCellDto {
    /**
     * 报价期限枚举
     */
    private QuoteTimePeriod timePeriod;
    /**
     * 报价低值 - 不包含null的价格
     */
    private BigDecimal priceLow;
    /**
     * 报价高值 - 不包含null的价格
     */
    private BigDecimal priceHigh;

    public QuoteTimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(QuoteTimePeriod timePeriod) {
        this.timePeriod = timePeriod;
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
