package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.model.dbmodel.User;

import java.util.List;
import java.util.Map;

/**
 * Created by fan.bai on 2016/5/11.
 * 提供针对用户的查询服务,服务。一些服务进行了缓存。
 */
public interface UserBaseService {
    /**
     * 获取所有用户信息，以ID为key
     * @return 以ID为key的用户信息
     */
    Map<String,User> retreiveAllUsersGroupByUserID();

    /**
     * 按用户名读取用户信息，为保证实时性，应直接读取数据库数据
     * @return 按用户名查找获取的User数据。找不到返回null
     */
    User retrieveQBUserByUserName(String username);

    /**
     * @return 获取所有QB用户信息,以username为key
     */
    Map<String,User> retrieveAllQBUsersGroupByUserName();

    /**
     * 根据机构的ID获取机构下的用户
     * @param institutionId
     * @return 获取用户list
     */
     List<User> retrieveQbUsersGroupByInstitutionId(String institutionId);
}
