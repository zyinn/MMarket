package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmAllianceInstitution;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaoxu.wang on 2016/6/12.
 */
@Component
public class MmAllianceInstitutionDaoImpl implements MmAllianceInstitutionDao {
    private static final  String AUTHORIZE_SEARCH = "com.sumscope.optimus.moneymarket.mapping.MmAllianceInstitutionMapper.isRecordExist";
    private static final  String QUERY_INSTITUTIONIDS_AVALIABLE = "com.sumscope.optimus.moneymarket.mapping.MmAllianceInstitutionMapper.getAvailableInstitutionIds";
    private static final  String QUERY_INSTITUTIONIDS = "com.sumscope.optimus.moneymarket.mapping.MmAllianceInstitutionMapper.isPrimaryInstitutions";
    @Autowired
    @Qualifier(value = Constant.BUSINESS_SQL_SESSION_TEMPLATE)
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<String> getAvailableInstitutionIds(String institutionId) {
        return sqlSessionTemplate.selectList(QUERY_INSTITUTIONIDS_AVALIABLE, institutionId);
    }

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT)
    public boolean isAuthorizedForAllianceQuote(String userId) {
        return getUserAuthority(userId, Constant.ALLIANCE_QUOTE_SETUP);
    }

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT)
    public boolean isAuthorizedForBrokerQuote(String userId) {
        return getUserAuthority(userId, Constant.BROKER_QUOTE_SETUP);
    }

    @Override
    public String getPrimaryInstitution(String userInstitutionId) {
        return sqlSessionTemplate.selectOne(QUERY_INSTITUTIONIDS,userInstitutionId);
    }


    private boolean getUserAuthority(String userId, String useCase) {
        Map<String, String> param = new HashMap<>();
        param.put("userId", userId);
        param.put("useCase", useCase);
        MmAllianceInstitution mmAllianceInstitution = sqlSessionTemplate.selectOne(AUTHORIZE_SEARCH, param);
        if(mmAllianceInstitution!=null){
            return true;
        }
        return false;
    }
}
