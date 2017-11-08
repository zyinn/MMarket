package com.sumscope.optimus.moneymarket.wsandlocalmessage;

import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;

/**
 * Created by fan.bai on 2016/7/20.
 * 向Web段通过WebSocket发送信息的接口
 */
public interface WebSocketInvoke {
    /**
     * 向前端发送报价单
     * @param mmQuote
     */
    void sendQuoteInListResponseDtoFomart(MmQuote mmQuote);

}
