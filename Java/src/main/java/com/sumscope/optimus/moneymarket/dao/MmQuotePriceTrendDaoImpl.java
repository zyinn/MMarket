package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.PriceTrend;
import com.sumscope.optimus.moneymarket.model.dbmodel.PriceTrendsQueryParameters;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by fan.bai on 2016/9/22.
 * MmQuotePriceTrendDao实现类
 */
@Component
public class MmQuotePriceTrendDaoImpl implements MmQuotePriceTrendDao {
    private static final String RETRIEVE_MM_QUOTE_PRICE_TRENDS = "com.sumscope.optimus.moneymarket.mapping.MmQuotePriceTrendsMapper.retrieveMmQuotePriceTrends";
    private static final String DELETE_PRICE_TRENDS_BY_TIME = "com.sumscope.optimus.moneymarket.mapping.MmQuotePriceTrendsMapper.deleteMmQuotePriceTrendsByTime";
    private static final String INSERT_MM_QUOTE_PRICE_TRENDS = "com.sumscope.optimus.moneymarket.mapping.MmQuotePriceTrendsMapper.insertMmQuotePriceTrends";

    @Autowired
    @Qualifier(value = Constant.BUSINESS_SQL_SESSION_TEMPLATE)
    private SqlSessionTemplate sqlSessionTemplate;


    @Override
    public void deleteByTime(Date createTime) {
        sqlSessionTemplate.delete(DELETE_PRICE_TRENDS_BY_TIME, createTime);
    }

    @Override
    public void insertPriceMatrixCells(List<PriceTrend> trends) {
        sqlSessionTemplate.insert(INSERT_MM_QUOTE_PRICE_TRENDS, trends);

    }

    @Override
    public List<PriceTrend> queryPriceTrends(PriceTrendsQueryParameters parameters) {
        return sqlSessionTemplate.selectList(RETRIEVE_MM_QUOTE_PRICE_TRENDS, parameters);
    }
}
