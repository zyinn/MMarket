package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/7/27.
 */
public class MobileMatrixResult {
    /**
     * 期限
     *
     */
    private QuoteTimePeriod timePeriod;

    /**
     * 小于一千亿规模机构价格低值
     */
    private BigDecimal smallerThenOneTL;

    /**
     * 一千亿-五千亿规模机构价格低值
     */
    private BigDecimal betweenOneTFiveTL;

    /**
     * 大于五千亿规模机构价格低值
     */
    private BigDecimal largerThenFiveTL;

    /**
     * 小于一千亿规模机构价格高值
     */
    private BigDecimal smallerThenOneTH;

    /**
     * 一千亿-五千亿规模机构价格高值
     */
    private BigDecimal betweenOneTFiveTH;

    /**
     * 大于五千亿规模机构价格高值
     */
    private BigDecimal largerThenFiveTH;

    public QuoteTimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(QuoteTimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public BigDecimal getSmallerThenOneTL() {
        return smallerThenOneTL;
    }

    public void setSmallerThenOneTL(BigDecimal smallerThenOneTL) {
        this.smallerThenOneTL = smallerThenOneTL;
    }

    public BigDecimal getBetweenOneTFiveTL() {
        return betweenOneTFiveTL;
    }

    public void setBetweenOneTFiveTL(BigDecimal betweenOneTFiveTL) {
        this.betweenOneTFiveTL = betweenOneTFiveTL;
    }

    public BigDecimal getLargerThenFiveTL() {
        return largerThenFiveTL;
    }

    public void setLargerThenFiveTL(BigDecimal largerThenFiveTL) {
        this.largerThenFiveTL = largerThenFiveTL;
    }

    public BigDecimal getSmallerThenOneTH() {
        return smallerThenOneTH;
    }

    public void setSmallerThenOneTH(BigDecimal smallerThenOneTH) {
        this.smallerThenOneTH = smallerThenOneTH;
    }

    public BigDecimal getBetweenOneTFiveTH() {
        return betweenOneTFiveTH;
    }

    public void setBetweenOneTFiveTH(BigDecimal betweenOneTFiveTH) {
        this.betweenOneTFiveTH = betweenOneTFiveTH;
    }

    public BigDecimal getLargerThenFiveTH() {
        return largerThenFiveTH;
    }

    public void setLargerThenFiveTH(BigDecimal largerThenFiveTH) {
        this.largerThenFiveTH = largerThenFiveTH;
    }
}
