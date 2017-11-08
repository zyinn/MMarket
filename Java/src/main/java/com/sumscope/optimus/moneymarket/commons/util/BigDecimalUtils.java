package com.sumscope.optimus.moneymarket.commons.util;

import java.math.BigDecimal;

/**
 * Created by fan.bai on 2016/8/10.
 * BigDecimal 工具类
 */
public class BigDecimalUtils {
    /**
     * 比较两个BigDecimal是否值相等
     */
    public static boolean equalsDoubleValue(BigDecimal b1, BigDecimal b2){
        if(b1 == null && b2 == null){
            return true;
        }
        if(b1 == null || b2 == null){
            return false;
        }
        return b1.doubleValue() == b2.doubleValue();
    }

    /**
     * 根据输入BigDecimal的浮点值判断大小。null值默认为0
     * @return -1： source < target, 0: source = target, 1: source > target
     */
    public static int compareDoubleValue(BigDecimal source, BigDecimal target){
        double sourceD = source == null? 0: source.doubleValue();
        double targetD = target == null? 0: target.doubleValue();

        if(sourceD == targetD){
            return 0;
        }else if(sourceD - targetD < 0){
            return -1;
        }else{
            return 1;
        }
    }
}
