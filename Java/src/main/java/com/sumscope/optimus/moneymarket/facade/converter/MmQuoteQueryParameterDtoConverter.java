package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.moneymarket.commons.enums.*;
import com.sumscope.optimus.moneymarket.commons.util.CollectionsUtil;
import com.sumscope.optimus.moneymarket.commons.util.Utils;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteMainQueryParameters;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteQueryFundSizePeriod;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteQueryParameterDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan.bai on 2016/8/5.
 * AbstractMmQuoteQueryParameters 转换器,负责将有关MmQuote主表的查询Dto转换为MmQuoteQueryParameters模型
 */
@Component
public class MmQuoteQueryParameterDtoConverter {

    /**
     * @param dto MmQuoteQueryParameterDto
     * @return MmQuoteMainQueryParameters
     */
    public MmQuoteMainQueryParameters convertToMainQueryModel(MmQuoteQueryParameterDto dto) {
        checkMmQuoteQueryParameterDtoValid(dto);
        MmQuoteMainQueryParameters result = new MmQuoteMainQueryParameters();
        BeanUtils.copyProperties(dto, result);
        if (dto.getOrderByPeriod() != null) {
            QuoteTimePeriod orderByPeriod = dto.getOrderByPeriod();
            result.setOrderByPeriodHigh(orderByPeriod.getDaysHigh());
            result.setOrderByPeriodLow(orderByPeriod.getDaysLow());
        }

        if (dto.getFundSizes() != null && dto.getFundSizes().size() > 0) {
            List<MmQuoteQueryFundSizePeriod> fundSizePeriods = new ArrayList<>();
            for (QueryFundSize fundSize : dto.getFundSizes()) {
                if (fundSize == null) {
                    continue;
                }
                MmQuoteQueryFundSizePeriod period = new MmQuoteQueryFundSizePeriod();
                period.setFundSizeHigh(fundSize.getFundSizeHighInBigDecimal());
                period.setFundSizeLow(fundSize.getFundSizeLowInBigDecimal());
                fundSizePeriods.add(period);
            }
            result.setFundSizes(fundSizePeriods);
        }
        if (!CollectionsUtil.isEmptyOrNullCollection(dto.getBankNatures())) {
            result.setBankNatures(new ArrayList<>());
            //如果查询“其他”机构类型，设置model内的查询其他机构类型布尔值字段。
            //Dao层进行查询时会根据该数值决定查询的方式
            if (dto.getBankNatures().contains(CalculatedBankNature.OTHERS)) {
                result.setSearchOtherBankNatures(true);
            }else{
                result.setSearchOtherBankNatures(false);
            }
            for (CalculatedBankNature calculatedBankNature : dto.getBankNatures()) {
                if (calculatedBankNature != CalculatedBankNature.OTHERS) {
                    result.getBankNatures().add(calculatedBankNature.getBankNatureCode());
                }
            }
        }
        convertPaging(dto, result);
        return result;
    }

    //该方法判断的依据是查询时报价类型应该是互斥的，既要么查询理财，要么查询同存,并且排序时不可为空
    private boolean searchForFIQuote(List<QuoteType> quoteTypes) {
        QuoteType quoteType = quoteTypes.get(0);
        return quoteType.getContactQuoteAttribute() == ContactQuoteAttribute.FI;
    }

    private void convertPaging(MmQuoteQueryParameterDto dto, MmQuoteMainQueryParameters result) {
        if (dto.getPageSize() > 0) {
            result.setPaging(true);
            result.setPageSize(dto.getPageSize());
            result.setStartRows((dto.getPageNumber() - 1) * dto.getPageSize());
        }
    }


    private void checkMmQuoteQueryParameterDtoValid(MmQuoteQueryParameterDto parameterDto) {
        Direction dir = parameterDto.getDirection();
        if (!Direction.IN.equals(dir) && !Direction.OUT.equals(dir)) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "direction不合法");
        }

        for (QuoteType quoteType : parameterDto.getQuoteTypes()) {
            if (!QuoteType.GTF.equals(quoteType) && !QuoteType.UR2.equals(quoteType) &&
                    !QuoteType.UR3.equals(quoteType) && !QuoteType.IBD.equals(quoteType)) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "quoteType不合法");
            }
        }

        for (CalculatedBankNature bankNature : parameterDto.getBankNatures()) {
            if (!CalculatedBankNature.SHIBOR.equals(bankNature) && !CalculatedBankNature.BIG_BANK.equals(bankNature) &&
                    !CalculatedBankNature.JOINT_STOCK.equals(bankNature) && !CalculatedBankNature.CITY_COMMERCIAL_BANK.equals(bankNature) &&
                    !CalculatedBankNature.RURAL_CREDIT.equals(bankNature) && !CalculatedBankNature.OTHERS.equals(bankNature)) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "bankNatures不合法");
            }
        }
        for (QueryFundSize fundSize : parameterDto.getFundSizes()) {
            if (!QueryFundSize.SMALLER_ONE_H.equals(fundSize) && !QueryFundSize.SMALLER_TWO_H.equals(fundSize) &&
                    !QueryFundSize.TWO_FIVE_H.equals(fundSize) && !QueryFundSize.FIVE_H_ONE_T.equals(fundSize) &&
                    !QueryFundSize.ONE_TWO_T.equals(fundSize) && !QueryFundSize.TWO_FIVE_T.equals(fundSize) &&
                    !QueryFundSize.LARGER_FIVE_T.equals(fundSize) ) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "fundSizes不合法");
            }
        }

        for (String str : parameterDto.getTagCodes()) {
            if (str != null && str.length() > 3) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "tagCodes不合法");
            }
        }
        List<String> validTags = new ArrayList<>();
        for (String str : parameterDto.getTagCodes()) {
            validTags.add(Utils.validateStr(str));
        }
        parameterDto.setTagCodes(validTags);

        QuoteTimePeriod period = parameterDto.getOrderByPeriod();
        if (period != null) {
            if (!QuoteTimePeriod.T1D.equals(period) && !QuoteTimePeriod.T7D.equals(period) &&
                    !QuoteTimePeriod.T14D.equals(period) && !QuoteTimePeriod.T1M.equals(period) &&
                    !QuoteTimePeriod.T2M.equals(period) && !QuoteTimePeriod.T3M.equals(period) &&
                    !QuoteTimePeriod.T6M.equals(period) && !QuoteTimePeriod.T9M.equals(period) &&
                    !QuoteTimePeriod.T1Y.equals(period)) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "period不合法");
            }
        }

        parameterDto.setOrderSeq(Utils.validate(parameterDto.getOrderSeq()));

        List<String> insIdList = new ArrayList<>();
        for (String insId : parameterDto.getInstitutionIdList()) {
            if (insId != null && insId.length() > 32) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "institutionIdList不合法");
            }
            insIdList.add(Utils.validate(insId));
        }
        parameterDto.setInstitutionIdList(insIdList);

        String quoteUserId = parameterDto.getQuoteUserId();
        if (quoteUserId != null && quoteUserId.length() > 32) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "quoteUserId不合法");
        }
        parameterDto.setQuoteUserId(Utils.validate(quoteUserId));

        String memo = parameterDto.getMemo();
        if (memo != null && memo.length() > 512) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "memo不合法");
        }
        parameterDto.setMemo(Utils.validate(memo));
    }
}
