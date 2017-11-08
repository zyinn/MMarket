package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.Constant;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by fan.bai on 2016/8/30.
 * 报价单标签相关Dao实现，用于业务数据库
 */
@Component
public class BusinessMmQuoteTagDaoImpl extends AbstractMmQuoteTagDaoImpl implements BusinessMmQuoteTagDao {

    @Autowired
    @Qualifier(value = Constant.BUSINESS_SQL_SESSION_TEMPLATE)
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    protected SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }
}
