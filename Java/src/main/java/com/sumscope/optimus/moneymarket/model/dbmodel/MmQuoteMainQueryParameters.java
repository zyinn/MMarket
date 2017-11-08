package com.sumscope.optimus.moneymarket.model.dbmodel;

/**
 * Created by fan.bai on 2016/8/6.
 * 查询报价主表的查询参数
 */
public class MmQuoteMainQueryParameters extends AbstractMmQuoteQueryParameters {
    /**
     * 排序的期限的低值，不排序则设置为null，任一一个为null既不排序
     */
    private Integer orderByPeriodLow;
    /**
     * 排序的期限的高值，不排序则设置为null，任一一个为null既不排序
     */
    private Integer orderByPeriodHigh;

    /**
     * 排序规则，ASC：升序排序，DESC：降序排序
     */
    private String orderSeq;

    public Integer getOrderByPeriodLow() {
        return orderByPeriodLow;
    }

    public void setOrderByPeriodLow(Integer orderByPeriodLow) {
        this.orderByPeriodLow = orderByPeriodLow;
    }

    public Integer getOrderByPeriodHigh() {
        return orderByPeriodHigh;
    }

    public void setOrderByPeriodHigh(Integer orderByPeriodHigh) {
        this.orderByPeriodHigh = orderByPeriodHigh;
    }

    public String getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(String orderSeq) {
        this.orderSeq = orderSeq;
    }
}
