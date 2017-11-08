package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.dao.UserDao;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fan.bai on 2016/5/11.
 * UserBaseService实现类
 */
@Service
public class UserBaseServiceImpl implements UserBaseService {


    @Autowired
    private UserDao userDao;

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false, notifyResolver = UserByUserIDCacheResolver.class)
    public Map<String, User> retreiveAllUsersGroupByUserID() {
        LogManager.debug("获取全部用户数据！");
        List<User> users = userDao.queryAllWithPinYin();
        Map<String, User> result = new HashMap<>();
        for (User user : users) {
            result.put(user.getUserId(), user);
        }
        return result;
    }

    @Override
    public User retrieveQBUserByUserName(String username) {
        return userDao.retrieveQBUserByUserName(username);
    }

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false, notifyResolver = UserByUserNameCacheResolver.class)
    public Map<String, User> retrieveAllQBUsersGroupByUserName() {
        LogManager.debug("获取全部QB用户数据并按username建立map！");
        Map<String, User> result = new HashMap<>();
        List<User> allQBUsers = userDao.retrieveAllQBUsers();
        for (User user : allQBUsers) {
            result.put(user.getUserName(), user);
        }
        return result;
    }

    @Override
    public List<User> retrieveQbUsersGroupByInstitutionId(String institutionId){
        return userDao.retrieveQBUserByInstitutionId(institutionId);
    }

}
