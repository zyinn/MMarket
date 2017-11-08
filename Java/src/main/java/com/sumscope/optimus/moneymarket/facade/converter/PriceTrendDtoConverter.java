package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.moneymarket.commons.enums.CalculatedBankNature;
import com.sumscope.optimus.moneymarket.commons.util.Utils;
import com.sumscope.optimus.moneymarket.model.dbmodel.PriceTrend;
import com.sumscope.optimus.moneymarket.model.dto.PriceTrendDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan.bai on 2016/9/22.
 * PriceTrendDto转换器
 */
@Component
public class PriceTrendDtoConverter {
    /**
     * 转换model为Dto
     */
    public List<PriceTrendDto> convertToDto(List<PriceTrend> models){
        List<PriceTrendDto> results = new ArrayList<>();
        for(PriceTrend priceTrend : models){
            results.add(convertSingleDto(priceTrend));
        }
        return results;

    }

    private PriceTrendDto convertSingleDto(PriceTrend priceTrend) {
        PriceTrendDto dto = new PriceTrendDto();
        BeanUtils.copyProperties(priceTrend,dto);
        if(priceTrend.getMatrixBankNature()!=CalculatedBankNature.SHIBOR && dto.getPriceHigh()!=null){
            dto.setPriceHigh(Utils.getBigDecimalToTwoDecimal(dto.getPriceHigh()));
        }
        if(priceTrend.getMatrixBankNature()!=CalculatedBankNature.SHIBOR && dto.getPriceLow()!=null){
            dto.setPriceLow(Utils.getBigDecimalToTwoDecimal(dto.getPriceLow()));
        }
        return dto;
    }
}
