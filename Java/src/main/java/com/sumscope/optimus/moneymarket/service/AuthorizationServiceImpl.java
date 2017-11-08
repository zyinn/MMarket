package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.SSCacheManagementFactory;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.util.MD5;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthorizationServiceImpl implements AuthorizationService {

    @Autowired
    private UserBaseService userService;

    public User validateUser(String username, String plainPassword) {
        String password = MD5.md5s(plainPassword);
        return authorizeUser(username, password);
    }

    public User authorizeUser(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            LogManager.warn("fail to authorize because username or password is blank.");
            return null;
        }

        //首先从有缓存的方法获取用户信息，
        Map<String, User> qbUserMap = userService.retrieveAllQBUsersGroupByUserName();
        User account = qbUserMap.get(username);

        if(account == null || (account.getPassword()!=null && !account.getPassword().equals(password))){
            // 若不成功，则再次试图直接从数据库查询用户信息，因为可能是新客户，或者用户密码改变了
            account = userService.retrieveQBUserByUserName(username);
            //可能是新增用户，通知对应于retrieveQBUserByUserName方法的缓存更新
            if(account !=null){
                SSCacheManagementFactory.notifyUpdateResult(account, Constant.AUTHORIZED_USER);
            }
        }

        if (account == null) {
            LogManager.warn("fail to authorize because can not find qb user by username["
                    + username + "].");
            return null;
        }
        // 验证密码
        if (!(StringUtils.isNotBlank(account.getPassword()) && account
                .getPassword().equals(password))) {
            LogManager.warn("fail to authorize bacause password is wrong.");
            return null;
        }
        return account;
    }

}
