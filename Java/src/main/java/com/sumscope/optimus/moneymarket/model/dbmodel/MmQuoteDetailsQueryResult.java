package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteSource;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by fan.bai on 2016/5/12.
 * 报价单主表以及明细表SQL查询结果数据结构
 */
public class MmQuoteDetailsQueryResult implements Serializable {

    private String quoteId;

    private String quoteDetailId;

    private String institutionId;

    private MethodType methodType;

    private String quoteUserId;

    // quoteInfo
    private QuoteType quoteType;

    private Direction direction;

    private Date expiredDate;

    private Date createTime;

    private Date quoteLastUpdateTime;

    // 期限区间
    private Integer daysLow;
    private Integer daysHigh;

    // 数量区间
    private BigDecimal quantityLow;
    private BigDecimal quantity;

    // 价格区间
    private BigDecimal priceLow;
    private BigDecimal price;

    private String memo;

    private QuoteSource source;
    
    private boolean active;

    private Date lastUpdateTime;

    private String qqMessage;

    private int sequence;

    //查询条件

    private String province;

    private String bankNature;

    private String custodianQualification;

    private BigDecimal fundSize;//机构规模

    private String quoteOperatorId;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteDetailId() {
        return quoteDetailId;
    }

    public void setQuoteDetailId(String quoteDetailId) {
        this.quoteDetailId = quoteDetailId;
    }

    public String getQuoteUserId() {
        return quoteUserId;
    }

    public void setQuoteUserId(String quoteUserId) {
        this.quoteUserId = quoteUserId;
    }

    public QuoteType getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(QuoteType quoteType) {
        this.quoteType = quoteType;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
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

    public BigDecimal getQuantityLow() {
        return quantityLow;
    }

    public void setQuantityLow(BigDecimal quantityLow) {
        this.quantityLow = quantityLow;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceLow() {
        return priceLow;
    }

    public void setPriceLow(BigDecimal priceLow) {
        this.priceLow = priceLow;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public QuoteSource getSource() {
        return source;
    }

    public void setSource(QuoteSource source) {
        this.source = source;
    }

    public Date getLastUpdateTime() {
        if(lastUpdateTime==null){
            return null;
        }
        return (Date)lastUpdateTime.clone();
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        if(lastUpdateTime==null){
            this.lastUpdateTime = null;
        }else{
            this.lastUpdateTime = (Date)lastUpdateTime.clone();
        }
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

	public String getQqMessage() {
		return qqMessage;
	}

	public void setQqMessage(String qqMessage) {
		this.qqMessage = qqMessage;
	}

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    public Date getExpiredDate() {
        if (this.expiredDate!=null) {
            return new Date(this.expiredDate.getTime());
        }
        return null;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getBankNature() {
        return bankNature;
    }

    public void setBankNature(String bankNature) {
        this.bankNature = bankNature;
    }

    public String getCustodianQualification() {
        return custodianQualification;
    }

    public void setCustodianQualification(String custodianQualification) {
        this.custodianQualification = custodianQualification;
    }

    public void setExpiredDate(Date expiredDate) {
            this.expiredDate = expiredDate;
    }

    public BigDecimal getFundSize() {
        return fundSize;
    }

    public void setFundSize(BigDecimal fundSize) {
        this.fundSize = fundSize;
    }

    public Date getQuoteLastUpdateTime() {
        return quoteLastUpdateTime;
    }

    public void setQuoteLastUpdateTime(Date quoteLastUpdateTime) {
        this.quoteLastUpdateTime = quoteLastUpdateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getQuoteOperatorId() {
        return quoteOperatorId;
    }

    public void setQuoteOperatorId(String quoteOperatorId) {
        this.quoteOperatorId = quoteOperatorId;
    }
}
