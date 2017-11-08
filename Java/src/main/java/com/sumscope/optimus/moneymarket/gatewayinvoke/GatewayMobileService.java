package com.sumscope.optimus.moneymarket.gatewayinvoke;

import com.sumscope.optimus.moneymarket.gatewayinvoke.model.QbUserId;

import java.util.Map;

/**
 * Created by Administrator on 2016/10/26.
 */
public interface GatewayMobileService {

    Map<String,QbUserId> getQbUserId();
}
