package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.moneymarket.commons.util.StringTrimUtil;
import com.sumscope.optimus.moneymarket.commons.enums.PreferenceType;

public class UserPreference {
    private String id;

    private String userId;

    private PreferenceType preferenceType;

    private String preferenceValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = StringTrimUtil.getTrimedString(id);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = StringTrimUtil.getTrimedString(userId);
    }

    public PreferenceType getPreferenceType() {
		return preferenceType;
	}

	public void setPreferenceType(PreferenceType preferenceType) {
		this.preferenceType = preferenceType;
	}

	public String getPreferenceValue() {
        return preferenceValue;
    }

    public void setPreferenceValue(String preferenceValue) {
        this.preferenceValue = StringTrimUtil.getTrimedString(preferenceValue);
    }

    @Override
    public String toString() {
        return "UserPreference{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", preferenceType='" + preferenceType + '\'' +
                ", preferenceValue='" + preferenceValue + '\'' +
                '}';
    }
}