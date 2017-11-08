package com.sumscope.optimus.moneymarket.commons.util;

import java.util.Collection;

/**
 * Created by fan.bai on 2016/8/31.
 * 用于集合判断的工具类
 */
public class CollectionsUtil {
    /**
     * 判断一个List是否为空
     */
    public static boolean isEmptyOrNullCollection(Collection list) {
        return list == null || list.size() == 0;
    }

}
