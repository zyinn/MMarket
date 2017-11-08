package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteSource;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.commons.util.CollectionsUtil;
import com.sumscope.optimus.moneymarket.commons.util.QuoteDateUtils;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by fan.bai on 2016/8/6.
 * 接口实现类,由于存在业务数据库与历史数据库，因此该类实现两个数据库的公共功能
 */
abstract class AbstractMmQuoteDaoImpl implements MmQuoteDao {
    private static final String QUERY_QUORE_MAIN = "com.sumscope.optimus.moneymarket.mapping.MmQuoteMapper.queryMmQuoteMain";
    private static final String QUERY_QUORE_MAIN_WITH_DETAILS = "com.sumscope.optimus.moneymarket.mapping.MmQuoteMapper.queryMmQuoteDetails";
    private static final String INSERT_MM_QUOTES = "com.sumscope.optimus.moneymarket.mapping.MmQuoteMapper.insertMmQuotes";
    private static final String UPDATE_MM_QUOTE = "com.sumscope.optimus.moneymarket.mapping.MmQuoteMapper.updateMmQuote";
    private static final String DEACTIVATE_MM_QUOTES = "com.sumscope.optimus.moneymarket.mapping.MmQuoteMapper.deactivateMmQuote";

    /**
     * 业务数据库或者历史数据库的相关对象
     */
    protected abstract SqlSessionTemplate getSqlSessionTemplate();

    @Override
    public List<MmQuote> queryMmQuoteMain(MmQuoteMainQueryParameters parameters) {
        normalizeParameter(parameters);
        List<MmQuote> readOnlyResults = getSqlSessionTemplate().selectList(QUERY_QUORE_MAIN, parameters);
        //在Mybatis的二级缓存中我们使用了ReadOnly = true模式，该模式表示所有通过缓存获取的对象都是同一份！
        //由于在后期我们需要对quoteDetails和quoteTags属性进行写入操作（获取明细后需要写入该MmQuote对象），如果不进行
        //任何处理则在多线程情况下由于多个线程同时向一份MmQuote对象的details属性写入数据时必定出现同步错误。
        //因此在本处我们通过构造一个新的MmQuote对象来避免并发错误。
        //如果不使用ReadOnly = true模式则无需该处理，然而此时二级缓存将进入同步模式，对高并发时的性能有很大影响。
        List<MmQuote> processableResults = new ArrayList<>();
        for(MmQuote quote: readOnlyResults){
            MmQuote quoteToProcess = new MmQuote();
            BeanUtils.copyProperties(quote,quoteToProcess);
            processableResults.add(quoteToProcess);
        }
        return processableResults;
    }

    @Override
    public List<MmQuoteDetailsQueryResult> queryMmQuoteWithDetails(MmQuoteDetailsQueryParameters parameters) {
        normalizeParameter(parameters);
        return getSqlSessionTemplate().selectList(QUERY_QUORE_MAIN_WITH_DETAILS, parameters);
    }

    /**
     * 在XML中，我们使用 bankNature == null 来判断是否设置了列表值，并进行where语句的组装。 在xml文件中不再进行
     * .size() == 0 的判断，因为在某些并发情况下，对size的判断会出现无法解释的空指针异常。
     * 因此在本方法中，对空的但非null的数值进行判断，如果是空则设置该值为null。
     */
    private void normalizeParameter(AbstractMmQuoteQueryParameters parameters) {
        if (StringUtils.isEmpty(parameters.getQuoteUserId())) {
            parameters.setQuoteUserId(null);
        }
        if (StringUtils.isEmpty(parameters.getMemo())) {
            parameters.setMemo(null);
        }
        if (CollectionsUtil.isEmptyOrNullCollection(parameters.getBankNatures())) {
            parameters.setBankNatures(null);
        }
        if (CollectionsUtil.isEmptyOrNullCollection(parameters.getInstitutionIdList())) {
            parameters.setInstitutionIdList(null);
        }
        if (CollectionsUtil.isEmptyOrNullCollection(parameters.getQuoteIdList())) {
            parameters.setQuoteIdList(null);
        }
        if (CollectionsUtil.isEmptyOrNullCollection(parameters.getAreas())) {
            parameters.setAreas(null);
        }
        if (CollectionsUtil.isEmptyOrNullCollection(parameters.getFundSizes())) {
            parameters.setFundSizes(null);
        }
        if (CollectionsUtil.isEmptyOrNullCollection(parameters.getQuoteTypes())) {
            parameters.setQuoteTypes(null);
        }
        if (CollectionsUtil.isEmptyOrNullCollection(parameters.getTagCodes())) {
            parameters.setTagCodes(null);
        }
        if (parameters.getCalculationTime() != null) {
            parameters.setCalculationTime(QuoteDateUtils.getMatrixCalculationTimeOfDate(parameters.getCalculationTime()));
        }

    }

    @Override
    public void insertMmQuotes(List<MmQuote> newQuotes) {
        validateMmQuotes(newQuotes);
        getSqlSessionTemplate().insert(INSERT_MM_QUOTES, newQuotes);

    }

    private void validateMmQuotes(List<MmQuote> newQuotes) {
        if (CollectionsUtil.isEmptyOrNullCollection(newQuotes)) {
            return;
        }
        for (MmQuote quotes : newQuotes) {
            if (quotes.getQuoteType() == null) {
                throw new BusinessRuntimeException(
                        BusinessRuntimeExceptionType.PARAMETER_INVALID, "报价类型非空");
            }
            if (quotes.getInstitutionId() == null) {
                throw new BusinessRuntimeException(
                        BusinessRuntimeExceptionType.PARAMETER_INVALID, "机构ID非空");
            }
            if (quotes.getQuoteUserId() == null) {
                throw new BusinessRuntimeException(
                        BusinessRuntimeExceptionType.PARAMETER_INVALID, "报价人ID非空");
            }
            if (quotes.getDirection() == null) {
                throw new BusinessRuntimeException(
                        BusinessRuntimeExceptionType.PARAMETER_INVALID, "报价方向非空");
            }
            if (quotes.getMethodType() == null) {
                throw new BusinessRuntimeException(
                        BusinessRuntimeExceptionType.PARAMETER_INVALID, "报价方法非空");
            }
            if (quotes.getSource() == null) {
                throw new BusinessRuntimeException(
                        BusinessRuntimeExceptionType.PARAMETER_INVALID, "报价来源非空");
            }
        }
    }

    @Override
    public void updateMmQuotes(MmQuote mmQuote) {
        getSqlSessionTemplate().update(UPDATE_MM_QUOTE, mmQuote);

    }

    @Override
    public void deactivateMmQuotes(Date yesterdayDate, List<String> idList) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("expiredDate", yesterdayDate);
        parameters.put("idList", idList);
        getSqlSessionTemplate().update(DEACTIVATE_MM_QUOTES, parameters);
    }
}
