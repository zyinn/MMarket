package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;

/**
 * Created by fan.bai on 2016/9/19.
 * 矩阵查询参数Dto，用于市场分析页面查询参数的前后台交互
 */
public class PriceMatrixQueryParameterDto {
    /**
     * 方向
     */
    private Direction direction;
    /**
     * 报价类型
     */
    private QuoteType quoteType;

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
}
