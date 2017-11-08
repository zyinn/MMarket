package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by shaoxu.wang on 2016/4/22.
 * 搜索的方式
 */
public enum DisplayType {
    DISPLAYQB("QB精品"),
    DISPLAYMARKET("市场报价");

    private String displayName;

    DisplayType(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
