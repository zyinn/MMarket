package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.Constant;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by fan.bai on 2016/8/30.
 * 历史数据库报价单主表Dao实现类
 */
@Component
public class HistoryMmQuoteDaoImpl extends AbstractMmQuoteDaoImpl implements HistoryMmQuoteDao {

    @Autowired
    @Qualifier(value = Constant.HISTORY_SQL_SESSION_TEMPLATE)
    private SqlSessionTemplate sqlSessionTemplate;


    @Override
    protected SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }
}
