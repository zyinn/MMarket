package com.sumscope.optimus.moneymarket.dao;

import java.util.List;

/**
 * Created by fan.bai on 2016/8/30.
 * 用于业务数据库的MmQuoteDao接口
 */
public interface BusinessMmQuoteDao extends MmQuoteDao {
    /**
     * 根据输入的报价单ID列表，删除本地数据库的报价单主表数据
     *
     * @param quoteIdList 报价单主表ID列表
     */
    void deleteDataFromDB(List<String> quoteIdList);

    /**
     * @return 获取当前所有报价单报价人列表
     */
    List<String> retrieveQuoteUsersID();

    /**
     * @return 获取当前所有报价单机构列表
     */
    List<String> retrieveQuoteInstitutionsID();
}
