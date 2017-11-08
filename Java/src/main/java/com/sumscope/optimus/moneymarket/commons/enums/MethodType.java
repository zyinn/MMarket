package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by fan.bai on 2016/6/7.
 * 报价方法枚举
 */
public enum MethodType {
    /**
     * 普通报价，由机构交易员自行报价。在列表中报价机构显示该用户所属机构
     */
    SEF("普通报价"),
    /**
     * 联盟报价，由主机构代联盟机构进行报价。在列表中报价机构显示为主机构，联盟机构信息显示在备注中
     */
    ALC("联盟报价"),
    /**
     * 代报价，由一个机构帮助其他机构进行报价。在列表中报价机构显示为各目标机构
     */
    BRK("代报价");

    private String displayName;

    MethodType(String dName){
        this.displayName = dName;

    }

    public String getDisplayName() {
        return displayName;
    }
}
