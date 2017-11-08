package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.moneymarket.commons.util.StringTrimUtil;
import com.sumscope.optimus.moneymarket.commons.util.BigDecimalUtils;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class MmQuoteDetails implements Serializable {
    private String id;

    private String quoteId;

    private BigDecimal price;

    private BigDecimal priceLow;

    private BigDecimal quantity;

    private BigDecimal quantityLow;

    private Date lastUpdateTime;

    private Integer daysLow;

    private Integer daysHigh;

    private  QuoteTimePeriod limitType;

    private boolean active;

    private String qqMessage;

    public QuoteTimePeriod getLimitType() {
        return limitType;
    }

    public void setLimitType(QuoteTimePeriod limitType) {
        this.limitType = limitType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = StringTrimUtil.getTrimedString(id);
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = StringTrimUtil.getTrimedString(quoteId);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Date getLastUpdateTime() {
        if (lastUpdateTime == null) {
            return null;
        }
        return (Date) lastUpdateTime.clone();
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        if (lastUpdateTime == null) {
            this.lastUpdateTime = null;
        } else {
            this.lastUpdateTime = (Date) lastUpdateTime.clone();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public BigDecimal getPriceLow() {
        return priceLow;
    }

    public void setPriceLow(BigDecimal priceLow) {
        this.priceLow = priceLow;
    }

    public BigDecimal getQuantityLow() {
        return quantityLow;
    }

    public void setQuantityLow(BigDecimal quantityLow) {
        this.quantityLow = quantityLow;
    }

    public Integer getDaysLow() {
        return daysLow;
    }

    public void setDaysLow(Integer daysLow) {
        this.daysLow = daysLow;
    }

    public Integer getDaysHigh() {
        return daysHigh;
    }

    public void setDaysHigh(Integer daysHigh) {
        this.daysHigh = daysHigh;
    }

    public String getQqMessage() {
        return qqMessage;
    }

    public void setQqMessage(String qqMessage) {
        this.qqMessage = qqMessage;
    }

    @Override
    public String toString() {
        return "MmQuoteDetails{" +
                "id='" + id + '\'' +
                ", quoteId='" + quoteId + '\'' +
                ", price=" + price +
                ", priceLow=" + priceLow +
                ", quantity=" + quantity +
                ", quantityLow=" + quantityLow +
                ", lastUpdateTime=" + lastUpdateTime +
                ", daysLow=" + daysLow +
                ", daysHigh=" + daysHigh +
                ", active=" + active +
                ", qqMessage=" + qqMessage +
                '}';
    }

    /**
     * 判断当前报价明细与传入的报价明细是否数值一致。该方法比较客户端传入报价明细与当前数据库报价明细来决定是否需要进行数据库更新
     * 操作。用于比较的字段也是可以进行更新的字段。本方法不比较业务主键，ID以及lastUpdateTime
     *
     * @param details MmQuoteDetails
     * @return true, 两者数值一致，无需更新。false，数值不一致，需要更新。
     */
    public boolean equalsForUpdate(MmQuoteDetails details) {
        if (this == details) return true;
        if(!BigDecimalUtils.equalsDoubleValue(price, details.getPrice())) return false;
        if(!BigDecimalUtils.equalsDoubleValue(priceLow, details.getPriceLow())) return false;
        if(!BigDecimalUtils.equalsDoubleValue(quantity, details.getQuantity())) return false;
        if(!BigDecimalUtils.equalsDoubleValue(quantityLow, details.getQuantityLow())) return false;
        if(active != details.active ) return false;
        return qqMessage != null ? qqMessage.equals(details.qqMessage) : details.qqMessage == null;
    }

    /**
     * 根据业务主键(QuoteID除外)比较两个明细数据是否相等。由于不包括QuoteID，所以应保证两个明细数据来自同一个报价主表
     */
    public boolean equalsForBusinessKeysExceptQuoteID(MmQuoteDetails details) {
        if (this == details) return true;
        if (daysLow != null ? !daysLow.equals(details.daysLow) : details.daysLow != null) return false;
        return daysHigh != null ? daysHigh.equals(details.daysHigh) : details.daysHigh == null;
    }

}