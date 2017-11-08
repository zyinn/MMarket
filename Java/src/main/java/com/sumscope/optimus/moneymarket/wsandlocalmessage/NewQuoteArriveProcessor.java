package com.sumscope.optimus.moneymarket.wsandlocalmessage;

/**
 * Created by qikai.yu on 2016/4/29.
 */
public interface NewQuoteArriveProcessor {
    /**
     * 当总线监听器从总线上获取了新的消息时，触发该方法进行消息处理
     * @param obj
     */
    void processNewQuote(String obj);
}
