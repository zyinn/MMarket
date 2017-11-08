package com.sumscope.optimus.moneymarket.model.dto;

/**
 * Created by fan.bai on 2016/8/5.
 * 报价单标签的Dto模型
 */
public class MmTagDto {
    /**
     * 唯一的code
     */
    private String tagCode;
    /**
     * 标签中文显示
     */
    private String tagName;

    /**
     * 排序字段
     */
    private int seq;

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
}
