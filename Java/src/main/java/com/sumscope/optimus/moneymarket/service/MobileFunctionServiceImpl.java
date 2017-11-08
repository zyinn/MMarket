package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.commons.enums.ContactQuoteAttribute;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;
import com.sumscope.optimus.moneymarket.commons.util.TimePeroidUtils;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import com.sumscope.optimus.moneymarket.model.dto.MobileMatrixParameterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 向手机理财应用提供服务的Service
 * Created by huaijie.chen on 2016/7/21.
 */
@Component
public class MobileFunctionServiceImpl implements MobileFunctionService {
    @Autowired
    private InstitutionService institutionService;
    @Autowired
    private MmQuoteQueryService mmQuoteQueryService;

    /**
     * Mobile大厅矩阵数据
     *
     * @param matrixParameter
     * @return
     */
    @Override
    public List<MobileMatrixResult> getMobileMatrix(MobileMatrixParameterDto matrixParameter) {
        //初始化矩阵数据
        List<MobileMatrixResult> result = initMobileMatrixList();
        MmQuoteDetailsQueryParameters parameters = new MmQuoteDetailsQueryParameters();
        parameters.setDirection(matrixParameter.getDirection());
        parameters.setQuoteTypes(new ArrayList<>());
        parameters.getQuoteTypes().add(matrixParameter.getQuoteType());
        parameters.setOnlyValidQuotes(true);
        parameters.setPrimeQuotesOnly(true);
        //获取报价明细记录
        List<MmQuoteDetailsQueryResult> mmQuoteDetailsQueryResults = mmQuoteQueryService.queryMmQuoteDetails(parameters);
        if (mmQuoteDetailsQueryResults != null && mmQuoteDetailsQueryResults.size() > 0) {
            //处理报价明细
            result = caculateMatrix(result, mmQuoteDetailsQueryResults);
        }
        return result;
    }

    /**
     * 初始化矩阵列表返回数据
     *
     * @return
     */
    private List<MobileMatrixResult> initMobileMatrixList() {
        List<MobileMatrixResult> mobileMatrixResults = new ArrayList<>();
        for (QuoteTimePeriod quoteTimePeriod : QuoteTimePeriod.values()) {
            //[MR-231]矩阵中不应出现1D的数据
            if(QuoteTimePeriod.T1D != quoteTimePeriod){
                MobileMatrixResult mobileMatrixResult = new MobileMatrixResult();
                mobileMatrixResult.setTimePeriod(quoteTimePeriod);
                mobileMatrixResults.add(mobileMatrixResult);
            }
        }
        return mobileMatrixResults;
    }

    /**
     * 计算获取矩阵的结果
     *
     * @param mobileMatrixResults
     * @param mmQuoteDetailsQueryResults
     * @return
     */
    private List<MobileMatrixResult> caculateMatrix(List<MobileMatrixResult> mobileMatrixResults, List<MmQuoteDetailsQueryResult> mmQuoteDetailsQueryResults) {
        final BigDecimal ONE_HUNDRED_BILLION = new BigDecimal(10000000L);//一千亿（万）
        final BigDecimal FIVE_HUNDRED_BILLION = new BigDecimal(50000000L);//五千亿（万）
        /**
         * 循环List<MmQuoteDetailsQueryResult>
         */
        for (MmQuoteDetailsQueryResult mmQuoteDetailsQueryResult : mmQuoteDetailsQueryResults) {
            int detailDayLow = mmQuoteDetailsQueryResult.getDaysLow();//期限低值
            int detailDayHigh = mmQuoteDetailsQueryResult.getDaysHigh();//期限高值
            BigDecimal fundSize = mmQuoteDetailsQueryResult.getFundSize();//机构规模
            BigDecimal priceHigh = mmQuoteDetailsQueryResult.getPrice();//价格的高值
            BigDecimal priceLow = mmQuoteDetailsQueryResult.getPriceLow();//价格的低值
            //获取受影响的QuoteTimePeriod的记录
            List<QuoteTimePeriod> affectTimePeroids =  TimePeroidUtils.getAffectTimePeroid(detailDayLow,detailDayHigh);
            /**
             * 循环List<MobileMatrixResponseDto>
             */
            for (QuoteTimePeriod quoteTimePeriod : affectTimePeroids) {
                for (MobileMatrixResult mobileMatrixResult : mobileMatrixResults) {
                    if (quoteTimePeriod == mobileMatrixResult.getTimePeriod()) {
                        if( fundSize == null){
                            fundSize = new BigDecimal(0L);
                        }
                        if (fundSize.doubleValue() < ONE_HUNDRED_BILLION.doubleValue()) {//机构规模在一千亿以下的
                            mobileMatrixResult.setSmallerThenOneTL(getExtreamValue(mobileMatrixResult.getSmallerThenOneTL(),priceLow,true));
                            mobileMatrixResult.setSmallerThenOneTH(getExtreamValue(mobileMatrixResult.getSmallerThenOneTH(),priceHigh,false)); //价格高值不为空 填入
                        } else if (fundSize.doubleValue() > FIVE_HUNDRED_BILLION.doubleValue()) {//机构规模在五千亿以上的
                            mobileMatrixResult.setLargerThenFiveTL(getExtreamValue(mobileMatrixResult.getLargerThenFiveTL(),priceLow,true));
                            mobileMatrixResult.setLargerThenFiveTH(getExtreamValue(mobileMatrixResult.getLargerThenFiveTH(),priceHigh,false));
                        } else {//机构规模在一千亿至五千亿之间的
                            mobileMatrixResult.setBetweenOneTFiveTL(getExtreamValue(mobileMatrixResult.getBetweenOneTFiveTL(),priceLow,true));
                            mobileMatrixResult.setBetweenOneTFiveTH(getExtreamValue(mobileMatrixResult.getBetweenOneTFiveTH(),priceHigh,false));
                        }
                    }
                }
            }
        }
        return mobileMatrixResults;
    }

