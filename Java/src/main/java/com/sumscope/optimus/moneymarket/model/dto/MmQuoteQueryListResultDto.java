package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.QueryFundSize;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteSource;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;

import java.util.Date;
import java.util.List;

/**
 * Created by fan.bai on 2016/8/3.
 * 用于前端显示报价列表的dto。该Dto包含了所有需要在列表中显示一行记录的数据。
 */
public class MmQuoteQueryListResultDto {
    /**
     * 报价主表ID,用于功能调用，不用于显示
     */
    private String quoteId;

    /**
     * 报价源，QB报价还有PRIME_QB方式表明是精品机构的报价信息
     */
    private QuoteSource quoteSource;
    /**
     * 机构名称，仅用于精品报价列表
     */
    private String primeListInstitutionName;

    /**
     * 联系人列表，仅用于精品报价列表
     */
    private List<QuoteUserInfoDto> primeListContacts;
    /**
     * 联系方式，精品机构只有一个电话号码，仅用于精品报价列表
     */
    private String primeListInstitutionTelephone;
    /**
     * 报价方向
     */
    private Direction direction;

    /**
     * 报价类型
     */
    private QuoteType quoteType;

    /**
     * 报价价格明细列表
     */
    private List<MmQuoteQueryResultDetailsDto> quoteDetails;

    /**
     * 备注
     */
    private String memo;
    /**
     * 最后更新日期
     */
    private Date lastUpdateTime;

    /**
     * 报价方显示。当QQ报价时显示QQ昵称，QB报价显示报价机构名称-报价人名，PRIME_QB报价显示精品机构名称
     * 该属性针对市场报价列表显示
     */
    private String marketListQuoter;

    /**
     * 联系方式显示。当QQ报价时显示QQ号码，QB报价显示报价人电话，多个电话用“，”分隔，PRIME_QB报价显示精品机构电话。
     * 该属性针对市场报价列表显示
     */
    private String marketListQuoterContacts;

    /**
     * 过期日期，时间段应为23:00:00, 仅用于是否作废判断，不进行显示
     */
    private Date expiredDate;

    /**
     * 机构ID，仅对于QB报价有效, 传给前端用于参数判断以及功能调用。不用于显示
     */
    private String institutionId;

    /**
     * 报价人ID，传给前端用于参数判断以及功能调用。不用于显示
     */
    private String quoteUserId;

    /**
     * 机构属性信息，传给前端用于参数判断以及功能调用。不用于显示
     */
    private String bankNature;

    /**
     * 省份信息，传给前端用于参数判断以及功能调用。不用于显示
     */
    private String province;

    /**
     * 机构托管,传给前端用于参数判断以及功能调用。不用于显示
     */
    private boolean custodianQualification;

    /**
     * 公司规模,传给前端用于参数判断以及功能调用。不用于显示
     */
    private QueryFundSize fundSizeEnum;

    private String quoteOperatorId;


    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public QuoteSource getQuoteSource() {
        return quoteSource;
    }

    public void setQuoteSource(QuoteSource quoteSource) {
        this.quoteSource = quoteSource;
    }

    public String getPrimeListInstitutionName() {
        return primeListInstitutionName;
    }

    public void setPrimeListInstitutionName(String primeListInstitutionName) {
        this.primeListInstitutionName = primeListInstitutionName;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public List<QuoteUserInfoDto> getPrimeListContacts() {
        return primeListContacts;
    }

    public void setPrimeListContacts(List<QuoteUserInfoDto> primeListContacts) {
        this.primeListContacts = primeListContacts;
    }

    public String getPrimeListInstitutionTelephone() {
        return primeListInstitutionTelephone;
    }

    public void setPrimeListInstitutionTelephone(String primeListInstitutionTelephone) {
        this.primeListInstitutionTelephone = primeListInstitutionTelephone;
    }

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

    public List<MmQuoteQueryResultDetailsDto> getQuoteDetails() {
        return quoteDetails;
    }

    public void setQuoteDetails(List<MmQuoteQueryResultDetailsDto> quoteDetails) {
        this.quoteDetails = quoteDetails;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getMarketListQuoter() {
        return marketListQuoter;
    }

    public void setMarketListQuoter(String marketListQuoter) {
        this.marketListQuoter = marketListQuoter;
    }

    public String getMarketListQuoterContacts() {
        return marketListQuoterContacts;
    }

    public void setMarketListQuoterContacts(String marketListQuoterContacts) {
        this.marketListQuoterContacts = marketListQuoterContacts;
    }

    public String getQuoteUserId() {
        return quoteUserId;
    }

    public void setQuoteUserId(String quoteUserId) {
        this.quoteUserId = quoteUserId;
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

    public boolean getCustodianQualification() {
        return custodianQualification;
    }

    public void setCustodianQualification(boolean custodianQualification) {
        this.custodianQualification = custodianQualification;
    }

    public QueryFundSize getFundSizeEnum() {
        return fundSizeEnum;
    }

    public void setFundSizeEnum(QueryFundSize fundSizeEnum) {
        this.fundSizeEnum = fundSizeEnum;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getQuoteOperatorId() {
        return quoteOperatorId;
    }

    public void setQuoteOperatorId(String quoteOperatorId) {
        this.quoteOperatorId = quoteOperatorId;
    }
}
