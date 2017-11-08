package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.commons.util.JsonUtil;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.enums.*;
import com.sumscope.optimus.moneymarket.commons.util.QuoteDateUtils;
import com.sumscope.optimus.moneymarket.commons.util.Utils;
import com.sumscope.optimus.moneymarket.gatewayinvoke.ShiborManagerInvoke;
import com.sumscope.optimus.moneymarket.model.dbmodel.PriceTrend;
import com.sumscope.optimus.moneymarket.model.dto.PriceMatrixCellDto;
import com.sumscope.optimus.moneymarket.model.dto.PriceMatrixDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by fan.bai on 2016/9/19.
 * 将矩阵计算结果转换为界面显示相关数据格式的转换器
 *
 */
@Component
public class PriceMatrixDtoConverter {
    @Autowired
    private ShiborManagerInvoke shiborManagerInvoke;

    @CacheMe(timeout = 60*60, synchornizeUpdate = false)
    public List<PriceTrend> convertShiborResultTableToPriceTrends(List<PriceTrend> priceTrends, Direction direction, QuoteType quoteType, Date date) {
        List<ShiborManagerInvoke.ResultTable> shibor = sendCDHReceiveShiborMapper();
        if (shibor!=null && shibor.size()>0){
            getResultMapForNull(shibor);
            List<PriceTrend> PriceTrend = new ArrayList<>();
            for (PriceTrend priceTrend : priceTrends) {
                if (priceTrend.getMatrixBankNature() == CalculatedBankNature.SHIBOR) {
                    return priceTrends;
                }
            }
            for (ShiborManagerInvoke.ResultTable resultTable : shibor) {
                checkQuoteTimePeriodAndSetPriceTrend(direction, quoteType, date, PriceTrend, resultTable);
            }
            priceTrends.addAll(PriceTrend);
        }else{
                List<PriceTrend> priceTrend = getPriceTrendsForShiborIsNull(direction, quoteType);
                priceTrends.addAll(priceTrend);
        }
        return priceTrends;
    }

    public List<ShiborManagerInvoke.ResultTable> sendCDHReceiveShiborMapper() {
        List<ShiborManagerInvoke.ResultTable> shibor=new ArrayList<>();
        Boolean sign=true;
        int startPage=0;
        while (sign){
            startPage= startPage+1;
            List<ShiborManagerInvoke.ResultTable> shiborInvoke = shiborManagerInvoke.getShiborInvoke(shiborManagerInvoke.ShiborJson(startPage),Constant.matrix);
            if(shiborInvoke!=null && shiborInvoke.size()>0){
                shibor.addAll(shiborInvoke);
            }
            sign= (shiborInvoke==null || shiborInvoke.size()<5000 ) ? false : true;
        }
        return shibor;
    }
    private List<PriceTrend> getPriceTrendsForShiborIsNull(Direction direction, QuoteType quoteType) {
        List<PriceTrend> priceTrend =new ArrayList<>();
        if(quoteType!=QuoteType.IBD){
            for(int i=0;i<7 ;i++){
                PriceTrend price=new PriceTrend();
                switch (i){
                    case 0:price.setTimePeriod(QuoteTimePeriod.T1D);break;
                    case 1:price.setTimePeriod(QuoteTimePeriod.T1M);break;
                    case 2:price.setTimePeriod(QuoteTimePeriod.T2M);break;
                    case 3:price.setTimePeriod(QuoteTimePeriod.T3M);break;
                    case 4:price.setTimePeriod(QuoteTimePeriod.T6M);break;
                    case 5:price.setTimePeriod(QuoteTimePeriod.T9M);break;
                    case 6:price.setTimePeriod(QuoteTimePeriod.T1Y);break;
                }
                getPriceTrend(direction,quoteType, new Date(), null,priceTrend,price.getTimePeriod());
                priceTrend.add(price);
            }
        }else{
            for(int i=0;i<9 ;i++){
                PriceTrend price=new PriceTrend();
                switch (i){
                    case 0:price.setTimePeriod(QuoteTimePeriod.T1D);break;
                    case 1:price.setTimePeriod(QuoteTimePeriod.T7D);break;
                    case 2:price.setTimePeriod(QuoteTimePeriod.T14D);break;
                    case 3:price.setTimePeriod(QuoteTimePeriod.T1M);break;
                    case 4:price.setTimePeriod(QuoteTimePeriod.T2M);break;
                    case 5:price.setTimePeriod(QuoteTimePeriod.T3M);break;
                    case 6:price.setTimePeriod(QuoteTimePeriod.T6M);break;
                    case 7:price.setTimePeriod(QuoteTimePeriod.T9M);break;
                    case 8:price.setTimePeriod(QuoteTimePeriod.T1Y);break;
                }
                getPriceTrend(direction,quoteType, new Date(), null,priceTrend,price.getTimePeriod());
                priceTrend.add(price);
            }
        }
        return priceTrend;
    }

