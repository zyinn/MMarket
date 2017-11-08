package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.model.dbmodel.User;

import java.util.List;

/**
 * Created by fan.bai on 2016/4/29.
 * 这个Dao类型提供所有用户的retrieve，该方法将被缓存。
 */
public interface UserDao {
    
    List<User> queryAllWithPinYin();

    /**
     * 按username查询用户，找不到返回null
     */
    User retrieveQBUserByUserName(String username);

    /**
     * @return 获取所有QB用户信息
     */
    List<User> retrieveAllQBUsers();

    /**
     * 根据机构ID查询该机构的用户
     * @param InstitutionId
     * @return 获取QB用户列表
     */
    List<User> retrieveQBUserByInstitutionId(String InstitutionId);
}
