package com.sumscope.optimus.moneymarket.dao;

import java.util.List;

/**
 * Created by fan.bai on 2016/8/30.
 * 用于业务数据库的MmQuoteDetailsDao接口
 */
public interface BusinessMmQuoteDetailsDao extends MmQuoteDetailsDao {
    /**
     * 根据输入的报价单主表ID，删除本地数据库对应的报价单明细数据
     *
     * @param quoteIdList 报价单主表ID列表
     */
    void deleteDataFromDB(List<String> quoteIdList);

    /**
     * 报价单主表仍未作废，但是其中明细数据可能已经失效，比如对于有效期7天的报价单，其中对T1D进行了报价。
     * 根据期限定义，该明细数据仅有效1天。因此过期后需要将这些明细数据作废。
     */
    void deactivateExpiredMmQuoteDetails();
}
