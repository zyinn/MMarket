package com.sumscope.optimus.moneymarket.gatewayinvoke;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.commons.exceptions.ExceptionType;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.util.JsonUtil;
import com.sumscope.optimus.moneymarket.gatewayinvoke.model.QbUserId;
import org.json.JSONArray;
import org.json.JSONObject;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/16.
 */
@Component
public class ShiborMobile {

    @Autowired
    private GatewayHTTPService service;
    @Value("${external.shiborUrl}")
    private String urlTemplate;
    @Value("${external.cdhLogin}")
    private String cdhLogin;
    @Value("${external.username}")
    private String username;
    @Value("${external.password}")
    private String password;
    @Value("${external.expireInSeconds}")
    private String expireInSeconds;


    public String login(){
        String paramLogin = getParamLoginJson();
        String responseLogin = service.sendHttpRequest(cdhLogin, paramLogin);
        return responseLogin;
    }

    public Map<String,QbUserId> getShiborInvoke(String param,String responseLogin) {
        Map<String,QbUserId> resultMap = new HashMap<>();
        if (responseLogin != null) {
            Map map = JsonUtil.readValue(responseLogin, Map.class);
            //验证用户是否登陆成功
            if ("OK".equals(map.get("message")) && map.get("code").equals(0)) {
                try {
                    String url = urlTemplate;
                    String response = service.sendHttpRequest(url, param);
                    if (response != null) {
                        JSONObject dataJson = new JSONObject(response);
                        JSONArray array=dataJson.getJSONArray("resultTable");
                        for(int i=0; i<array.length();i++){
                            QbUserId qbUserId = (QbUserId) JSONToObj(array.get(i).toString(), QbUserId.class);
                            resultMap.put(qbUserId.getAccountid(),qbUserId);
                        }
                        return resultMap;
                    }
                } catch (Exception e) {
                    ExceptionType et = BusinessRuntimeExceptionType.OTHER;
                    LogManager.error(new BusinessRuntimeException(et, e));
                }
            }
        }
        return resultMap;
    }
    public static<T> Object JSONToObj(String jsonStr,Class<T> obj) {
        T t = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            t = objectMapper.readValue(jsonStr,
                    obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
    private String getParamLoginJson() {
        JSONObject jo=new JSONObject();
        jo.put("User",username);
        jo.put("Password",password);
        jo.put("ExpireInSeconds",expireInSeconds);
        return jo.toString();
    }


}
