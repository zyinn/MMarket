package com.sumscope.optimus.moneymarket.model.dbmodel;

import java.io.Serializable;

/**
 * Created by fan.bai on 2016/8/5.
 * 报价单对应的标签模型
 */
public class MmQuoteTag implements Serializable {
    /**
     * 该标签对应的报价单id
     */
    private String quoteId;
    /**
     * 标签Code
     */
    private String tagCode;

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MmQuoteTag that = (MmQuoteTag) o;

        if (quoteId != null ? !quoteId.equals(that.quoteId) : that.quoteId != null) return false;
        return tagCode != null ? tagCode.equals(that.tagCode) : that.tagCode == null;

    }

    public boolean equalsCode(MmQuoteTag that) {
        if (this == that) return true;

        return tagCode != null ? tagCode.equals(that.tagCode) : that.tagCode == null;

    }

}
