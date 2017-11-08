package com.sumscope.optimus.moneymarket.wsandlocalmessage;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by fan.bai on 2016/7/29.
 * 用于启动本地监听器的管理类，通过Configuration类的@Bean方法启动
 */
public class LocalMessageBusManager {
    @Autowired
    private GatewayQuoteMessageService busMessageService;

    public void init(){
        busMessageService.start();
    }

    public void uninit(){
        busMessageService.stop();
    }
}
