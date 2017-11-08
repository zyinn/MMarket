package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.CachedDataContainer;
import com.sumscope.optimus.commons.cachemanagement.SSCacheResolver;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by fan.bai on 2016/9/6.
 * 系统缓存了用户信息。当新的用户信息通过其他方式获取后，会出发缓存更新类，刷新已经缓存的用户信息
 * 该类用户刷新以用户userID作为key值的并且返回值类型为Map的缓存结果
 */
public class UserByUserIDCacheResolver implements SSCacheResolver<User> {
    private static Set<Class> acceptedResultsClass = new HashSet<>();

    static {
        acceptedResultsClass.add(User.class);
    }

    @Override
    public Set<Class> acceptedResultTypes() {
        return acceptedResultsClass;
    }

    @Override
    public boolean isCacheable(Object[] params) {
        return true;
    }

    @Override
    public void updateCache(CachedDataContainer container, User convertedChangedData, String source, Object[] params) {
        // 见 UserByUserNameCacheResolver同方法说明
        if (Constant.AUTHORIZED_USER.equals(source)) {
            Object cachedData = container.getCachedData();
            Map<String, User> cachedValues = (Map<String, User>) cachedData;
            cachedValues.put(convertedChangedData.getUserId(), convertedChangedData);
        }

    }

    @Override
    public boolean isUpdateNeeded(User convertedChangedData, Object[] params) {
        return true;
    }

    @Override
    public User convertToCachedType(Object changedData) {
        return (User) changedData;
    }
}
