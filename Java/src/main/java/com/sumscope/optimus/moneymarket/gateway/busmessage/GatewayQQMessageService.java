package com.sumscope.optimus.moneymarket.gateway.busmessage;

import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.messagebus.MessageBusSubscriber;
import com.sumscope.optimus.moneymarket.MessageBusConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by fan.bai on 2016/5/7.
 * 侦听QQ接入信息并将接收的消息传递给实际消息处理的服务。监听器本身并不进行业务处理。
 */
@Service
public class GatewayQQMessageService {
    @Autowired
    @Qualifier(value = MessageBusConfiguration.QQ_MESSAGE_INTERBANK_SUBSCRIBER)
    private MessageBusSubscriber qqMessageInterbankSubscriber;

    @Autowired
    @Qualifier(value = MessageBusConfiguration.QQ_MESSAGE_OFFLINE_SUBSCRIBER)
    private MessageBusSubscriber qqMessageOfflineSubscriber;

    @Autowired
    @Qualifier(value = MessageBusConfiguration.MANUAL_QQ_MESSAGE_INTERBANK_SUBSCRIBER)
    private MessageBusSubscriber manualQQMessageInterbankSubscriber;

    @Autowired
    @Qualifier(value = MessageBusConfiguration.MANUAL_QQ_MESSAGE_OFFLINE_SUBSCRIBER)
    private MessageBusSubscriber manualQQMessageOfflineSubscriber;

    @Autowired
    private QQInterbankMessageListener interbankListener;

    @Autowired
    private QQOfflineMessageListener offlineListener;

    @Autowired
    private ManualQQInterbankMessageListener manualInterbankListener;

    @Autowired
    private ManualQQOfflineMessageListener manualOfflineListener;

    public void start(){
        //启动 service, 注册回调函数等
        try {
            qqMessageInterbankSubscriber.setSubscriberListener(interbankListener);
            qqMessageInterbankSubscriber.startToListen();

            LogManager.info("QQ理财数据监听器已启动！");

        }  catch (Exception e){
            LogManager.error("QQ理财数据监听器启动失败！"+e.getMessage());
        }

        try {
            qqMessageOfflineSubscriber.setSubscriberListener(offlineListener);
            qqMessageOfflineSubscriber.startToListen();

            LogManager.info("QQ同存数据监听器已启动！");

        }  catch (Exception e){
            LogManager.error("QQ同存数据监听器启动失败！"+e.getMessage());
        }

        try {
            manualQQMessageInterbankSubscriber.setSubscriberListener(manualInterbankListener);
            manualQQMessageInterbankSubscriber.startToListen();

            LogManager.info("手动解析QQ理财数据监听器已启动！");

        }  catch (Exception e){
            LogManager.error("手动解析QQ理财数据监听器启动失败！"+e.getMessage());
        }

        try {
            manualQQMessageOfflineSubscriber.setSubscriberListener(manualOfflineListener);
            manualQQMessageOfflineSubscriber.startToListen();

            LogManager.info("手动解析QQ同存数据监听器已启动！");

        }  catch (Exception e){
            LogManager.error("手动解析QQ同存数据监听器启动失败！"+e.getMessage());
        }
    }

    public void stop(){

    }
}