    private void getResultMapForNull(List<ShiborManagerInvoke.ResultTable> resultMap) {
        if(resultMap!=null && resultMap.size()>0 &&resultMap.size()<9){
            String json= JsonUtil.writeValueAsString(resultMap);
            if(!json.contains("SHIBOR_O/N")){
                shiborManagerInvoke.getResultTableForNull(resultMap,"SHIBOR_O/N");
            }
            if(!json.contains("SHIBOR_1W")){
                shiborManagerInvoke.getResultTableForNull(resultMap,"SHIBOR_1W");
            }
            if(!json.contains("SHIBOR_2W")){
                shiborManagerInvoke.getResultTableForNull(resultMap,"SHIBOR_2W");
            }
            if(!json.contains("SHIBOR_1M")){
                shiborManagerInvoke.getResultTableForNull(resultMap,"SHIBOR_1M");
            }
            if(!json.contains("SHIBOR_2M")){
                shiborManagerInvoke.getResultTableForNull(resultMap,"SHIBOR_2M");
            }
            if(!json.contains("SHIBOR_3M")){
                shiborManagerInvoke.getResultTableForNull(resultMap,"SHIBOR_3M");
            }
            if(!json.contains("SHIBOR_6M")){
                shiborManagerInvoke.getResultTableForNull(resultMap,"SHIBOR_6M");
            }
            if(!json.contains("SHIBOR_9M")){
                shiborManagerInvoke.getResultTableForNull(resultMap,"SHIBOR_9M");
            }
            if(!json.contains("SHIBOR_1Y")){
                shiborManagerInvoke.getResultTableForNull(resultMap,"SHIBOR_1Y");
            }
        }
    }
    private void checkQuoteTimePeriodAndSetPriceTrend(Direction direction, QuoteType quoteType, Date date, List<PriceTrend> priceTrend, ShiborManagerInvoke.ResultTable resultTable) {
        if(resultTable.getSourcecode().equals(ShiborManagerInvoke.SourceCode.T1D.getDisplayName())){
            getPriceTrend(direction, quoteType, date, resultTable, priceTrend, QuoteTimePeriod.T1D);
        }
        if(resultTable.getSourcecode().equals(ShiborManagerInvoke.SourceCode.T7D.getDisplayName()) && quoteType==QuoteType.IBD){
            getPriceTrend(direction, quoteType, date, resultTable, priceTrend,QuoteTimePeriod.T7D);
        }
        if(resultTable.getSourcecode().equals(ShiborManagerInvoke.SourceCode.T14D.getDisplayName()) && quoteType==QuoteType.IBD){
            getPriceTrend(direction, quoteType, date, resultTable, priceTrend,QuoteTimePeriod.T14D);
        }
        if(resultTable.getSourcecode().equals(ShiborManagerInvoke.SourceCode.T1M.getDisplayName()) ){
            getPriceTrend(direction, quoteType, date, resultTable, priceTrend,QuoteTimePeriod.T1M);
        }
        if(resultTable.getSourcecode().equals(ShiborManagerInvoke.SourceCode.T2M.getDisplayName())){
            getPriceTrend(direction, quoteType, date, resultTable, priceTrend,QuoteTimePeriod.T2M);
        }
        if(resultTable.getSourcecode().equals(ShiborManagerInvoke.SourceCode.T3M.getDisplayName())){
            getPriceTrend(direction, quoteType, date, resultTable, priceTrend,QuoteTimePeriod.T3M);
        }
        if(resultTable.getSourcecode().equals(ShiborManagerInvoke.SourceCode.T6M.getDisplayName())){
            getPriceTrend(direction, quoteType, date, resultTable, priceTrend,QuoteTimePeriod.T6M);
        }
        if(resultTable.getSourcecode().equals(ShiborManagerInvoke.SourceCode.T9M.getDisplayName())){
            getPriceTrend(direction, quoteType, date, resultTable, priceTrend,QuoteTimePeriod.T9M);
        }
        if(resultTable.getSourcecode().equals(ShiborManagerInvoke.SourceCode.T1Y.getDisplayName())){
            getPriceTrend(direction, quoteType, date, resultTable, priceTrend,QuoteTimePeriod.T1Y);
        }
    }

