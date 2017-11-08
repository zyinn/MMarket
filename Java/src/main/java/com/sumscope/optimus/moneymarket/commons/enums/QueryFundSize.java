package com.sumscope.optimus.moneymarket.commons.enums;

import java.math.BigDecimal;

/**
 * Created by fan.bai on 2016/8/4.
 * 用于查询参数的银行规模枚举。
 */
public enum QueryFundSize {
    /**
     * <100亿
     */
    SMALLER_ONE_H("<100亿",0,1000000),
    /**
     * 100-200亿
     */
    SMALLER_TWO_H("100-200亿",1000000,2000000),
    /**
     * 200-500亿
     */
    TWO_FIVE_H("200-500亿",2000000,5000000),
    /**
     * 500亿-1千亿
     */
    FIVE_H_ONE_T("500-1000亿",5000000,10000000),
    /**
     * 1-2千亿
     */
    ONE_TWO_T("1000-2000亿",10000000,20000000),
    /**
     * 2-5千亿
     */
    TWO_FIVE_T("2000-5000亿",20000000,50000000),
    /**
     * >5千亿
     */
    LARGER_FIVE_T(">5000亿",50000000,Long.MAX_VALUE);

    QueryFundSize(String name,long low, long high){
        this.fundSizeHigh = high;
        this.fundSizeLow = low;
        this.displayName = name;
    }

    private String displayName;

    private long fundSizeLow;

    private long fundSizeHigh;

    public long getFundSizeLow() {
        return fundSizeLow;
    }

    public void setFundSizeLow(long fundSizeLow) {
        this.fundSizeLow = fundSizeLow;
    }

    public long getFundSizeHigh() {
        return fundSizeHigh;
    }

    public BigDecimal getFundSizeHighInBigDecimal(){
        return new BigDecimal(fundSizeHigh);
    }

    public BigDecimal getFundSizeLowInBigDecimal(){
        return new BigDecimal(fundSizeLow);
    }

    public void setFundSizeHigh(long fundSizeHigh) {
        this.fundSizeHigh = fundSizeHigh;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
