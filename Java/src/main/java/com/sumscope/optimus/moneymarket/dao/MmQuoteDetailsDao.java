package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteDetails;

import java.util.List;

/**
 * Created by fan.bai on 2016/8/5.
 * * 处理与报价单明细表有关的数据库操作,由于存在业务数据库与历史数据库，因此该接口实现两个数据库的公共功能
 */
public interface MmQuoteDetailsDao {
    /**
     * 查询quoteId在列表里的报价详情记录，包括已失效的明细数据 active = 0
     * 当List很大时可能需要分批处理。分批处理的逻辑由Dao内部完成
     *
     * @param mmQuoteIds quoteId集合
     * @return
     */
    List<MmQuoteDetails> queryQuoteDetails(List<String> mmQuoteIds);

    /**
     * 将报价单明细表插入数据库，id由service层设置
     *
     * @param details MmQuoteDetails 的列表
     */
    void insertQuoteDetails(List<MmQuoteDetails> details);

    /**
     * 根据输入参数的报价单明细id更新报价单明细表
     *
     * @param details 报价单明细表列表
     */
    void updateQuoteDetails(List<MmQuoteDetails> details);


}
