package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.dao.MmTagDao;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fan.bai on 2016/8/6.
 * MmTagService的实现类
 */
@Component
public class MmTagServiceImpl implements MmTagService {
    @Autowired
    private MmTagDao mmTagDao;

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false)
    public Map<String, MmTag> retrieveAllTags() {
        List<MmTag> mmTags = mmTagDao.retrieveAvailableTags();
        Map<String, MmTag> results = new HashMap<>();
        for (MmTag mmTag : mmTags) {
            results.put(mmTag.getTagCode(), mmTag);
        }
        return results;
    }
}
