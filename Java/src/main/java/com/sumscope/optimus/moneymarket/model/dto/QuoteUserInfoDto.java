package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.ContactQuoteAttribute;

public class QuoteUserInfoDto {
    
    // 用户id(qqr qb_id)
    private String qb_id;
    // 用户名称
    private String name;
    // qm状态
    private String status;
    
    private String telephone;
    private String mobile;
    private String email;

    private String qmId;

    private ContactQuoteAttribute quoteAttribute;

    public String getQb_id() {
        return qb_id;
    }

    public void setQb_id(String qb_id) {
        this.qb_id = qb_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQmId() {
        return qmId;
    }

    public void setQmId(String qmId) {
        this.qmId = qmId;
    }

    public ContactQuoteAttribute getQuoteAttribute() {
        return quoteAttribute;
    }

    public void setQuoteAttribute(ContactQuoteAttribute quoteAttribute) {
        this.quoteAttribute = quoteAttribute;
    }
}
