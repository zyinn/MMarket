package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by Administrator on 2016/6/24.
 */
public enum ContactQuoteAttribute {
    FI("FI"),//同业理财
    CD("CD");//线下资金

    private String quoteTypeName;

    ContactQuoteAttribute(String name) {
        this.quoteTypeName = name;
    }

    public String getQuoteTypeName() {
        return quoteTypeName;
    }

    public void setQuoteTypeName(String quoteTypeName) {
        this.quoteTypeName = quoteTypeName;
    }
}
