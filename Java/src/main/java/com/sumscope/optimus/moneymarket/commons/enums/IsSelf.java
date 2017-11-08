package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by shaoxu.wang on 2016/4/22.
 * 查询参数，表明是否仅查询我的报价
 */
public enum IsSelf {
    NOSELF("不启用我的报价过滤"),
    SELF("我的报价过滤");

    private String displayName;

    IsSelf(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
