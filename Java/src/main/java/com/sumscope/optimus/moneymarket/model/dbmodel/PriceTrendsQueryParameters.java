package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.moneymarket.commons.enums.*;

import java.util.Date;

/**
 * Created by fan.bai on 2016/9/22.
 * 用于查询报价价格极值的参数模型
 */
public class PriceTrendsQueryParameters {
    /**
     * 方向
     */
    private Direction direction;
    /**
     * 报价类型
     */
    private QuoteType quoteType;
    /**
     * 用于计算的机构类型枚举
     */
    private CalculatedBankNature matrixBankNature;
    /**
     * 用于计算的机构规模枚举
     */
    private MatrixFundSize matrixFundSize;
    /**
     * 报价期限枚举
     */
    private QuoteTimePeriod timePeriod;
    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 查询按createdTime为基准提前多少天的记录，例如提前30天（一个月）的数据，默认是60天。
     */
    private Integer numberOfPreviousDays;

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public QuoteType getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(QuoteType quoteType) {
        this.quoteType = quoteType;
    }

    public CalculatedBankNature getMatrixBankNature() {
        return matrixBankNature;
    }

    public void setMatrixBankNature(CalculatedBankNature matrixBankNature) {
        this.matrixBankNature = matrixBankNature;
    }

    public MatrixFundSize getMatrixFundSize() {
        return matrixFundSize;
    }

    public void setMatrixFundSize(MatrixFundSize matrixFundSize) {
        this.matrixFundSize = matrixFundSize;
    }

    public QuoteTimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(QuoteTimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getNumberOfPreviousDays() {
        return numberOfPreviousDays;
    }

    public void setNumberOfPreviousDays(Integer numberOfPreviousDays) {
        this.numberOfPreviousDays = numberOfPreviousDays;
    }
}
