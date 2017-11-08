package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by shaoxu.wang on 2016/4/22.
 * 前台根据搜索框交互，由用户确定的搜索方式。
 */
public enum SearchType {
    INSTITUTE("机构"),
    CONTACT("联系人"),
    QQ("QQ昵称"),
    MEMO("备注");


    private String displayName;

    SearchType(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    
}
