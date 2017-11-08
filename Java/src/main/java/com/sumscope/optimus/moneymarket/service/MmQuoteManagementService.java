package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;

import java.util.List;

/**
 * Created by fan.bai on 2016/8/10.
 * 报价单管理服务，用于报价单的数据写入
 */
interface MmQuoteManagementService {

    /**
     * 将通过QB客户端生成的MmQuote列表的报价单写入数据库，并往本地总线发布。
     * 该方法使用事物控制。
     * 实际的代码逻辑请看实现类的说明。
     *
     * @param mmQuoteList 报价单列表，该报价从QB客户端生成
     */
    void setupMmQuotesInTransaction(List<MmQuote> mmQuoteList, Source source);


    /**
     * 该枚举用于区分不同的报价单录入方式。不同的录入方式有不同的数据逻辑
     * <p>
     * 普通QB报价
     * QQ接入
     */
    enum Source {
        /**
         * QQ导入数据，数据逻辑简介：
         * 根据主表信息查询今日现存报价单
         * 若无现存报价单，插入数据库
         * 若存在报价单，更新该报价单主表，明细表
         */
        QQ,
        /**
         * Excel导入数据，数据逻辑简介：
         * 先将本次导入报价单的所有所属机构的现存报价单作废
         * 再将报价单作为新报价单插入数据库
         */
        EXCEL,
        /**
         * 通过QB客户端录入的报价。
         * 数据逻辑简介：
         * 如果ID无值，直接插入数据库；
         * 如果ID有值，若现存报价单是今日报价，更新数据库；
         * 若现存报价单是今日以前报价，则作废该报价单
         * 并将此次报价单作为新报价单插入数据库。
         */
        QB_CLIENT,

        /**
         * 历史记录写入数据库。由于是历史记录，因此主键不做变化，直接插入历史数据库中
         */
        ARCHIVE
    }

}
