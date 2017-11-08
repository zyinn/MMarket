package com.sumscope.optimus.moneymarket.model.dbmodel;

/**
 * 用于按明细查询报价单的参数列表。
 */
public class MmQuoteDetailsQueryParameters extends AbstractMmQuoteQueryParameters {
    /**
     * 是否仅查询有效的明细数据
     */
    private boolean active = true;
    /**
     * 查询限定日期期限的低值
     */
    private int daysLow  = -1;
    /**
     * 查询限定日期期限的高值
     */
    private int daysHigh = -1;

    public int getDaysLow() {
        return daysLow;
    }

    public void setDaysLow(int daysLow) {
        this.daysLow = daysLow;
    }

    public int getDaysHigh() {
        return daysHigh;
    }

    public void setDaysHigh(int daysHigh) {
        this.daysHigh = daysHigh;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
