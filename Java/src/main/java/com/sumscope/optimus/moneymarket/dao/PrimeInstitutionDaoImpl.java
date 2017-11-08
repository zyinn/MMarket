package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.Contact;
import com.sumscope.optimus.moneymarket.model.dbmodel.PrimeCompany;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Created by fan.bai on 2016/5/12.
 * PrimeInstitutionDao 实现类
 */
@Component
public class PrimeInstitutionDaoImpl implements PrimeInstitutionDao {

    private static final String RETRIEVE_ALL_PRIME_INSTITUTIONS = "com.sumscope.optimus.moneymarket.mapping.PrimeInstitutionMapper.retrieveAll";
    private static final String RETRIEVE_ALL_PRIME_CONTACTS = "com.sumscope.optimus.moneymarket.mapping.PrimeInstitutionMapper.retrieveAllPrimeInstitutionContacts";

    @Autowired
    @Qualifier(value= Constant.BUSINESS_SQL_SESSION_TEMPLATE)
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<PrimeCompany> retrieveAllPrimeCompanys() {
        return sqlSessionTemplate.selectList(RETRIEVE_ALL_PRIME_INSTITUTIONS);
    }
    @Override
    public List<Contact> retrieveAllPrimeContacts() {
        return sqlSessionTemplate.selectList(RETRIEVE_ALL_PRIME_CONTACTS);
    }

}
