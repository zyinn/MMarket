package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 
 * 报价单主表Dto，用于报价单生成
 *
 */
public class MmQuoteDto {
	private String id;

	private QuoteType quoteType;

	private Direction direction;

	private String quoteUserId;

	private List<MmQuoteDetailsDto> quoteDetailsDtos;

	private String memo;

	private String source;

	/**
	 * 报价机构ID，若用户只是报自己机构报价单，则该字段可以为空。反之则填入对应的联盟机构ID
	 */
	private String institutionId;

	private String institutionName;

	/**
	 * 机构联系人Id
	 */
	private String quoteOperatorId;
	/**
	 * 机构联系人name
	 */
	private String quoteOperatorName;

	/**
	 * 机构名称的汉语拼音。用于排序
	 */
	private String institutionNamePY;

	/**
	 * 报价方法，由前台设置，若前台未设置，默认普通报价
	 */
	private MethodType methodType;

	/**
	 * 序列号，必须由前台设置
	 */
	private int sequence;

	/**
	 * 表明多少天有效
	 */
	private int valideDays;
	/**
	 * 省份信息，反范式设计，用于查询优化
	 */
	private String province;
	/**
	 * 机构规模
	 */
	private BigDecimal fundSize;

	/**
	 * 该报价单使用的标签
	 */
	private Set<MmTagDto> tags;

	/**
	 * 对于联盟报价，前端将对整个报价单设置是否有效，该字段仅用于联盟报价。
	 * 当active为false时，设置所有明细数据的active为false，否则全部为有效。
	 */
	private boolean active;

	private QuoteUserInfoDto trader;//联系人

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

	public List<MmQuoteDetailsDto> getQuoteDetailsDtos() {
		return quoteDetailsDtos;
	}

	public void setQuoteDetailsDtos(List<MmQuoteDetailsDto> quoteDetailsDtos) {
		this.quoteDetailsDtos = quoteDetailsDtos;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getQuoteUserId() {
		return quoteUserId;
	}

	public void setQuoteUserId(String quoteUserId) {
		this.quoteUserId = quoteUserId;
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

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getValideDays() {
		return valideDays;
	}

	public void setValideDays(int valideDays) {
		this.valideDays = valideDays;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public BigDecimal getFundSize() {
		return fundSize;
	}

	public void setFundSize(BigDecimal fundSize) {
		this.fundSize = fundSize;
	}

	public String getInstitutionNamePY() {
		return institutionNamePY;
	}

	public void setInstitutionNamePY(String institutionNamePY) {
		this.institutionNamePY = institutionNamePY;
	}

	public Set<MmTagDto> getTags() {
		return tags;
	}

	public void setTags(Set<MmTagDto> tags) {
		this.tags = tags;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getQuoteOperatorId() {
		return quoteOperatorId;
	}

	public void setQuoteOperatorId(String quoteOperatorId) {
		this.quoteOperatorId = quoteOperatorId;
	}

	public String getQuoteOperatorName() {
		return quoteOperatorName;
	}

	public void setQuoteOperatorName(String quoteOperatorName) {
		this.quoteOperatorName = quoteOperatorName;
	}

	public QuoteUserInfoDto getTrader() {
		return trader;
	}

	public void setTrader(QuoteUserInfoDto trader) {
		this.trader = trader;
	}
}
