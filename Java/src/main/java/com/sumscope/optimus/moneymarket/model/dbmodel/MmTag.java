package com.sumscope.optimus.moneymarket.model.dbmodel;

/**
 * Created by fan.bai on 2016/8/5.
 * 报价标签信息数据模型
 */
public class MmTag {
    /**
     * 唯一的code
     */
    private String tagCode;
    /**
     * 标签中文显示
     */
    private String tagName;

    /**
     * 序列号，用于排序
     */
    private int seq;

    /**
     * 是否有效
     */
    private boolean active;

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
