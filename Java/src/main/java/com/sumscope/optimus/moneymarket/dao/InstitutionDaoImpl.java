package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 接口实现类
 */
@Component
public class InstitutionDaoImpl implements InstitutionDao {

    private static final String QUERY_ALL = "com.sumscope.optimus.moneymarket.mapping.IdbFinancialCompanyDTOMapper.queryAll";
    @Autowired
    @Qualifier(value = Constant.BUSINESS_SQL_SESSION_TEMPLATE)
    private SqlSessionTemplate sqlSessionTemplate;

    public List<Institution> retrieveInstitutions() {
        return sqlSessionTemplate.selectList(QUERY_ALL);
    }

}
