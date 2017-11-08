package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.moneymarket.commons.util.StringTrimUtil;

public class User {
    private String userId;

    private String displayName;
    
    private String displayNamePinYin;
    
    private String displayNamePY;

    private String companyId;

    private String companyName;

    private String telephone;

    private String mobile;
    
    private String email;

    private String province;

    private String cityName;

    private String bankNature;

    private String primeCompanyId;

    private String qualification;

    private String userName;

    private String password;

    private String source;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = StringTrimUtil.getTrimedString(userId);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = StringTrimUtil.getTrimedString(displayName);
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = StringTrimUtil.getTrimedString(companyId);
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = StringTrimUtil.getTrimedString(companyName);
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = StringTrimUtil.getTrimedString(telephone);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = StringTrimUtil.getTrimedString(mobile);
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = StringTrimUtil.getTrimedString(province);
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = StringTrimUtil.getTrimedString(cityName);
    }

    public String getBankNature() {
        return bankNature;
    }

    public void setBankNature(String bankNature) {
        this.bankNature = StringTrimUtil.getTrimedString(bankNature);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = StringTrimUtil.getTrimedString(source);
    }

    public String getDisplayNamePinYin() {
        return displayNamePinYin;
    }

    public void setDisplayNamePinYin(String displayNamePinYin) {
        this.displayNamePinYin = displayNamePinYin;
    }

    public String getDisplayNamePY() {
        return displayNamePY;
    }

    public void setDisplayNamePY(String displayNamePY) {
        this.displayNamePY = displayNamePY;
    }

    public String getPrimeCompanyId() {
        return primeCompanyId;
    }

    public void setPrimeCompanyId(String primeCompanyId) {
        this.primeCompanyId = primeCompanyId;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}