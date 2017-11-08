package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteMainQueryParameters;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteQueryFundSizePeriod;
import com.sumscope.optimus.moneymarket.model.dto.MobileQueryQuoteMainParameterDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by fan.bai on 2016/8/30.
 * MobileQueryQuoteMainParameterDto 转换器
 */
@Component
public class MobileQueryQuoteMainParameterDtoConverter {
    public MmQuoteMainQueryParameters convertToMainQueryParameter(MobileQueryQuoteMainParameterDto dto){
        MmQuoteMainQueryParameters result = new MmQuoteMainQueryParameters();

        result.setOnlyValidQuotes(true);
        result.setPrimeQuotesOnly(true);

        result.setAreas(dto.getAreas());
        result.setDirection(dto.getDirection());
        result.setTagCodes(dto.getTagCodes());
        result.setMemo("");

        MmQuoteQueryFundSizePeriod fundSizePeriod = new MmQuoteQueryFundSizePeriod();
        fundSizePeriod.setFundSizeHigh(dto.getInstitutionScaleHigh());
        fundSizePeriod.setFundSizeLow(dto.getInstitutionScaleLow());
        result.setFundSizes(new ArrayList<>());
        result.getFundSizes().add(fundSizePeriod);

        result.setQuoteTypes(new ArrayList<>());
        result.getQuoteTypes().add(dto.getQuoteType());

        QuoteTimePeriod sortByPeriod = dto.getSortByPeriod();
        if(sortByPeriod != null){
            result.setOrderByPeriodLow(sortByPeriod.getDaysLow());
            result.setOrderByPeriodHigh(sortByPeriod.getDaysHigh());
        }

        //移动端特殊要求，安报价单的方向确定排序顺序
        if(dto.getDirection() == Direction.OUT){
            result.setOrderSeq(Constant.DESC);
        }else{
            result.setOrderSeq(Constant.ASC);
        }
        if(dto.isPage()){
            result.setPaging(true);
            result.setPageSize(dto.getPageSize());
            result.setStartRows(dto.getPageNumber());
        }
        return result;
    }
}
