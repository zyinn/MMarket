package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmTag;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by fan.bai on 2016/8/6.
 * 接口实现类
 */
@Component
public class MmTagDaoImpl implements MmTagDao {
    private static final String QUERY_ALL_TAGS = "com.sumscope.optimus.moneymarket.mapping.MmTagMapper.retrieveMmTags";

    @Autowired
    @Qualifier(value= Constant.BUSINESS_SQL_SESSION_TEMPLATE)
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<MmTag> retrieveAvailableTags() {
        return sqlSessionTemplate.selectList(QUERY_ALL_TAGS);
    }
}
