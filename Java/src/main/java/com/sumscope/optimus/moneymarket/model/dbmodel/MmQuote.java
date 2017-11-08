package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.moneymarket.commons.util.StringTrimUtil;
import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteSource;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MmQuote implements Serializable {
    /**
     * 报价单主表ID
     */
    private String id;

    /**
     * 报价类型
     */
    private QuoteType quoteType;

    /**
     * 报价机构ID
     */
    private String institutionId;

    /**
     * 方向
     */
    private Direction direction;

    /**
     * 报价人ID
     */
    private String quoteUserId;

    /**
     * 备注
     */
    private String memo;

    /**
     * 决定是QQ报价还是QB报价
     */
    private QuoteSource source;

    /**
     * 报价单创建日期时间
     */
    private Date createTime;

    /**
     * 过期日期，时间段应为23:00:00
     */
    private Date expiredDate;

    /**
     * 报价主表的最后更新日期。该日期根据最后依次修改整个报价单设置。由service层控制
     */
    private Date lastUpdateTime;

    /**
     * 报价方法，由何种方式进行的报价
     */
    private MethodType methodType;

    /**
     * 序列号，必须由前台设置
     */
    private int sequence;

    /**
     * 机构属性信息，反范式设计，用于查询优化
     */
    private String bankNature;

    /**
     * 省份信息，反范式设计，用于查询优化
     */
    private String province;

    /**
     * 机构托管
     */
    private boolean custodianQualification;

    /**
     * 公司规模
     */
    private BigDecimal fundSize;

    /**
     * 报价明细信息，从数据库读取时该字段无法直接获取，需要调用其他的Dao方法进行设置
     */
    private List<MmQuoteDetails> mmQuoteDetails;

    /**
     * 报价标签，从数据库读取时该字段无法直接获取，需要调用其他的Dao方法进行设置
     */
    private Set<MmQuoteTag> tags;

    private String quoteOperatorId;//机构联系人

    public boolean getCustodianQualification() {
        return custodianQualification;
    }

    public void setCustodianQualification(boolean custodianQualification) {
        this.custodianQualification = custodianQualification;
    }

    public Date getExpiredDate() {
        if (this.expiredDate != null) {
            return new Date(this.expiredDate.getTime());
        }
        return null;
    }

    public void setExpiredDate(Date expiredDate) {
            this.expiredDate = expiredDate;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getBankNature() {
        return bankNature;
    }

    public void setBankNature(String bankNature) {
        this.bankNature = bankNature;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = StringTrimUtil.getTrimedString(id);
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

    public String getQuoteUserId() {
        return quoteUserId;
    }

    public void setQuoteUserId(String quoteUserId) {
        this.quoteUserId = StringTrimUtil.getTrimedString(quoteUserId);
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = StringTrimUtil.getTrimedString(memo);
    }

    public QuoteSource getSource() {
        return source;
    }

    public void setSource(QuoteSource source) {
        this.source = source;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public Date getCreateTime() {
        if(createTime==null){
            return null;
        }
            return (Date)createTime.clone();
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setCreateTime(Date createTime) {
        if(createTime==null){
            this.createTime =null;
        }else{
            this.createTime =(Date)createTime.clone();
        }
    }

    /**
     * 判断当前报价单与传入的报价单是否数值一致。该方法比较客户端传入报价单与当前数据库报价单来决定是否需要进行数据库更新
     * 操作。用于比较的字段也是可以进行更新的字段。本方法不比较报价明细及标签。也不比较createTime。
     * 本方法使用自动生成equals方法产生。
     * @param o MmQuote
     * @return true, 两者数值一致，无需更新。false，数值不一致，需要更新。
     */
    public boolean equalsForUpdate(MmQuote o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MmQuote mmQuote = (MmQuote) o;

        if (direction != mmQuote.direction) return false;
        if (memo != null ? !memo.equals(mmQuote.memo) : mmQuote.memo != null) return false;
        if( expiredDate != null && mmQuote.expiredDate != null){
            //对过期日期仅比较日期不比较时间
            Calendar c1 = Calendar.getInstance();
            c1.setTime(expiredDate);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(mmQuote.expiredDate);
            return c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR) == 0;
        }else if(expiredDate == null && mmQuote.expiredDate == null){
            return true;
        }else{
            return false;
        }

    }


    @Override
    public String toString() {
        return "MmQuote{" +
                "id='" + id + '\'' +
                ", quoteType=" + quoteType +
                ", direction=" + direction +
                ", quoteUserId='" + quoteUserId + '\'' +
                ", memo='" + memo + '\'' +
                ", source='" + source + '\'' +
                ", createTime=" + createTime +
                ", mmQuoteDetails=" + mmQuoteDetails +
                '}';
    }

    public void setMmQuoteDetails(List<MmQuoteDetails> mmQuoteDetails) {
        this.mmQuoteDetails = mmQuoteDetails;
    }

    public List<MmQuoteDetails> getMmQuoteDetails() {
        return mmQuoteDetails;
    }

    public BigDecimal getFundSize() {
        return fundSize;
    }

    public void setFundSize(BigDecimal fundSize) {
        this.fundSize = fundSize;
    }

    public Set<MmQuoteTag> getTags() {
        return tags;
    }

    public void setTags(Set<MmQuoteTag> tags) {
        this.tags = tags;
    }

    public String getQuoteOperatorId() {
        return quoteOperatorId;
    }

    public void setQuoteOperatorId(String quoteOperatorId) {
        this.quoteOperatorId = quoteOperatorId;
    }
}