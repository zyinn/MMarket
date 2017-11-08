package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.util.CollectionsUtil;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteTag;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan.bai on 2016/8/6.
 * MmQuoteTagDao 的实现类,由于存在业务数据库与历史数据库，因此该类实现两个数据库的公共功能
 */
abstract class AbstractMmQuoteTagDaoImpl implements MmQuoteTagDao {

    private static final int MAX_LIST_SIZE_FOR_ONE_CALL = 200;

    private static final String QUERY_QUOTE_TAGS_BY_QUOTE_IDS
            = "com.sumscope.optimus.moneymarket.mapping.MmQuoteTagMapper.queryMmQuoteTagsByQuoteIDList";
    private static final String INSERT_QUOTE_TAGS
            = "com.sumscope.optimus.moneymarket.mapping.MmQuoteTagMapper.insertQuoteTags";
    private static final String DELETE_QUOTE_TAGS_FOR_QUOTES
            = "com.sumscope.optimus.moneymarket.mapping.MmQuoteTagMapper.deleteTagsForQuotes";


    abstract protected SqlSessionTemplate getSqlSessionTemplate();

    @Override
    public List<MmQuoteTag> queryMmQuoteTags(List<String> quoteIds) {
        List<MmQuoteTag> results = new ArrayList<>();
        if (quoteIds == null || quoteIds.size() == 0) {
            return results;
        }
        //当输入的报价单id列表太长时，分批处理。因为SQL的in语句在数值过多时效率可能很低
        while (quoteIds.size() > MAX_LIST_SIZE_FOR_ONE_CALL) {
            List<String> subList = quoteIds.subList(0, MAX_LIST_SIZE_FOR_ONE_CALL);
            results.addAll(getSqlSessionTemplate().selectList(QUERY_QUOTE_TAGS_BY_QUOTE_IDS, subList));
            subList.clear();
        }
        //分批后还有剩余，仍然需要执行一次
        results.addAll(getSqlSessionTemplate().selectList(QUERY_QUOTE_TAGS_BY_QUOTE_IDS, quoteIds));
        return results;

    }

    @Override
    public void insertMmQuoteTags(List<MmQuoteTag> mmQuoteTags) {
        if (!CollectionsUtil.isEmptyOrNullCollection(mmQuoteTags)) {
            getSqlSessionTemplate().insert(INSERT_QUOTE_TAGS, mmQuoteTags);
        }

    }


    @Override
    public void deleteTagsFromQuotes(List<String> quoteToDeleteTags) {
        if (quoteToDeleteTags != null) {
            getSqlSessionTemplate().delete(DELETE_QUOTE_TAGS_FOR_QUOTES, quoteToDeleteTags);
        }
    }
}
