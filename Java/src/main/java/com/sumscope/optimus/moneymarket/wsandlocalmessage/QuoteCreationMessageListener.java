package com.sumscope.optimus.moneymarket.wsandlocalmessage;

import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.messagebus.AbstractBytesBusinessMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * 实现总线提供的接口, 接收到消息通过 BusMessageProcessor 进行处理(如果直接将处理QM状态和报价信息的监听器插入, 则结合太紧密)
 *
 *
 * Created by qikai.yu on 2016/4/28.
 */
@Component
public class QuoteCreationMessageListener extends AbstractBytesBusinessMessageListener {

    @Autowired
    private NewQuoteArriveProcessor quoteProcessor;

    @Override
    protected void onBusinessMessageReceived(String topic, byte[] msg) {
        LogManager.debug("Get quote message from Bus ...");
        quoteProcessor.processNewQuote(new String(msg));
    }
}
