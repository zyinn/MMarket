package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by kedong.li on 2016/5/9. 
 * LASTMMQUOTE = 上次报价
 * AREA = 地域偏好
 */
public enum PreferenceType {

	LASTMMQUOTE("上次报价"),
	AREA("地域偏好"),
	INSTITUTION_USER_MAP("机构联系人对应关系");
	private String name;

	PreferenceType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
