package com.sumscope.optimus.moneymarket.gateway.busmessage.converter;

import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteSource;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.commons.util.QuoteDateUtils;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteDetails;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import com.sumscope.optimus.moneymarket.service.UserBaseService;

import java.util.Date;
import java.util.Map;

/**
 * Created by fan.bai on 2016/8/3.
 * 用于接收QQ数据的工具类
 */
public class QQConverterUtil {
    static void setupDaysPeroidInModel(MmQuoteDetails detail, Integer daysHigh, Integer daysLow) {
        daysHigh = normalizeYearDays(daysHigh);
        daysLow = normalizeYearDays(daysLow);
        if (daysHigh == null && daysLow == null) {
            //当QQ报价不包含明细数据或者不指定日期期限时，默认为1天有效
            detail.setDaysHigh(1);
            detail.setDaysLow(1);
        } else if (daysHigh == null) {
            detail.setDaysHigh(daysLow);
            detail.setDaysLow(daysLow);
        } else if (daysLow == null) {
            detail.setDaysHigh(daysHigh);
            detail.setDaysLow(daysHigh);
        } else {
            // QQ报价不正规，有可能上下区间颠倒
            if (daysHigh < daysLow) {
                Integer tmp = daysHigh;
                daysHigh = daysLow;
                daysLow = tmp;
            }
            detail.setDaysHigh(daysHigh);
            detail.setDaysLow(daysLow);
        }
    }
    /**
     * QQ解析 使用365作为一年而QB使用360作为一年。此处进行天数的转换以支持一年期的QQ报价在页面上显示的是整年。
     */
    private static Integer normalizeYearDays(Integer days) {
        if(days != null && days % 365 == 0){
            return (days / 365) * 360;
        }
        return days;
    }

    static void convertInfoToMmQuote(UserBaseService userBaseService, QuoteType quoteType, MmQuote mmQuote, Direction direction, String qqMsgSource, String qqId) {
        mmQuote.setSource(QuoteSource.QQ);
        mmQuote.setDirection(Direction.valueOf(direction.name()));
        mmQuote.setMemo(qqMsgSource);
        mmQuote.setQuoteUserId(qqId);
        mmQuote.setQuoteType(QuoteType.valueOf(quoteType.name()));
        mmQuote.setInstitutionId("-1");
        mmQuote.setSequence(1); //QQ报价该字段默认为1
        mmQuote.setMethodType(MethodType.SEF);
        //设置过期时间为当天 23:00整
        mmQuote.setExpiredDate(QuoteDateUtils.getExpiredTimeOfDate(new Date()));
        Map<String, User> userList = userBaseService.retreiveAllUsersGroupByUserID();  //拿到所有用户的分组
        User user = userList.get(qqId);
        if (user != null) {
            mmQuote.setBankNature(user.getBankNature());
            mmQuote.setProvince(user.getProvince());  //省份信息
            mmQuote.setCustodianQualification(false);  //托管资格
        }
    }

}
