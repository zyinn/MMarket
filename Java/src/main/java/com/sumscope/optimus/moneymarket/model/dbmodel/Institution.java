package com.sumscope.optimus.moneymarket.model.dbmodel;


import java.math.BigDecimal;
import java.util.List;

/**
 * 机构数据模型，记录了机构的一些用于本系统的信息
 */
public class Institution {
    
    private String id;
    
    private String name;

    private String pinyin;

    private String pinyinFull;

    private String qualification;

    private String bankNature;

    private String cityName;

    private String province;

    private String parentId;//总公司id

    private String isHead;//是否是总公司 是否总公司  0:no     1:yes

    private BigDecimal fundSize;//公司的机构规模

    private List<Institution> parentIds;//父机构id集合

    public String getBankNature() {
        return bankNature;
    }

    public void setBankNature(String bankNature) {
        this.bankNature = bankNature;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyinFull() {
        return pinyinFull;
    }

    public void setPinyinFull(String pinyinFull) {
        this.pinyinFull = pinyinFull;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIsHead() {
        return isHead;
    }

    public void setIsHead(String isHead) {
        this.isHead = isHead;
    }

    public BigDecimal getFundSize() {
        return fundSize;
    }

    public void setFundSize(BigDecimal fundSize) {
        this.fundSize = fundSize;
    }

    public List<Institution> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<Institution> parentIds) {
        this.parentIds = parentIds;
    }

    @Override
    public String toString() {
        return "Institution [id=" + id + ", name=" + name
                + ", pinyin=" + pinyin + ", pinyinFull=" + pinyinFull + "]";
    }

    
}