package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.model.dbmodel.User;

import java.util.List;

/**
 * Created by fan.bai on 2016/5/28.
 * 机构Dto，用于前后太交互机构信息
 */
public class InstitutionDto {
    /**
     * 机构ID，不可空
     */
    private String institutionId;

    /**
     * 机构显示名称
     */
    private String displayName;

    /**
     * 拼音首字母，用于按拼音查询
     */
    private String pinyin;

    /**
     * 拼音全称，用于按拼音查询
     */
    private String pinyinFull;

    /**
     * 新增字段 机构联系人id
     */
    private String institutionContactsId;

    //联系人PinYin
    private String contactsDisplayNamePinYin;
   //联系人PY
    private String contactsDisplayNamePY;
    /**
     * 新增字段 机构联系人名称
     */
    private String institutionContactsName;

    /**
     * 机构下的联系人
     */
    private List<User> userList;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getInstitutionContactsId() {
        return institutionContactsId;
    }

    public void setInstitutionContactsId(String institutionContactsId) {
        this.institutionContactsId = institutionContactsId;
    }

    public String getInstitutionContactsName() {
        return institutionContactsName;
    }

    public void setInstitutionContactsName(String institutionContactsName) {
        this.institutionContactsName = institutionContactsName;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public String getContactsDisplayNamePinYin() {
        return contactsDisplayNamePinYin;
    }

    public void setContactsDisplayNamePinYin(String contactsDisplayNamePinYin) {
        this.contactsDisplayNamePinYin = contactsDisplayNamePinYin;
    }

    public String getContactsDisplayNamePY() {
        return contactsDisplayNamePY;
    }

    public void setContactsDisplayNamePY(String contactsDisplayNamePY) {
        this.contactsDisplayNamePY = contactsDisplayNamePY;
    }
}
