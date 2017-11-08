package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.enums.*;
import com.sumscope.optimus.moneymarket.commons.util.BigDecimalUtils;
import com.sumscope.optimus.moneymarket.commons.util.CollectionsUtil;
import com.sumscope.optimus.moneymarket.commons.util.TimePeroidUtils;
import com.sumscope.optimus.moneymarket.commons.util.UUIDUtils;
import com.sumscope.optimus.moneymarket.dao.MmQuotePriceTrendDao;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by fan.bai on 2016/9/19.
 * 接口实现类
 */
@Component
public class MatrixCalculationAndQueryServiceImpl implements MatrixCalcuationAndQueryService {
    private static double maxValidPrice = 6.0;
    @Autowired
    private MmQuoteQueryService mmQuoteQueryService;
    @Autowired
    private MmQuotePriceTrendDao mmQuotePriceTrendDao;

    /**
     * 计算矩阵数据比较耗时，也不必要每次调用都重新计算，因此使用@CacheMe标签进行缓存
     * 当有新的报价产生时，该方法的结果可能需要改变，具体说明请参见QuoteMatrixCacheResolver类说明
     */
    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false, notifyResolver = QuoteMatrixCacheResolver.class)
    public List<PriceTrend> calculateMatrixCells(Direction direction, QuoteType quoteType, Date time) {
        LogManager.debug("矩阵开始计算！:" + direction + ":" + quoteType);
        List<MmQuote> mmQuotes = searchMmQuotes(direction, quoteType, time);
        Map<String, PriceTrend> cells = initialMatrixCells(direction, quoteType, time);
        calculateCellResults(cells, mmQuotes);
        return new ArrayList<>(cells.values());
    }

    private List<MmQuote> searchMmQuotes(Direction direction, QuoteType quoteType, Date time) {
        MmQuoteDetailsQueryParameters parameters = new MmQuoteDetailsQueryParameters();
        parameters.setDirection(direction);
        parameters.setQuoteTypes(new ArrayList<>());
        parameters.getQuoteTypes().add(quoteType);
        parameters.setCalculationTime(time);
        return mmQuoteQueryService.queryMmQuoteDetails(parameters, false);
    }

    private Map<String, PriceTrend> initialMatrixCells(Direction direction, QuoteType quoteType, Date time) {
        Map<String, PriceTrend> results = new HashMap<>();
        buildTimePeriodsCells(results, direction, quoteType, CalculatedBankNature.BIG_BANK, MatrixFundSize.NONE, time);
        buildTimePeriodsCells(results, direction, quoteType, CalculatedBankNature.JOINT_STOCK, MatrixFundSize.NONE, time);
        buildTimePeriodsCells(results, direction, quoteType, CalculatedBankNature.CITY_COMMERCIAL_BANK, MatrixFundSize.LARGER_FIVE_T, time);
        buildTimePeriodsCells(results, direction, quoteType, CalculatedBankNature.CITY_COMMERCIAL_BANK, MatrixFundSize.ONE_FIVE_T, time);
        buildTimePeriodsCells(results, direction, quoteType, CalculatedBankNature.CITY_COMMERCIAL_BANK, MatrixFundSize.SMALLER_ONE_T, time);
        buildTimePeriodsCells(results, direction, quoteType, CalculatedBankNature.OTHERS, MatrixFundSize.NONE, time);
        return results;
    }

    private void buildTimePeriodsCells(Map<String, PriceTrend> cells, Direction direction, QuoteType quoteType, CalculatedBankNature matrixBankNature, MatrixFundSize matrixFundSize, Date time) {
        for (QuoteTimePeriod timePeriod : QuoteTimePeriod.values()) {
            if (timePeriod.isAttributeSupported(quoteType.getContactQuoteAttribute())) {
                String key = buildKey(direction, quoteType, matrixBankNature, matrixFundSize, timePeriod);
                PriceTrend cell = new PriceTrend();
                cell.setDirection(direction);
                cell.setQuoteType(quoteType);
                cell.setMatrixBankNature(matrixBankNature);
                cell.setMatrixFundSize(matrixFundSize);
                cell.setCreateTime(time);
                cell.setTimePeriod(timePeriod);
                cells.put(key, cell);
            }
        }
    }

    private void calculateCellResults(Map<String, PriceTrend> cells, List<MmQuote> mmQuotes) {
        for (MmQuote mmQuote : mmQuotes) {
            Direction quoteDirection = mmQuote.getDirection();
            QuoteType quoteType = mmQuote.getQuoteType();
            CalculatedBankNature quoteMatrixBankNature = getMatrixBankNature(mmQuote.getBankNature());
            MatrixFundSize quoteMatrixFundSize = getMatrixFundSize(mmQuote.getFundSize(), quoteMatrixBankNature);
            for (MmQuoteDetails mmQuoteDetails : mmQuote.getMmQuoteDetails()) {
                QuoteTimePeriod periodFromValue = TimePeroidUtils.getPeriodFromValue(mmQuoteDetails.getDaysLow(), mmQuoteDetails.getDaysHigh());
                if (periodFromValue == null) {
                    List<QuoteTimePeriod> affectTimePeroid = TimePeroidUtils.getAffectTimePeroid(mmQuoteDetails.getDaysLow(), mmQuoteDetails.getDaysHigh());
                    if (!CollectionsUtil.isEmptyOrNullCollection(affectTimePeroid)) {
                        for (QuoteTimePeriod timePeriod : affectTimePeroid) {
                            calculateSingleQuoteDetails(cells, quoteDirection, quoteType, quoteMatrixBankNature, quoteMatrixFundSize, timePeriod, mmQuoteDetails);
                        }
                    }
                } else {
                    calculateSingleQuoteDetails(cells, quoteDirection, quoteType, quoteMatrixBankNature, quoteMatrixFundSize, periodFromValue, mmQuoteDetails);
                }
            }
        }

    }

    //根据报价单对应的banknature数值查找对应的枚举
    private CalculatedBankNature getMatrixBankNature(String bankNature) {
        if (StringUtils.isNotBlank(bankNature)) {
            for (CalculatedBankNature calculatedBankNature : CalculatedBankNature.values()) {
                if (bankNature.equals(calculatedBankNature.getBankNatureCode())) {
                    //矩阵报价中没有农商行,后决定将农商行的行情报价直接合并到对应的一定规模中的城商行中.
                    if (calculatedBankNature.equals(CalculatedBankNature.RURAL_CREDIT)) {  //如果该机构为农商行,就将他设置成城商行返回,否则返回具体的机构类型
                        return CalculatedBankNature.CITY_COMMERCIAL_BANK;
                    } else {
                        return calculatedBankNature;
                    }
                }
            }
        }
        return CalculatedBankNature.OTHERS;
    }

    //根据需求，仅计算城商行的规模
    private MatrixFundSize getMatrixFundSize(BigDecimal fundSize, CalculatedBankNature matrixBankNature) {
        if (matrixBankNature == CalculatedBankNature.CITY_COMMERCIAL_BANK) {
            for (MatrixFundSize matrixFundSize : MatrixFundSize.values()) {
                long fundSizeD = fundSize == null ? 0 : fundSize.longValue();
                if (matrixFundSize.getFundSizeLow() <= fundSizeD && matrixFundSize.getFundSizeHigh() >= fundSizeD) {
                    return matrixFundSize;
                }
            }
            return MatrixFundSize.SMALLER_ONE_T;
        }
        return MatrixFundSize.NONE;
    }

    //根据初始化之后的结果集进行计算。按参数key值获取对应的已初始化完成的结果，再判断极值
    private void calculateSingleQuoteDetails(Map<String, PriceTrend> cells, Direction quoteDirection, QuoteType quoteType, CalculatedBankNature quoteMatrixBankNature, MatrixFundSize quoteMatrixFundSize, QuoteTimePeriod timePeriod, MmQuoteDetails mmQuoteDetails) {
        String key = buildKey(quoteDirection, quoteType, quoteMatrixBankNature, quoteMatrixFundSize, timePeriod);
        PriceTrend priceTrend = cells.get(key);
        if (priceTrend != null && mmQuoteDetails.isActive()) {
            BigDecimal priceLow = mmQuoteDetails.getPriceLow();
            priceLow = checkPrice(priceLow);
            if (priceLow != null) {
                if (priceTrend.getPriceLow() != null) {
                    if (BigDecimalUtils.compareDoubleValue(priceLow, priceTrend.getPriceLow()) < 0) {
                        priceTrend.setPriceLow(priceLow);
                    }
                } else {
                    priceTrend.setPriceLow(priceLow);
                }
            }
            BigDecimal priceHigh = mmQuoteDetails.getPrice();
            priceHigh = checkPrice(priceHigh);
            if (priceHigh != null) {
                if (priceTrend.getPriceHigh() != null) {
                    if (BigDecimalUtils.compareDoubleValue(priceHigh, priceTrend.getPriceHigh()) > 0) {
                        priceTrend.setPriceHigh(priceHigh);
                    }
                } else {
                    priceTrend.setPriceHigh(priceHigh);
                }
            }
        }


    }



    //由于存在自动识别以及一些人为错误等潜在错误源,报价的数据有可能出现不合理的数值,这些数值对于单一报价单而言问题不大,
    //但是对于历史数据分析则有危害,因此需要对不合理的报价数据进行检查和剔除.
    private BigDecimal checkPrice(BigDecimal price) {
        if (price != null && price.doubleValue() < maxValidPrice) {
            return price;
        }
        return null;
    }

    private String buildKey(Direction quoteDirection, QuoteType quoteType, CalculatedBankNature quoteMatrixBankNature, MatrixFundSize quoteMatrixFundSize, QuoteTimePeriod timePeriod) {
        String timePS = timePeriod == null ? "NULL" : timePeriod.name();
        return quoteDirection.name() + ":" + quoteType.name() + ":" + quoteMatrixBankNature.name() + ":" + quoteMatrixFundSize.name() + ":" + timePS;
    }


    @Override
    public List<PriceTrend> queryPriceTrends(PriceTrendsQueryParameters parameters) {
        return mmQuotePriceTrendDao.queryPriceTrends(parameters);
    }

    @Override
    @Transactional(value = Constant.BUSINESS_TRANSACTION_MANAGER)
    public void takeSnapshotForMatrix(Date time) {
        List<PriceTrend> priceTrends = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            for (QuoteType quoteType : QuoteType.values()) {
                //由于是在一个类中，因此不会调用到缓存的数据。这也正是我们希望的。
                priceTrends.addAll(calculateMatrixCells(direction, quoteType, time));
            }
        }
        priceTrends = removeNullValueItems(priceTrends);
        persistencePriceTrends(time, priceTrends);
    }

    private List<PriceTrend> removeNullValueItems(List<PriceTrend> priceTrends) {
        List<PriceTrend> result = new ArrayList<>();
        for (PriceTrend priceTrend : priceTrends) {
            if (priceTrend.getPriceHigh() != null && priceTrend.getPriceLow() != null) {
                result.add(priceTrend);
            }
        }
        return result;
    }

    private void persistencePriceTrends(Date time, List<PriceTrend> priceTrends) {
        //先删除上次生成的数据-如果存在
        mmQuotePriceTrendDao.deleteByTime(time);

        if (!CollectionsUtil.isEmptyOrNullCollection(priceTrends)) {
            //插入数据库
            for (PriceTrend trend : priceTrends) {
                trend.setId(UUIDUtils.generatePrimaryKey());
            }
            mmQuotePriceTrendDao.insertPriceMatrixCells(priceTrends);
        }
    }
}
