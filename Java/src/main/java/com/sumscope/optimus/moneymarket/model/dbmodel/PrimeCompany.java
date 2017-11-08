package com.sumscope.optimus.moneymarket.model.dbmodel;

/**
 * Created by fan.bai on 2016/5/12.
 * 精品机构数据结构
 */
public class PrimeCompany {
    private String id;
    private String companyId;
    private String telephone;
    private Integer active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
