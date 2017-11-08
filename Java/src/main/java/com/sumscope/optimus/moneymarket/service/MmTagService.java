package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.model.dbmodel.MmTag;

import java.util.Map;

/**
 * Created by fan.bai on 2016/8/6.
 * 有关标签的服务，该服务仅提供标签元数据相关服务，与报价单相关的标签服务在对应服务中实现。
 */
public interface MmTagService {
    /**
     * 获取所有标签，该方法将使用缓存
     *
     * @return 所有标签的一个Map, key为标签code，value为MmTag数据
     */
    Map<String, MmTag> retrieveAllTags();
}
