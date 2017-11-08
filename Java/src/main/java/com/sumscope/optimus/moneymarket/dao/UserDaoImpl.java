package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.util.CollectionsUtil;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class UserDaoImpl implements UserDao {
    
    private static final String QUERY_ALL = "com.sumscope.optimus.moneymarket.mapping.UserDTOMapper.queryAll";
    private static final String RETRIEVE_QB_USER_BY_USERNAME = "com.sumscope.optimus.moneymarket.mapping.UserDTOMapper.retrieveQBUserByUserName";
    private static final java.lang.String RETRIEVE_ALL_QB_USERS = "com.sumscope.optimus.moneymarket.mapping.UserDTOMapper.retrieveAllQBUsers";
    private static final String RETRIEVE_QB_USER_BY_INSTITUTIONID = "com.sumscope.optimus.moneymarket.mapping.UserDTOMapper.retrieveQBUserByInstitutionId";

    @Autowired
    @Qualifier(value=Constant.BUSINESS_SQL_SESSION_TEMPLATE)
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 使用缓存标记，基础信息可以简单设置为1小时过期
     * @return 所有的带拼音的用户Dto
     */
    @Override
    public List<User> queryAllWithPinYin() {
        return sqlSessionTemplate.selectList(QUERY_ALL);
    }

    @Override
    public User retrieveQBUserByUserName(String username) {
         List<User> users = sqlSessionTemplate.selectList(RETRIEVE_QB_USER_BY_USERNAME,username);
        if(CollectionsUtil.isEmptyOrNullCollection(users)){
            return null;
        }else{
            return users.get(0);
        }
    }

    @Override
    public List<User> retrieveAllQBUsers() {
        return sqlSessionTemplate.selectList(RETRIEVE_ALL_QB_USERS);
    }


    @Override
    public List<User> retrieveQBUserByInstitutionId(String companyId) {
        return  sqlSessionTemplate.selectList(RETRIEVE_QB_USER_BY_INSTITUTIONID,companyId);
    }

}
