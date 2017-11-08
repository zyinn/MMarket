package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by shaoxu.wang on 2016/4/21.
 * UNLIMIT = 不限
 * LIMIT = 有
 */
public enum TrustType {
    UNLIMIT("0", "不限"),
    LIMIT("1", "有");

    private String name;
    private String value;

    TrustType(String value, String name) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public  void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getValueStr(){
        return value+"";
    }
}
