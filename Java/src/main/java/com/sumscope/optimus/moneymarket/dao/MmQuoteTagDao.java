package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteTag;

import java.util.List;

/**
 * Created by fan.bai on 2016/8/5.
 * 处理有关MmQuoteTag数据的Dao接口,由于存在业务数据库与历史数据库，因此该接口实现两个数据库的公共功能
 */
public interface MmQuoteTagDao {
    /**
     * 根据输入的报价单id列表获取对应的报价单标签数据
     *
     * @param quoteIds 报价单id列表
     * @return 对应的所有报价单标签数据
     */
    List<MmQuoteTag> queryMmQuoteTags(List<String> quoteIds);

    /**
     * 将报价单标签插入数据库
     *
     * @param mmQuoteTags 标签列表
     */
    void insertMmQuoteTags(List<MmQuoteTag> mmQuoteTags);

    /**
     * 删除报价单列表中的所有标签
     *
     * @param quoteToDeleteTags 报价单ID列表
     */
    void deleteTagsFromQuotes(List<String> quoteToDeleteTags);
}
