package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;

import java.math.BigDecimal;


public class MmQuoteDetailsDto {
	private String id;

	private QuoteTimePeriod limitType; // 正常报价的期限

	private Integer dayHigh;
								//自定义报价时,期限在这两字段设置.
	private Integer dayLow;

	private BigDecimal price;

	private BigDecimal quantity;

	private boolean active;  //有效

	private  boolean icon;// 是否是非标准期限

	private  QuoteTimePeriod quoteTimePeriod; //用于展示是几月的期限

	public QuoteTimePeriod getLimitType() {
		return limitType;
	}

	public void setLimitType(QuoteTimePeriod limitType) {
		this.limitType = limitType;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public Integer getDayHigh() {
		return dayHigh;
	}

	public void setDayHigh(Integer dayHigh) {
		this.dayHigh = dayHigh;
	}

	public Integer getDayLow() {
		return dayLow;
	}

	public void setDayLow(Integer dayLow) {
		this.dayLow = dayLow;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isIcon() {
		return icon;
	}

	public void setIcon(boolean icon) {
		this.icon = icon;
	}

	public QuoteTimePeriod getQuoteTimePeriod() {
		return quoteTimePeriod;
	}

	public void setQuoteTimePeriod(QuoteTimePeriod quoteTimePeriod) {
		this.quoteTimePeriod = quoteTimePeriod;
	}
}
