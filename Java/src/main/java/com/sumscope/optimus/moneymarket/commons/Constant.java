package com.sumscope.optimus.moneymarket.commons;

/**
 * 常量
 *
 * @author xingyue.wang
 */
public final class Constant {
    //用于定义业务数据库及历史数据库相关数据服务
    public static final String BUSINESS_DATA_SOURCE = "dataSource";
    public static final String HISTORY_DATA_SOURCE = "historydataSource";
    public static final String BUSINESS_TRANSACTION_MANAGER = "currentTransactionManager";
    public static final String HISTORY_TRANSACTION_MANAGER = "historyTransactionManager";
    public static final String BUSINESS_SQL_SESSION_TEMPLATE = "sqlSessionTemplate";
    public static final String HISTORY_SQL_SESSION_TEMPLATE = "historysqlSessionTemplate";

    //    用于定义缓存更新通知
    public static final String QUOTE_CREATED = "Quote_Created";
    public static final String AUTHORIZED_USER = "AUTHORIZED_USER";

    //    用于定义用户权限
    public static final String ALLIANCE_QUOTE_SETUP = "ALLIANCE_QUOTE_SETUP";
    public static final String BROKER_QUOTE_SETUP = "BROKER_QUOTE_SETUP";

    //定义缓存过期时间，按秒计算
    public static final long CACHE_TIMEOUT = 2 * 60 * 60;

    //定义查询相关常量
    public static final String OTHERS = "OTHERS";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    //矩阵或手机标记
    public static final String matrix="matrix";
    public static final String mobile="mobile";


    private Constant() {
    }
}