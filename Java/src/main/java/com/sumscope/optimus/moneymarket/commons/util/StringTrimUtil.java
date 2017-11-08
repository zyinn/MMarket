package com.sumscope.optimus.moneymarket.commons.util;

/**
 * Created by xuejian.sun on 2016/7/18.
 * 去除字符串中的空格
 */

public final class StringTrimUtil {
    /**
     * 获取trim之后的字符串
     * @param param 输入字符串
     * @return trim之后的字符串
     */
    public static String getTrimedString(String param){
        if(param!=null){
            return param.trim();
        }
        return null;
    }
}
