package com.sumscope.optimus.moneymarket.model.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by fan.bai on 2016/9/22.
 * 报价趋势查询结果Dto，用于前端绘制价格趋势图形。
 */
public class PriceTrendDto {
    /**
     * 报价低值 - 不包含null的价格
     */
    private BigDecimal priceLow;
    /**
     * 报价高值 - 不包含null的价格
     */
    private BigDecimal priceHigh;
    /**
     * 创建日期
     */
    private Date createTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
