package com.sumscope.optimus.moneymarket.gatewayinvoke;

import com.alibaba.fastjson.JSONObject;
import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.gatewayinvoke.model.QbUserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/26.
 */
@Component
public class GatewayMobileServiceImpl implements GatewayMobileService {

    @Autowired
    private ShiborMobile shiborMobile;

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false)
    public Map<String,QbUserId> getQbUserId(){
        String responseLogin =shiborMobile.login();
        Map<String,QbUserId> qBQmList=new HashMap<>();
        Boolean sign=true;
        int startPage=0;
        while (sign){
            startPage= startPage+1;
            Map<String,QbUserId> shibor = shiborMobile.getShiborInvoke(getQbQmMapperJson(startPage),responseLogin);
            if(shibor!=null && shibor.size()>0){
                qBQmList.putAll(shibor);
            }
            sign= (shibor==null||shibor.size()<5000) ? false : true;
        }
        return qBQmList;

    }
    private String getQbQmMapperJson(int startPage){
        JSONObject  jo=new JSONObject();
        jo.put("User","MM");
        jo.put("DataSourceId","104");
        jo.put("ApiName","idb_account_map");
        jo.put("ApiVersion","N");
        jo.put("StartPage",startPage);
        jo.put("PageSize","5000");
        return jo.toJSONString();
    }

}
