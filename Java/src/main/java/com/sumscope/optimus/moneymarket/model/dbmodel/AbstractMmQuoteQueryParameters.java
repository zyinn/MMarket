package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.commons.util.QuoteDateUtils;

import java.util.Date;
import java.util.List;


/**
 * 查询报价主表的参数模型
 */
public abstract class AbstractMmQuoteQueryParameters implements Cloneable {
    /**
     * 按报价单主表ID列表查询
     */
    private List<String> quoteIdList;
    /**
     * 查询方向
     */
    private Direction direction;

    /**
     * 查询报价类型列表
     */
    private List<QuoteType> quoteTypes;

    /**
     * 查询报价区域，记录省份中文，例如： 辽宁，上海，北京
     */
    private List<String> areas;

    /**
     * 查询机构类型列表，写入对应的bankNature的code，比如1，9，等
     */
    private List<String> bankNatures;

    /**
     * true:查询其他机构类型
     * false: 不做约束
     */
    private boolean searchOtherBankNatures;

    /**
     * 机构规模列表
     */
    private List<MmQuoteQueryFundSizePeriod> fundSizes;

    /**
     * 产品特征标签列表
     */
    private List<String> tagCodes;

    /**
     * 查询QB精选
     * false = 查询所有
     * true = 仅查询QB精选报价
     */
    private boolean primeQuotesOnly = false;

    /**
     * 查询托管资质
     * false = 查询所有
     * true = 仅查询有托管资质的报价
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

    private String quoteOperatorId;

    /**
     * 按备注查询
     */
    private String memo;


    /**
     * 是否查询有效报价，有效报价指的是尚未过期的报价。既报价单.过期日期 >= 当前日期
     * true ： 仅查询有效报价
     * false: 查询所有报价
     */
    private boolean onlyValidQuotes = false;


    /**
     * 是否查询过期报价，既报价单.过期日期 < 当前日期，该字段用于获取过期报价导出至历史库
     * true ： 仅查询过期报价
     * false: 查询所有报价
     */
    private boolean onlyExpiredQuotes = false;


    /**
     * 是否支持paging
     */
    private boolean paging = false;
    /**
     * 分页查询时指定每页的大小
     */
    private int pageSize;
    /**
     * 分页查询时指定本次查询从第几行数据开始查询，通过对应Dto中的（pageNumber-1）*pageSize 得出
     */
    private long startRows;
    /**
     * 按生成日期查询。由于记录的生成日期精确到毫秒，因此在查询时按当日的开始至结束时间查询
     * 例如设置createTime是‘2016-06-05’，则查询是按2016-06-05 0:0:1 至 2016-06-05 23:59:59 进行查询
     */
    private Date createTime;

    /**
     * 报价方法
     */
    private MethodType methodType;

    /**
     * 根据报价单创建日期早于该计算日期并且过期日期晚于该计算日期的逻辑进行查询。
     * 计算日期的时间段落应该是18：00：00
     * 该参数用于计算报价单报价极值
     */
    private Date calculationTime;

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

    public List<String> getBankNatures() {
        return bankNatures;
    }

    public void setBankNatures(List<String> bankNatures) {
        this.bankNatures = bankNatures;
    }

    public List<MmQuoteQueryFundSizePeriod> getFundSizes() {
        return fundSizes;
    }

    public void setFundSizes(List<MmQuoteQueryFundSizePeriod> fundSizes) {
        this.fundSizes = fundSizes;
    }

    public List<String> getTagCodes() {
        return tagCodes;
    }

    public void setTagCodes(List<String> tagCodes) {
        this.tagCodes = tagCodes;
    }


    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getStartRows() {
        return startRows;
    }

    public void setStartRows(long startRows) {
        this.startRows = startRows;
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

    public boolean isOnlyValidQuotes() {
        return onlyValidQuotes;
    }

    public void setOnlyValidQuotes(boolean onlyValidQuotes) {
        this.onlyValidQuotes = onlyValidQuotes;
    }

    public boolean isSearchOtherBankNatures() {
        return searchOtherBankNatures;
    }


    public boolean isPaging() {
        return paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            LogManager.error(e.toString() + "this is CloneNotSupportedException");
        }
        return null;
    }

    public List<String> getQuoteIdList() {
        return quoteIdList;
    }

    public void setQuoteIdList(List<String> quoteIdList) {
        this.quoteIdList = quoteIdList;
    }

    public boolean isOnlyExpiredQuotes() {
        return onlyExpiredQuotes;
    }

    public void setOnlyExpiredQuotes(boolean onlyExpiredQuotes) {
        this.onlyExpiredQuotes = onlyExpiredQuotes;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTimeBegin() {
        if (createTime != null) {
            return QuoteDateUtils.getBeginingTimeOfDate(createTime);
        }
        return null;
    }


    public Date getCreateTimeEnd() {
        if (createTime != null) {
            return QuoteDateUtils.getLatestTimeOfDate(createTime);
        }
        return null;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    public void setSearchOtherBankNatures(boolean searchOtherBankNatures) {
        this.searchOtherBankNatures = searchOtherBankNatures;
    }

    public Date getCalculationTime() {
        return calculationTime;
    }

    public void setCalculationTime(Date calculationTime) {
        this.calculationTime = calculationTime;
    }

    public String getQuoteOperatorId() {
        return quoteOperatorId;
    }

    public void setQuoteOperatorId(String quoteOperatorId) {
        this.quoteOperatorId = quoteOperatorId;
    }
}
