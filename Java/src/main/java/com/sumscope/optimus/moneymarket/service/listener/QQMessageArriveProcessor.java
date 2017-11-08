package com.sumscope.optimus.moneymarket.service.listener;

import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by fan.bai on 2016/5/7.
 * 转换总线监听器监听到的QQ报价信息至本地报价单结构，并写入本地数据库。
 */
public interface QQMessageArriveProcessor {
    /**
     * 处理QQ信息转换得到的报价单，进行事务控制
     * @param mmQuote QQ信息转换之后生成的报价单数据
     */
    void processQQQuoteInTransaction(MmQuote mmQuote);
}
