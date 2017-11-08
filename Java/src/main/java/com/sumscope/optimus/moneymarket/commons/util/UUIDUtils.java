package com.sumscope.optimus.moneymarket.commons.util;

import java.util.UUID;

/**
 * Created by fan.bai on 2016/9/22.
 * UUID 相关公用服务程序
 */
public class UUIDUtils {
    /**
     * @return 用于ID的UUID字符串，取消了其中的“-”
     */
    public static String generatePrimaryKey() {
        String mqdid = UUID.randomUUID().toString();
        return mqdid.replace("-", "");
    }
}
