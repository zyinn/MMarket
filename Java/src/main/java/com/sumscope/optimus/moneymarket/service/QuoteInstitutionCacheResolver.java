package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.CachedDataContainer;
import com.sumscope.optimus.commons.cachemanagement.SSCacheResolver;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by fan.bai on 2016/9/7.
 * 说明见QuoteUsersCacheResolver
 */
class QuoteInstitutionCacheResolver implements SSCacheResolver<Institution> {
    private static Set<Class> acceptedResultsClass = new HashSet<>();

    static {
        acceptedResultsClass.add(Institution.class);
    }

    @Override
    public boolean isCacheable(Object[] params) {
        return true;
    }

    @Override
    public void updateCache(CachedDataContainer container, Institution convertedChangedData, String source, Object[] params) {
        if (Constant.QUOTE_CREATED.equals(source) && convertedChangedData != null) {
            Map<String, Institution> cachedData = (Map<String, Institution>) container.getCachedData();
            cachedData.put(convertedChangedData.getId(), convertedChangedData);
        }
    }

    @Override
    public boolean isUpdateNeeded(Institution convertedChangedData, Object[] params) {
        return true;
    }

    @Override
    public Institution convertToCachedType(Object changedData) {
        return (Institution) changedData;
    }

    @Override
    public Set<Class> acceptedResultTypes() {
        return acceptedResultsClass;
    }
}