    private void getPriceTrend(Direction direction, QuoteType quoteType, Date date, ShiborManagerInvoke.ResultTable resultTable,List<PriceTrend> PriceTrend,QuoteTimePeriod QuoteTimePeriod) {
        PriceTrend priceTrend=new PriceTrend();
        priceTrend.setQuoteType(quoteType);
        priceTrend.setDirection(direction);
        priceTrend.setMatrixBankNature(CalculatedBankNature.SHIBOR);
        priceTrend.setCreateTime(date);
        priceTrend.setMatrixFundSize(MatrixFundSize.NONE);
        priceTrend.setPriceHigh(resultTable!=null ? resultTable.getIndexValue() : null);
        priceTrend.setPriceLow(resultTable!=null ? resultTable.getIndexValue() : null);
        priceTrend.setTimePeriod(QuoteTimePeriod);
        PriceTrend.add(priceTrend);
    }

    /**
     * model to dto
     * @param cells List of PriceTrend
     * @return List of PriceMatrixDto
     */
    public List<PriceMatrixDto> convertModelsToDtos(List<PriceTrend> cells){
       if(cells!=null && cells.size()>0){    sortTimePeriod(cells);}
        List<PriceMatrixDto> results = new ArrayList<>();
        for(PriceTrend cell: cells){
            CalculatedBankNature matrixBankNature = cell.getMatrixBankNature();
            MatrixFundSize matrixFundSize = cell.getMatrixFundSize();
            PriceMatrixDto existedDto = findExistedDto(matrixBankNature,matrixFundSize,results);
            if(existedDto == null){
                existedDto = new PriceMatrixDto();
                existedDto.setMatrixFundSize(matrixFundSize);
                existedDto.setMatrixBankNature(matrixBankNature);
                existedDto.setRowDtoList(new ArrayList<>());
                results.add(existedDto);
            }
            existedDto.getRowDtoList().add(convertToCellDto(cell));
        }
        return results;
    }

    private void sortTimePeriod(List<PriceTrend> cells) {
        Collections.sort(cells, new Comparator<PriceTrend>() {
            public int compare(PriceTrend arg0, PriceTrend arg1) {
                return  arg0.getTimePeriod().getDaysHigh() == arg1.getTimePeriod().getDaysHigh() ? 0 : (arg0.getTimePeriod().getDaysHigh() > arg1.getTimePeriod().getDaysHigh() ? 1 : -1);
            }
        });
    }

    private PriceMatrixDto findExistedDto(CalculatedBankNature matrixBankNature, MatrixFundSize matrixFundSize, List<PriceMatrixDto> results) {
        for( PriceMatrixDto dto: results){
            if(matrixBankNature == dto.getMatrixBankNature() && matrixFundSize == dto.getMatrixFundSize()){
                return dto;
            }
        }
        return null;
    }

    private PriceMatrixCellDto convertToCellDto(PriceTrend cell) {
        PriceMatrixCellDto dto = new PriceMatrixCellDto();
        BeanUtils.copyProperties(cell,dto);
        if(cell.getMatrixBankNature()!=CalculatedBankNature.SHIBOR && dto.getPriceHigh()!=null){
            dto.setPriceHigh(Utils.getBigDecimalToTwoDecimal(dto.getPriceHigh()));
        }
        if(cell.getMatrixBankNature()!=CalculatedBankNature.SHIBOR && dto.getPriceLow()!=null){
            dto.setPriceLow(Utils.getBigDecimalToTwoDecimal(dto.getPriceLow()));
        }
        return dto;
    }
}
