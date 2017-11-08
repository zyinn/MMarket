package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.model.dbmodel.User;

public interface AuthorizationService {
    
    /**
     * 认证用户。成功返回用户对象，失败返回null
     * 
     * @param username
     * @param password
     * @return
     */
    User authorizeUser(String username, String password);

    /**
     * 验证用户
     * @param username 用户账号
     * @param plainPassword 密码明文
     * @return 成功返回User，否则null
     */
    User validateUser(String username, String plainPassword);
    
}
