package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by shaoxu.wang on 2016/4/22.
 * 有效报价查询参数
 */
public enum IsActive {
    ACTIVE(0, "不启用有效报价过滤"),
    INACTIVE(1, "有效报价过滤");

    private int value;

    private String displayName;

    IsActive(int value, String name) {
        this.value = value;
        this.displayName = name;
    }


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getValueStr(){
        return value+"";
    }
}
