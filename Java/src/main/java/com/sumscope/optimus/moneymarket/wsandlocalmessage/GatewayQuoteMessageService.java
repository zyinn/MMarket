package com.sumscope.optimus.moneymarket.wsandlocalmessage;

import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.messagebus.MessageBusException;
import com.sumscope.optimus.commons.messagebus.MessageBusInitialException;
import com.sumscope.optimus.commons.messagebus.MessageBusSubscriber;
import com.sumscope.optimus.moneymarket.MessageBusConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 提供和总线交互的接口, 初始化, 并且设置监听器, 一旦从总线获取到报价信息, 调用监听器进行处理
 *
 * Created by qikai.yu on 2016/4/28.
 */
@Component
public class GatewayQuoteMessageService {
    @Autowired
    @Qualifier(value = MessageBusConfiguration.APPLICATION_MESSAGE_BUS_QUOTE_SUBSCRIBER)
    private MessageBusSubscriber applicationMsgSubscriber;

    @Autowired
    private QuoteCreationMessageListener listener;

    public void start(){
        //启动 service, 注册回调函数等
        try {
            applicationMsgSubscriber.setSubscriberListener(listener);
            applicationMsgSubscriber.startToListen();
        } catch (MessageBusException e) {
            LogManager.error(e.toString());
        } catch (MessageBusInitialException e) {
            LogManager.error(new BusinessRuntimeException(BusinessRuntimeExceptionType.MESSAGE_BUS_ERROR,e));
            //todo: exception handle
        }
    }
    public void stop(){

    }
}
