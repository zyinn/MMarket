package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.commons.enums.*;
import com.sumscope.optimus.moneymarket.model.dbmodel.PriceTrend;
import com.sumscope.optimus.moneymarket.model.dbmodel.PriceTrendsQueryParameters;

import java.util.Date;
import java.util.List;

/**
 * Created by fan.bai on 2016/9/19.
 * 收益率矩阵计算及收益率走势查询的服务
 */
public interface MatrixCalcuationAndQueryService {
    /**
     * 根据参数对某一日的报价单进行报价极限值计算。矩阵的行是报价类型对应的报价期限，矩阵的列是当前需求
     * 规定的机构类型及规模。该服务根据当前需求定义的机构规模和类型进行每个期限的报价极限值计算。每一个
     * 结果对应于矩阵一个cell的数值
     *
     * @param direction 方向
     * @param quoteType 报价类型
     * @param time      日期
     * @return 计算结果列表
     */
    List<PriceTrend> calculateMatrixCells(Direction direction, QuoteType quoteType, Date time);

    /**
     *
     * @param parameters 查询参数
     * @return 查询得到的数值
     */
    List<PriceTrend> queryPriceTrends(PriceTrendsQueryParameters parameters);

    /**
     * 每日18:00结转矩阵计算结果生成历史数据，存入数据库对应表中
     */
    void takeSnapshotForMatrix(Date time);
}
