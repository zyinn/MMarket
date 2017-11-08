package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by fan.bai on 2016/8/4.
 * 用于矩阵计算及显示的银行规模枚举
 */
public enum MatrixFundSize {
    /**
     * >5千亿
     */
    LARGER_FIVE_T(">5000亿",50000001,Long.MAX_VALUE),
    /**
     * 1-5千亿
     */
    ONE_FIVE_T("1000-5000亿",10000001,50000000),
    /**
     * <1千亿
     */
    SMALLER_ONE_T("<1000亿",0,10000000),

    NONE("Long.MIN_VALUE-Long.MAX_VALUE",Long.MIN_VALUE,Long.MAX_VALUE);
    MatrixFundSize(String name,long low, long high){
        this.fundSizeHigh = high;
        this.fundSizeLow = low;
        this.displayName = name;
    }

    private String displayName;

    private long fundSizeLow;

    private long fundSizeHigh;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getFundSizeLow() {
        return fundSizeLow;
    }

    public void setFundSizeLow(long fundSizeLow) {
        this.fundSizeLow = fundSizeLow;
    }

    public long getFundSizeHigh() {
        return fundSizeHigh;
    }

    public void setFundSizeHigh(long fundSizeHigh) {
        this.fundSizeHigh = fundSizeHigh;
    }
}
