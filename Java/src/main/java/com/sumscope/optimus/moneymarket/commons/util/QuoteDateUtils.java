package com.sumscope.optimus.moneymarket.commons.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by fan.bai on 2016/9/19.
 * 报价日期工具类，用于生成特定的日期及时间
 */
public class QuoteDateUtils {
    /**
     * 设置某一日期的时间为23：59：59
     */
    public static Date getLatestTimeOfDate(Date date) {
        return getDateWithSpecifiedTime(date, 23, 59, 59);
    }

    private static Date getDateWithSpecifiedTime(Date date, int hour, int min, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    /**
     * 获取某一日期时间为一日开始时间，既00：00：01
     */
    public static Date getBeginingTimeOfDate(Date date) {
        return getDateWithSpecifiedTime(date, 0, 0, 1);
    }

    /**
     * 设置日期为用于矩阵计算的时间，既18:00:00，该时间是工作日结束进行结转的时间
     */
    public static Date getMatrixCalculationTimeOfDate(Date date) {
        return getDateWithSpecifiedTime(date, 18, 0, 0);
    }

    /**
     * 设置日期为过期时间，既23:00:00
     */
    public static Date getExpiredTimeOfDate(Date date) {
        return getDateWithSpecifiedTime(date, 23, 0, 0);
    }

    /**
     * 获取昨日过期日期，时间为23：00：00
     */
    public static Date getYesterdayExpiredTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return getExpiredTimeOfDate(calendar.getTime());
    }

}
