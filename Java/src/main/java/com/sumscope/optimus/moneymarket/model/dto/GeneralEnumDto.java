package com.sumscope.optimus.moneymarket.model.dto;

/**
 * Created by fan.bai on 2016/8/29.
 * 服务端使用了一些用于计算或查询的枚举类型。这些类型的一部分需要传递给前端程序。本类用于一般枚举类型的Dto包装
 */
public class GeneralEnumDto {
    private String name;
    private String displayName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
