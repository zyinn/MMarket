package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.util.CollectionsUtil;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteDetails;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan.bai on 2016/8/6.
 * MmQuoteDetailsDao实现类,由于存在业务数据库与历史数据库，因此该类实现两个数据库的公共功能
 */
abstract class AbstractMmQuoteDetailsDaoImpl implements MmQuoteDetailsDao {
    private static final String QUERY_QUORE_DETAILS_BY_QUOTE_IDS
            = "com.sumscope.optimus.moneymarket.mapping.MmQuoteDetailsMapper.queryMmQuoteDetailsByQuoteIDList";

    private static final String INSERT_MM_QUOTE_DETAILS_LIST
            = "com.sumscope.optimus.moneymarket.mapping.MmQuoteDetailsMapper.insertMmQuoteDetailsList";

    private static final String UPDATE_MM_QUOTE_DETAILS
            = "com.sumscope.optimus.moneymarket.mapping.MmQuoteDetailsMapper.updateMmQuoteDetails";


    private static final int MAX_LIST_SIZE_FOR_ONE_CALL = 200;

    /**
     * 业务数据库或者历史数据库的相关对象
     */
    protected abstract SqlSessionTemplate getSqlSessionTemplate();

    @Override
    public List<MmQuoteDetails> queryQuoteDetails(List<String> mmQuoteIds) {
        List<MmQuoteDetails> results = new ArrayList<>();
        if (mmQuoteIds == null || mmQuoteIds.size() == 0) {
            return results;
        }
        //当输入的报价单id列表太长时，分批处理。因为SQL的in语句在数值过多时效率可能很低
        while (mmQuoteIds.size() > MAX_LIST_SIZE_FOR_ONE_CALL) {
            List<String> subList = mmQuoteIds.subList(0, MAX_LIST_SIZE_FOR_ONE_CALL);
            results.addAll(getSqlSessionTemplate().selectList(QUERY_QUORE_DETAILS_BY_QUOTE_IDS, subList));
            subList.clear();
        }
        //分批后还有剩余（或根本就没有超出限制），仍然需要执行一次
        results.addAll(getSqlSessionTemplate().selectList(QUERY_QUORE_DETAILS_BY_QUOTE_IDS, mmQuoteIds));
        return results;

    }

    @Override
    public void insertQuoteDetails(List<MmQuoteDetails> details) {
        if (!CollectionsUtil.isEmptyOrNullCollection(details)) {
            getSqlSessionTemplate().insert(INSERT_MM_QUOTE_DETAILS_LIST, details);
        }

    }

    @Override
    public void updateQuoteDetails(List<MmQuoteDetails> detailsList) {
        if (detailsList != null) {
            for (MmQuoteDetails details : detailsList) {
                getSqlSessionTemplate().update(UPDATE_MM_QUOTE_DETAILS, details);
            }
        }

    }


}
