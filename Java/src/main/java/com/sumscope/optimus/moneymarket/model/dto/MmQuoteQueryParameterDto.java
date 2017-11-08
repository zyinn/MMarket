package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.*;

import java.util.List;

/**
 * Created by fan.bai on 2016/8/4.
 * 列表查询参数Dto
 */
public class MmQuoteQueryParameterDto {
    /**
     * 方向
     */
    private Direction direction;

    /**
     * 报价类型
     */
    private List<QuoteType> quoteTypes;

    /**
     * 报价区域，记录省份中文，例如： 辽宁，上海，北京
     */
    private List<String> areas;

    /**
     * 机构类型列表
     */
    private List<CalculatedBankNature> bankNatures;

    /**
     * 机构规模列表
     */
    private List<QueryFundSize> fundSizes;

    /**
     * 产品特征标签列表
     */
    private List<String> tagCodes;

    /**
     * 排序的期限，不排序则设置为null
     */
    private QuoteTimePeriod orderByPeriod;

    /**
     * 排序顺序：
     * ASC 升序
     * DESC 降序
     */
    private String orderSeq;
    /**
     * 分页查询时指定每页的大小
     */
    private int pageSize;
    /**
     * 分页查询时指定本次查询的第几个分页数据
     */
    private int pageNumber;
    /**
     * 需要进行count计算
     */
    private boolean needCount = false;

    /**
     * 是否只查询“我的报价”
     */
    private boolean privateQuotesOnly = false;

    /**
     * 查询QB精选
     */
    private boolean primeQuotesOnly = false;

    /**
     * 查询托管资质
     */
    private boolean custodianQualification = false;

    /**
     * 按机构ID查询
     */
    private List<String> institutionIdList;

    /**
     * 按报价人ID查询- QQ 或者QB id
     */
    private String quoteUserId;

    /**
     * 按备注查询
     */
    private String memo;

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public List<QuoteType> getQuoteTypes() {
        return quoteTypes;
    }

    public void setQuoteTypes(List<QuoteType> quoteTypes) {
        this.quoteTypes = quoteTypes;
    }

    public List<String> getAreas() {
        return areas;
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
    }

    public List<CalculatedBankNature> getBankNatures() {
        return bankNatures;
    }

    public void setBankNatures(List<CalculatedBankNature> bankNatures) {
        this.bankNatures = bankNatures;
    }

    public List<QueryFundSize> getFundSizes() {
        return fundSizes;
    }

    public void setFundSizes(List<QueryFundSize> fundSizes) {
        this.fundSizes = fundSizes;
    }

    public List<String> getTagCodes() {
        return tagCodes;
    }

    public void setTagCodes(List<String> tagCodes) {
        this.tagCodes = tagCodes;
    }

    public QuoteTimePeriod getOrderByPeriod() {
        return orderByPeriod;
    }

    public void setOrderByPeriod(QuoteTimePeriod orderByPeriod) {
        this.orderByPeriod = orderByPeriod;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public boolean isNeedCount() {
        return needCount;
    }

    public void setNeedCount(boolean needCount) {
        this.needCount = needCount;
    }

    public boolean isPrivateQuotesOnly() {
        return privateQuotesOnly;
    }

    public void setPrivateQuotesOnly(boolean privateQuotesOnly) {
        this.privateQuotesOnly = privateQuotesOnly;
    }

    public boolean isPrimeQuotesOnly() {
        return primeQuotesOnly;
    }

    public void setPrimeQuotesOnly(boolean primeQuotesOnly) {
        this.primeQuotesOnly = primeQuotesOnly;
    }

    public boolean isCustodianQualification() {
        return custodianQualification;
    }

    public void setCustodianQualification(boolean custodianQualification) {
        this.custodianQualification = custodianQualification;
    }

    public List<String> getInstitutionIdList() {
        return institutionIdList;
    }

    public void setInstitutionIdList(List<String> institutionIdList) {
        this.institutionIdList = institutionIdList;
    }

    public String getQuoteUserId() {
        return quoteUserId;
    }

    public void setQuoteUserId(String quoteUserId) {
        this.quoteUserId = quoteUserId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(String orderSeq) {
        this.orderSeq = orderSeq;
    }
}
