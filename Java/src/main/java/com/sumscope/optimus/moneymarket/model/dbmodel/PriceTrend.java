package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.moneymarket.commons.enums.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by fan.bai on 2016/9/19.
 * 矩阵计算结果，对应于矩阵的一个cell数值
 * 等同于报价单价格极限值数据
 */
public class PriceTrend {
    /**
     * 收益率极限值记录ID
     */
    private String id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
