package com.sumscope.optimus.moneymarket.gateway;

import com.sumscope.optimus.moneymarket.gateway.busmessage.GatewayQQMessageService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * 管理所有对外接口, 负责启动,停止各个接口
 * 如果有同一个功能, 不同接口分别有实现的情况下, 可以在此进行逻辑处理, 区分不同的处理方式
 * 在configuration中通过@Bean的方式调用
 *
 * Created by qikai.yu on 2016/4/28.
 */
public class GatewayManager {

    @Autowired
    private GatewayQQMessageService qqMessageService;


    public void init(){
        qqMessageService.start();
    }

}
