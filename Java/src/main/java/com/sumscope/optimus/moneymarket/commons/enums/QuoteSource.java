package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by fan.bai on 2016/5/12.
 * 报价来源，QB报价或者QQ报价
 */
public enum  QuoteSource {
    /**
     * QQ 报价，通过数据源从QQ群中接入的报价信息，该数值写入数据库。
     */
    QQ("QQ"),

    /**
     * QB报价，通过页面报价管理进行的报价，该数值写入数据库。
     */
    QB("QB"),

    /**
     * QB精选报价，通过QB报价管理精选的报价，同时机构属于精品机构的报价。该数值仅用于Web端显示，不记录入数据库
     */
    PRIME_QB("PRIME_QB");


    private final String displayString;

    QuoteSource(String type){
        this.displayString = type;
    }

    public String getDisplayString() {
        return displayString;
    }
}
