package com.sumscope.optimus.moneymarket.facade.converter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.enums.CalculatedBankNature;
import com.sumscope.optimus.moneymarket.commons.enums.MatrixFundSize;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;
import com.sumscope.optimus.moneymarket.commons.util.QuoteDateUtils;
import com.sumscope.optimus.moneymarket.gatewayinvoke.ShiborManagerInvoke;
import com.sumscope.optimus.moneymarket.model.dbmodel.PriceTrend;
import com.sumscope.optimus.moneymarket.model.dbmodel.PriceTrendsQueryParameters;
import com.sumscope.optimus.moneymarket.model.dto.PriceTrendQueryParameterDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by fan.bai on 2016/9/22.
 */
@Component
public class PriceTrendQueryParameterDtoConverter {

    @Autowired
    private ShiborManagerInvoke shiborManagerInvoke;

    public PriceTrendsQueryParameters convertToModel(PriceTrendQueryParameterDto dto){
        PriceTrendsQueryParameters parameters = new PriceTrendsQueryParameters();
        BeanUtils.copyProperties(dto, parameters);
        if(dto.getCreateTime()!=null){
            dto.setCreateTime(QuoteDateUtils.getMatrixCalculationTimeOfDate(dto.getCreateTime()));
        }
        if(dto.getNumberOfPreviousDays() == null){
            parameters.setNumberOfPreviousDays(60);
        }
        return parameters;
    }
    @CacheMe(timeout = 60*60, synchornizeUpdate = false)
    public List<PriceTrend> convertToPriceTrendsForList(PriceTrendQueryParameterDto parameters,List<PriceTrend> priceTrends){
        if(parameters.getMatrixBankNature()== CalculatedBankNature.SHIBOR){
            List<ShiborManagerInvoke.ResultTable> shiborInvoke =sendCDHReceiveShiborMapper(parameters);
            if(shiborInvoke!=null && shiborInvoke.size()>0){
                for(ShiborManagerInvoke.ResultTable resultTable:shiborInvoke){
                    PriceTrend priceTrend=new PriceTrend();
                    priceTrend.setPriceLow(resultTable.getIndexValue());
                    priceTrend.setPriceHigh(resultTable.getIndexValue());
                    priceTrend.setMatrixFundSize(MatrixFundSize.NONE);
                    if(resultTable.getIndexDate()!=null && !"".equals(resultTable.getIndexDate())){
                        try {
                            priceTrend.setCreateTime( new SimpleDateFormat("yyyy-MM-dd").parse(resultTable.getIndexDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    priceTrend.setDirection(parameters.getDirection());
                    priceTrend.setQuoteType(parameters.getQuoteType());
                    priceTrend.setTimePeriod(parameters.getTimePeriod());
                    priceTrend.setMatrixBankNature(CalculatedBankNature.SHIBOR);
                    priceTrend.setId(null);
                    priceTrends.add(priceTrend);
                }
                if(priceTrends!=null && priceTrends.size()>0){
                    Collections.sort(priceTrends, new Comparator<PriceTrend>() {
                        public int compare(PriceTrend arg0, PriceTrend arg1) {
                            return arg0!=null && arg1!=null ?arg0.getCreateTime().compareTo(arg1.getCreateTime()) : 0;
                        }
                    });
                }
            }
        }
        return priceTrends;
    }


    public List<ShiborManagerInvoke.ResultTable> sendCDHReceiveShiborMapper(PriceTrendQueryParameterDto parameters) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        List<ShiborManagerInvoke.ResultTable> shibor=new ArrayList<>();
        JSONArray ShiborInvokeCodes = shiborManagerInvoke.getHistoryParamCodes(parameters.getTimePeriod());
        Boolean sign=true;
        int startPage=0;
        while (sign){
            startPage= startPage+1;
            String endDate = sdf.format(new Date());
            String startDate= dateTime();
            String history_json = shiborManagerInvoke.ShiborJsonToHistory(startPage, startDate, endDate, ShiborInvokeCodes);
            List<ShiborManagerInvoke.ResultTable> shiborInvoke = shiborManagerInvoke.getShiborInvoke(history_json,Constant.matrix);
            if(shiborInvoke!=null && shiborInvoke.size()>0){
                shibor.addAll(shiborInvoke);
            }
            sign= (shiborInvoke==null || shiborInvoke.size()<5000 ) ? false : true;
        }
        return shibor;
    }



    private String dateTime(){
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(new Date());//把当前时间赋给日历
        calendar.add(calendar.MONTH, -3);  //设置为前3月
        dBefore = calendar.getTime();   //得到前3月的时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd"); //设置时间格式
        String defaultStartDate = sdf.format(dBefore);    //格式化前3月的时间
        return defaultStartDate;
    }

}
