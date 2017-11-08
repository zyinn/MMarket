package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;

/**
 * 获取精品机构联系人参数Dto
 */
public class InstitutionContactsParameterDto {

	/**
	 * 报价类型
	 */
	private QuoteType quoteType;

	/**
	 * 机构ID，该机构应为精品机构
	 */
	private String institutionId;

	public QuoteType getQuoteType() {
		return quoteType;
	}

	public void setQuoteType(QuoteType quoteType) {
		this.quoteType = quoteType;
	}

	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
	}
}
