package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.model.dbmodel.PriceTrend;
import com.sumscope.optimus.moneymarket.model.dbmodel.PriceTrendsQueryParameters;

import java.util.Date;
import java.util.List;

/**
 * Created by fan.bai on 2016/9/22.
 * PriceTrend 数据的Dao接口
 */
public interface MmQuotePriceTrendDao {
    /**
     * 根据生成时间删除当前数据。
     * 应用程序在每日规定时间（18：00）会自动结转一次数据。我们允许该程序重复执行，因此需要先删除某日已结转的数据。
     * @param time 生成时间
     */
    void deleteByTime(Date time);

    /**
     * 将得到的结果保存入数据库。要求其ID已经被设置。
     * @param trends 报价价格极值数据
     */
    void insertPriceMatrixCells(List<PriceTrend> trends);

    /**
     * 根据参数查询价格极值
     * @param parameters 参数
     * @return 对应的结果
     */
    List<PriceTrend> queryPriceTrends(PriceTrendsQueryParameters parameters);
}