    /**
     * 返回值是输入两个值中的最大值（minium == false）或是最小值（minium==true）
     *
     * @param valueFromMatrix
     * @param valueFromRecord
     * @param minium
     * @return
     */
    private BigDecimal getExtreamValue(BigDecimal valueFromMatrix, BigDecimal valueFromRecord, boolean minium) {
        if (minium) {//设置小值
            if (valueFromMatrix == null) {//如果记录没有值
                if (valueFromRecord != null) {//传入的值不为空 填入
                    return valueFromRecord;
                } else {//传入的值也为空 返回原来的值
                    return valueFromMatrix;
                }
            } else {//如果记录有值
                if (valueFromRecord == null) {//如果传入记录的priceLow为null 返回原来记录里的数据
                    return valueFromMatrix;
                } else {
                    return valueFromRecord.compareTo(valueFromMatrix) == -1 ? valueFromRecord : valueFromMatrix;
                }
            }
        } else {//设置大值
            if (valueFromMatrix == null) {
                if (valueFromRecord != null) {
                    return valueFromRecord;
                } else {
                    return valueFromMatrix;
                }
            } else {
                if (valueFromRecord == null) {//如果传入记录的price为null 返回原来记录里的数据
                    return valueFromMatrix;
                } else {
                    return valueFromRecord.compareTo(valueFromMatrix) == 1 ? valueFromRecord : valueFromMatrix;
                }
            }
        }
    }



    @Override
    public List<ContactUser> getPrimeInstitutionContarct(String instituionId, ContactQuoteAttribute quoteAttribute) {
        Map<String, Set<ContactUser>> contactUsersByInstitutionId = institutionService.retrieveAllPrimeContacts();//根据机构id分类获取所有联系人信息
        Set<ContactUser> institutionContacts = contactUsersByInstitutionId.get(instituionId);//获取当前机构联系人信息
        List<ContactUser> contactUsers = new ArrayList<>();//需要返回的对象
        if (institutionContacts != null && institutionContacts.size() > 0) {
            for(ContactUser cuser : institutionContacts){
                if(cuser.getQuoteAttribute() == quoteAttribute){
                    contactUsers.add(cuser);
                }
            }
        }
        return contactUsers;
    }

    /**
     * 获取报价信息记录和详细记录
     *
     * @return
     */
    @Override
    public List<MmQuote> retrieveMmQuoteMain(MmQuoteMainQueryParameters parameter) {
        //获取所有报价信息并设置对应机构规模
        List<MmQuote> mmQuotes;
        mmQuotes = mmQuoteQueryService.queryMmQuoteMain(parameter,true,true);
        return mmQuotes;
    }

}
