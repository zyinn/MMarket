package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.model.dbmodel.*;

import java.util.Date;
import java.util.List;

/**
 * Created by fan.bai on 2016/8/5.
 * 处理与报价单主表有关的数据库操作，由于存在业务数据库与历史数据库，因此该接口实现两个数据库的公共功能
 */
public interface MmQuoteDao {
    /**
     * 根据参数获取报价单主表信息，该方法仅获取主表信息，对于数据模型中其他明细信息，例如报价明细数据以及标签列表需要
     * 通过其他Dao方法获取。
     * 该方法应为所有对于报价主表查询的唯一方法，服务层实现不同的查询参数以实现不同的服务，
     * 比如仅查询当前用户有效报价，或查询联盟报价列表等
     * @param parameters MmQuoteMainQueryParameters
     * @return 查询的结果
     */
    List<MmQuote> queryMmQuoteMain(MmQuoteMainQueryParameters parameters);

    /**
     * 根据参数获取报价单明细以及主表信息，该方法一步获取了报价单的主表及对应明细表的数据，因此效率上比queryMmQuoteMain
     * 在支持需要明细数据的查询时来的高。然而如果分页逻辑确认是按主表分页，则不得不采用queryMmQuoteMain（）方法。
     * 该方法应为所有对于报价单详细数据查询的唯一方法，服务层实现不同的查询参数以实现不同的服务，
     * @param parameters AbstractMmQuoteQueryParameters
     * @return 查询的结果
     */
    List<MmQuoteDetailsQueryResult> queryMmQuoteWithDetails(MmQuoteDetailsQueryParameters parameters);

    /**
     * 将报价单主表插入数据库。ID由service层设置
     * @param newQuotes 需要新增的报价单
     */
    void insertMmQuotes(List<MmQuote> newQuotes);

    /**
     * 根据ID依次更新报价单主表数据
     * @param mmQuote 需要更新的报价单主表
     */
    void updateMmQuotes(MmQuote mmQuote);

    /**
     * 作废报价单。既把报价单过期日期设置为昨日。
     * @param yesterdayDate 作废日期，一般是昨日日期
     * @param idList 需要作废的报价单ID列表
     */
    void deactivateMmQuotes(Date yesterdayDate, List<String> idList);
}
