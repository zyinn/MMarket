package com.sumscope.optimus.moneymarket;

import com.sumscope.optimus.commons.messagebus.AbstractMessageBusSubPub;
import com.sumscope.optimus.commons.messagebus.MessageBusPublisher;
import com.sumscope.optimus.commons.messagebus.MessageBusSubscriber;
import com.sumscope.optimus.commons.messagebus.MessageBusType;
import com.sumscope.optimus.commons.messagebus.activemq.ActiveMQPublisher;
import com.sumscope.optimus.commons.messagebus.activemq.ActiveMQSubscriber;
import com.sumscope.optimus.commons.messagebus.qpid.QpidPublisher;
import com.sumscope.optimus.commons.messagebus.qpid.QpidSubscriber;
import com.sumscope.optimus.moneymarket.gateway.GatewayManager;
import com.sumscope.optimus.moneymarket.wsandlocalmessage.LocalMessageBusManager;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by fan.bai on 2016/4/28.
 * 总线监听及信息发布相关配置
 */
@Configuration
public class MessageBusConfiguration implements EnvironmentAware {
    public static final String APPLICATION_MESSAGE_BUS_QUOTE_SUBSCRIBER = "app_msg_quote_subscriber";
    public static final String APPLICATION_MESSAGE_BUS_QUOTE_PUBLISHER = "app_msg_quote_publisher";
    public static final String QQ_MESSAGE_INTERBANK_SUBSCRIBER = "qq_msg_interbank_subscriber";
    public static final String QQ_MESSAGE_OFFLINE_SUBSCRIBER = "qq_msg_offline_subscriber";
    public static final String MANUAL_QQ_MESSAGE_INTERBANK_SUBSCRIBER = "manual_qq_msg_interbank_subscriber";
    public static final String MANUAL_QQ_MESSAGE_OFFLINE_SUBSCRIBER = "manual_qq_msg_offline_subscriber";
    private static final String APPLICATION_MESSAGEBUS = "application_messagebus.";
    private static final String MESSAGEBUS = "messagebus.";
    private static final String QUOTE_TOPIC = "quote_persistence_topic";
    private static final String QQ_MESSAGEBUS_OFFLINE = "qqmessagebus.offline.";
    private static final String QQ_MESSAGEBUS_INTERBANK = "qqmessagebus.interbank.";
    private static final String MANUAL_QQ_MESSAGEBUS_OFFLINE = "manualqqmessagebus.offline.";
    private static final String MANUAL_QQ_MESSAGEBUS_INTERBANK = "manualqqmessagebus.interbank.";

    private static final String MESSAGEBUS_TYPE = "type";
    private static final String MESSAGEBUS_URL = "url";
    private static final String QQ_TOPIC = "topic";


    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment env) {
        this.propertyResolver = new RelaxedPropertyResolver(env, APPLICATION_MESSAGEBUS);
    }

    @Bean(name = APPLICATION_MESSAGE_BUS_QUOTE_SUBSCRIBER, autowire = Autowire.BY_NAME)
    public MessageBusSubscriber messageBusSubscriber() {
        return setMessageBusSubscriberParams(MESSAGEBUS,QUOTE_TOPIC);
    }

    @Bean(name = QQ_MESSAGE_OFFLINE_SUBSCRIBER, autowire = Autowire.BY_NAME)
    public MessageBusSubscriber qqMessageOfflieSubscriber() {
        return setMessageBusSubscriberParams(QQ_MESSAGEBUS_OFFLINE,QQ_TOPIC);
    }

    @Bean(name = QQ_MESSAGE_INTERBANK_SUBSCRIBER, autowire = Autowire.BY_NAME)
    public MessageBusSubscriber qqMessageInterbankSubscriber() {
        return setMessageBusSubscriberParams(QQ_MESSAGEBUS_INTERBANK,QQ_TOPIC);
    }

    @Bean(name = MANUAL_QQ_MESSAGE_OFFLINE_SUBSCRIBER, autowire = Autowire.BY_NAME)
    public MessageBusSubscriber manualQQMessageOfflieSubscriber() {
        return setMessageBusSubscriberParams(MANUAL_QQ_MESSAGEBUS_OFFLINE,QQ_TOPIC);
    }

    @Bean(name = MANUAL_QQ_MESSAGE_INTERBANK_SUBSCRIBER, autowire = Autowire.BY_NAME)
    public MessageBusSubscriber manualQQMessageInterbankSubscriber() {
        return setMessageBusSubscriberParams(MANUAL_QQ_MESSAGEBUS_INTERBANK,QQ_TOPIC);
    }

    //设置需要读取的队列信息.
    private MessageBusSubscriber setMessageBusSubscriberParams(String msgPrefix,String topic){
        String typeKey = msgPrefix + MESSAGEBUS_TYPE;
        String urlKey = msgPrefix + MESSAGEBUS_URL;
        String topicKey = msgPrefix + topic;
       return getMessageBusSubscriber(typeKey, urlKey, topicKey);
    }

    private MessageBusSubscriber getMessageBusSubscriber(String busTypeKey, String urlKey, String topicKey) {
        String messagebusType = propertyResolver.getProperty(busTypeKey);
        String url = propertyResolver.getProperty(urlKey);
        String topic = propertyResolver.getProperty(topicKey);
        MessageBusSubscriber messageBusSubscriber;
        if (MessageBusType.ACTIVEMQ.name().equals(messagebusType)) {
            messageBusSubscriber = new ActiveMQSubscriber(url);
        } else {
            messageBusSubscriber = new QpidSubscriber(url);
        }
        ((AbstractMessageBusSubPub) messageBusSubscriber).setTopic(topic);
        return messageBusSubscriber;
    }

    @Bean(name = APPLICATION_MESSAGE_BUS_QUOTE_PUBLISHER, autowire = Autowire.BY_NAME)
    public MessageBusPublisher messageBusPublisher() {
        String messagebusTypeS = propertyResolver.getProperty(MESSAGEBUS + MESSAGEBUS_TYPE);
        String url = propertyResolver.getProperty(MESSAGEBUS + MESSAGEBUS_URL);
        String topic = propertyResolver.getProperty(MESSAGEBUS + QUOTE_TOPIC);
        String messagebusType = propertyResolver.getProperty(messagebusTypeS);
        MessageBusPublisher messageBusPublisher = null;

        if (MessageBusType.ACTIVEMQ.name().equals(messagebusType)) {
            messageBusPublisher = new ActiveMQPublisher(url);
        } else {
            messageBusPublisher = new QpidPublisher(url);
        }
        ((AbstractMessageBusSubPub) messageBusPublisher).setTopic(topic);
        return messageBusPublisher;
    }

    @Bean(initMethod = "init")
    public GatewayManager gatewayManager(){
        return new GatewayManager();
    }

    @Bean(initMethod = "init")
    public LocalMessageBusManager localMessageBusManager(){
        return new LocalMessageBusManager();
    }

}
