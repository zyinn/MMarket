package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.commons.enums.Direction;

/**
 * 手机理财应用用于获取价格矩阵的查询参数Dto
 */
public class MobileMatrixParameterDto {

	/**
	 * 报价类型
	 */
	private QuoteType quoteType;

	/**
	 * 报价方向
	 */
	private Direction direction;

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
}
