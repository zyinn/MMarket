package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * 是否是总公司枚举类
 * Created by huaijie.chen on 2016/7/25.
 */
public enum IsHeadCompany {
    HEAD_COMPANY("1"),//是总公司
    NOT_HEAD_COMPANY("0"),//不是总公司
    ;
    private String value;

    IsHeadCompany(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
