package com.sumscope.optimus.moneymarket.model.dbmodel;

import java.math.BigDecimal;

/**
 * Created by fan.bai on 2016/8/4.
 * 查询报价时用于限定机构规模的数据，例如可查询规模在200-500亿的机构报价单
 */
public class MmQuoteQueryFundSizePeriod {
    /**
     * 查询机构规模低值
     */
    private BigDecimal fundSizeLow;
    /**
     * 查询机构规模高值
     */
    private BigDecimal fundSizeHigh;

    public BigDecimal getFundSizeLow() {
        return fundSizeLow;
    }

    public void setFundSizeLow(BigDecimal fundSizeLow) {
        this.fundSizeLow = fundSizeLow;
    }

    public BigDecimal getFundSizeHigh() {
        return fundSizeHigh;
    }

    public void setFundSizeHigh(BigDecimal fundSizeHigh) {
        this.fundSizeHigh = fundSizeHigh;
    }
}
