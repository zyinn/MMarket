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
 * 业务数据库报价单明细表Dao实现
 */
@Component
public class BusinessMmQuoteDetailsDaoImpl extends AbstractMmQuoteDetailsDaoImpl implements BusinessMmQuoteDetailsDao {
    private static final String DELETE_MM_QUOTES_DETAILS = "com.sumscope.optimus.moneymarket.mapping.MmQuoteDetailsMapper.deleteMmQuoteDetails";

    private static final String DEACTIVATE_EXPIRED_MM_QUOTE_DETAILS
            = "com.sumscope.optimus.moneymarket.mapping.MmQuoteDetailsMapper.deactivateExpiredMmQuoteDetails";

    @Autowired
    @Qualifier(value = Constant.BUSINESS_SQL_SESSION_TEMPLATE)
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    protected SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    @Override
    public void deleteDataFromDB(List<String> quoteIdList) {
        getSqlSessionTemplate().delete(DELETE_MM_QUOTES_DETAILS, quoteIdList);
    }

    @Override
    public void deactivateExpiredMmQuoteDetails(){
        getSqlSessionTemplate().update(DEACTIVATE_EXPIRED_MM_QUOTE_DETAILS);
    }
}
