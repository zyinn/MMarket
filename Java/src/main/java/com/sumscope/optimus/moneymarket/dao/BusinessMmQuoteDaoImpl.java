package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.Constant;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by fan.bai on 2016/8/30.
 * * 业务数据库报价单主表Dao实现
 */
@Component
public class BusinessMmQuoteDaoImpl extends AbstractMmQuoteDaoImpl implements BusinessMmQuoteDao {

    private static final String DELETE_MM_QUOTES = "com.sumscope.optimus.moneymarket.mapping.MmQuoteMapper.deleteMmQuote";
    private static final String RETRIEVE_QUOTE_USERS_ID = "com.sumscope.optimus.moneymarket.mapping.MmQuoteMapper.retrieveQuoteUsersID";
    private static final String RETRIEVE_QUOTE_INSTITUTIONS_ID = "com.sumscope.optimus.moneymarket.mapping.MmQuoteMapper.retrieveQuoteInstitutionsID";


    @Autowired
    @Qualifier(value = Constant.BUSINESS_SQL_SESSION_TEMPLATE)
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    protected SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    @Override
    public void deleteDataFromDB(List<String> quoteIdList) {
        getSqlSessionTemplate().delete(DELETE_MM_QUOTES, quoteIdList);
    }

    @Override
    public List<String> retrieveQuoteUsersID() {
        return sqlSessionTemplate.selectList(RETRIEVE_QUOTE_USERS_ID);
    }

    @Override
    public List<String> retrieveQuoteInstitutionsID() {
        return sqlSessionTemplate.selectList(RETRIEVE_QUOTE_INSTITUTIONS_ID);

    }
}
