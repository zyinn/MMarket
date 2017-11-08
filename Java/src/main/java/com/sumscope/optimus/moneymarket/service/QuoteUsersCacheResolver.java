package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.CachedDataContainer;
import com.sumscope.optimus.commons.cachemanagement.SSCacheResolver;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by fan.bai on 2016/9/7.
 * 对用户和机构的查询的范围仅为报价人及报价机构。系统使用缓存记录了当前的报价人及机构列表
 * 然而当有新增报价时，可能需要更新该列表，本缓存更新类用于该功能。
 */
class QuoteUsersCacheResolver implements SSCacheResolver<User> {

    private static Set<Class> acceptedResultsClass = new HashSet<>();

    static {
        acceptedResultsClass.add(User.class);
    }

    @Override
    public boolean isCacheable(Object[] params) {
        return true;
    }

    @Override
    public void updateCache(CachedDataContainer container, User convertedChangedData, String source, Object[] params) {
        //系统总线会获取报价单的改变并发出相应的缓存更新命令
        if (Constant.QUOTE_CREATED.equals(source) && convertedChangedData != null) {
            Map<String, User> cachedData = (Map<String, User>) container.getCachedData();
            cachedData.put(convertedChangedData.getUserId(), convertedChangedData);
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

    @Override
    public Set<Class> acceptedResultTypes() {
        return acceptedResultsClass;
    }
}
