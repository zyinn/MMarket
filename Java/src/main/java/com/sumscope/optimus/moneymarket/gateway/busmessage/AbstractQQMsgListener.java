package com.sumscope.optimus.moneymarket.gateway.busmessage;

import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.messagebus.AbstractBusinessMessageListener;
import org.apache.qpid.client.message.AMQPEncodedMapMessage;

import javax.jms.Message;
import java.util.Map;

/**
 * Created by fan.bai on 2016/5/9.
 * QQ接入信息监听器抽象类
 */
public abstract class AbstractQQMsgListener extends AbstractBusinessMessageListener {

    protected void processJMSMessage(String s, Message message) {
        if (message != null) {
            LogManager.debug("Receive QQ Message: " + message.getClass().toString() + " : " + message.toString());
            LogManager.info("Receive QQ Message: " + message.getClass().toString() + " : " + message.toString());
        } else {
            LogManager.error("Receive NULL QQ Message!");
        }
        if (message instanceof AMQPEncodedMapMessage) {
            //QQ数据信息在QPID总线上是AMQPEncodedMapMessage类型
            AMQPEncodedMapMessage mapMessage = (AMQPEncodedMapMessage) message;
            processQQJmsMessage(mapMessage.getMap());
        }
    }

    protected abstract void processQQJmsMessage(Map<String, Object> map);
}
