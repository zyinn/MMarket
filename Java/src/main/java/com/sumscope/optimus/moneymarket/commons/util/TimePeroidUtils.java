package com.sumscope.optimus.moneymarket.commons.util;

import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteDetailsDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan.bai on 2016/9/1.
 * 报价明细表数据相关帮助服务
 */
public class TimePeroidUtils {
    /**
     * 根据传入的报价明细的期限上下值获取所有受影响的QuoteTimePeriod记录，标准期限返回对应的标准期限唯一值
     * @param detailDayLow 日期下限
     * @param detailDayHigh 日期上限
     * @return 有影响的报价区间枚举列表
     */
    public static List<QuoteTimePeriod> getAffectTimePeroid(int detailDayLow, int detailDayHigh) {
        List<QuoteTimePeriod> result = new ArrayList<>();
        //先判断是否是标准期限，是标准期限直接返回标准期限，否则进行期限值判断
        QuoteTimePeriod starndardPeroid = TimePeroidUtils.getPeriodFromValue(detailDayLow,detailDayHigh);
        if(starndardPeroid == null){
            for (QuoteTimePeriod quoteTimePeriod : QuoteTimePeriod.values()) {
                int daysLow = quoteTimePeriod.getDaysLow();//对应期限的低值
                int daysHigh = quoteTimePeriod.getDaysHigh();//对应期限的值
                //区间里的期限低值或高值只要有一个在报价期间内则满足条件 需要参与计算
                if ((detailDayLow <= daysLow && daysLow <= detailDayHigh) ||
                        (detailDayLow <= daysHigh && daysHigh <= detailDayHigh)||
                        (detailDayLow >= daysLow && daysHigh >= detailDayHigh)) {
                    result.add(quoteTimePeriod);
                }
            }
            if(result==null || result.size()==0){
                result.add(QuoteTimePeriod.T1Y);
            }
        }else{
            result.add(starndardPeroid);
        }

        return result;
    }

    /**
     * 转换int类型天数至显示字符串。增加D，M（可以整除30），Y（可以整除360）
     */
    public static String convertNumberOfDaysToString(int days){
        final int constantMonth=30;
        final int constantYear=360;
        if(days <= 0){
            return "";
        }
        if(days % constantYear == 0 ){
            return days / constantYear + "Y";
        }
        if(days % constantMonth == 0 ){
            return days / constantMonth + "M";
        }
        else{
            return days + "D";
        }

    }

    /**
     * 根据日期高低判断对应的期限枚举值
     * @return 对应的枚举值，非标准期限返回null
     */
    public static QuoteTimePeriod getPeriodFromValue(int low, int high) {
        for (QuoteTimePeriod item : QuoteTimePeriod.values()) {
            if (item.getDaysLow() == low && item.getDaysHigh() == high) {
                return item;
            }
        }
        return null;
    }
}
