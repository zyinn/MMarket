package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.model.dbmodel.MmTag;

import java.util.List;

/**
 * Created by fan.bai on 2016/8/5.
 * 用于处理有关标签字典数据的Dao层接口
 */
public interface MmTagDao {
    /**
     * 从标签字典表中获取所有标签列表
     * @return 标签列表
     */
    List<MmTag> retrieveAvailableTags();


}
