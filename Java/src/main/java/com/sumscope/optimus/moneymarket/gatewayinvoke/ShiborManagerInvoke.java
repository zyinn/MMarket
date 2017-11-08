package com.sumscope.optimus.moneymarket.gatewayinvoke;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.commons.exceptions.ExceptionType;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.util.JsonUtil;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;
import com.sumscope.optimus.moneymarket.commons.util.JsonNodeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用于通过 HTTP请求 获取 SHIBOR 利率
 * <p>
 * Created by Qikai.Yu on 2016/5/5.
 */
@Component
public class ShiborManagerInvoke {
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

    public static final String PERIODCONSTANT = "{period}";

    private String[] periods = {"1D", "7D", "14D", "1M", "2M", "3M", "6M", "9M", "1Y"};
    public JSONArray getHistoryParamCodes(QuoteTimePeriod timePeriod) {
        String quoteTimePeriod;
        if (timePeriod==QuoteTimePeriod.T1D){
            quoteTimePeriod="SHIBOR_O/N";
        }else if(timePeriod==QuoteTimePeriod.T7D){
            quoteTimePeriod="SHIBOR_1W";
        }else if(timePeriod==QuoteTimePeriod.T14D){
            quoteTimePeriod="SHIBOR_2W";
        }else{
            quoteTimePeriod="SHIBOR_"+timePeriod.name().replace("T","");
        }
        JSONArray array=new JSONArray();
        array.add(quoteTimePeriod);
        return array;


    }
    public String ShiborJsonToHistory(int startPage,String startDate,String endDate,JSONArray ShiborInvokeCodes){
        JSONObject obj = new JSONObject();
        obj.put("User","MM");
        obj.put("ApiName","V_History_Interest_Rate");
        obj.put("ApiVersion","N");
        obj.put("DataSourceId",100);
        obj.put("Columns",converterToShiborInvokeColumns());
        obj.put("Codes",ShiborInvokeCodes);
        obj.put("Conditions","ORDER BY Source_code, Index_Date DESC");
        obj.put("PageSize",5000);
        obj.put("StartPage",startPage);
        obj.put("StartDate",startDate);
        obj.put("EndDate",endDate);
        StringWriter out = new StringWriter();
        obj.writeJSONString(out);
        String jsonText = out.toString();
        System.out.print(jsonText);
        return jsonText;
    }

    public String ShiborJson(int startPage){
        JSONObject obj = new JSONObject();
        obj.put("User","MM");
        obj.put("ApiName","V_Interest_Rate");
        obj.put("ApiVersion","N");
        obj.put("DataSourceId",100);
        obj.put("Columns",converterToShiborInvokeColumns());
        obj.put("Codes",converterToShiborInvokeCodes());
        obj.put("Conditions","ORDER BY Source_code, Index_Date DESC");
        obj.put("PageSize",5000);
        obj.put("StartPage",startPage);
        StringWriter out = new StringWriter();
        obj.writeJSONString(out);
        String jsonText = out.toString();
        System.out.print(jsonText);
        return jsonText;
    }
    public  JSONArray converterToShiborInvokeColumns(){
        JSONArray array = new JSONArray();
        array.add("Index_Date");
        array.add("Index_Value");
        array.add("unit_type_local");
        array.add("series_name_local");
        array.add("source_code");
        return array;
    }
    public  JSONArray converterToShiborInvokeCodes(){
        JSONArray array = new JSONArray();
        array.add("SHIBOR_O/N");
        array.add("SHIBOR_1W");
        array.add("SHIBOR_2W");
        array.add("SHIBOR_1M");
        array.add("SHIBOR_2M");
        array.add("SHIBOR_3M");
        array.add("SHIBOR_6M");
        array.add("SHIBOR_9M");
        array.add("SHIBOR_1Y");
        return array;
    }

    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false)
    private String login(){
        String paramLogin = getParamLoginJson();
        String responseLogin = service.sendHttpRequest(cdhLogin, paramLogin);
        return responseLogin;
    }

    public List<ResultTable> getShiborInvoke(String param,String sign) {
        String responseLogin =login();
        List<ResultTable> resultMap = new ArrayList<>();
        if (responseLogin != null) {
            Map map = JsonUtil.readValue(responseLogin, Map.class);
            //验证用户是否登陆成功
            if ("OK".equals(map.get("message")) && map.get("code").equals(0)) {
                try {
                    String url = urlTemplate;
                    String response = service.sendHttpRequest(url, param);
                    if (response != null) {
                        String jsonString = JsonUtil.writeValueAsString(JsonNodeUtils.getJsonNode(response).get("resultTable"));
                        JsonNode jsonNode = JsonNodeUtils.getJsonNode(jsonString);
                        for (int i = 0; i < jsonNode.size(); i++) {
                            ResultTable resultTable = new ResultTable();
                            getResultMap(sign, jsonString, i, resultTable);
                            resultMap.add(resultTable);
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

    private void getResultMap(String sign, String jsonString, int i, ResultTable resultTable) {
        if("matrix".equals(sign)){
            String Index_Date = JsonNodeUtils.getJsonNode(jsonString).get(i).path("Index_Date").asText();
            String index_value = JsonNodeUtils.getJsonNode(jsonString).get(i).path("Index_Value").asText();
            String source_code = JsonNodeUtils.getJsonNode(jsonString).get(i).path("source_code").asText();
            String seriesNameLocal = JsonNodeUtils.getJsonNode(jsonString).get(i).path("series_name_local").asText();
            String unitTypeLocal = JsonNodeUtils.getJsonNode(jsonString).get(i).path("unit_type_local").asText();
            resultTable.setIndexDate(Index_Date);
            resultTable.setIndexValue(index_value!="null" ? new BigDecimal(index_value) : null);
            resultTable.setSourcecode(source_code);
            resultTable.setSeriesnamelocal(seriesNameLocal);
            resultTable.setUnittypelocal(unitTypeLocal);
        }else{
            String userId = JsonNodeUtils.getJsonNode(jsonString).get(i).path("userid").asText();
            String accountId = JsonNodeUtils.getJsonNode(jsonString).get(i).path("accountid").asText();
            resultTable.setUserid(userId);
            resultTable.setAccountid(accountId);
        }
    }

    private String getParamLoginJson() {
        StringBuilder sb=new StringBuilder();
        sb.append("{\"User\":\"");
        sb.append(username);
        sb.append("\",\"Password\":\"");
        sb.append(password);
        sb.append("\",\"ExpireInSeconds\":\"");
        sb.append(expireInSeconds);
        sb.append("\"");
        sb.append("}");
        return sb.toString();
    }
    public void getResultTableForNull(List<ShiborManagerInvoke.ResultTable> resultMap, String sourceCode) {
        ShiborManagerInvoke.ResultTable resultTable = new ShiborManagerInvoke.ResultTable();
        resultTable.setIndexDate(null);
        resultTable.setIndexValue(null);
        resultTable.setSeriesnamelocal(null);
        resultTable.setUnittypelocal(null);
        resultTable.setSourcecode(sourceCode);
        resultMap.add(resultTable);
    }
     class QbUserId{
        @JsonProperty("id")
        private String id;//id
        @JsonProperty("userid")
        private String userid;//userid是qmid
        @JsonProperty("accountid")
        private String accountid;//accountid是qbid

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getAccountid() {
            return accountid;
        }

        public void setAccountid(String accountid) {
            this.accountid = accountid;
        }
    }
    /**
     *
     *
     */
     public class ResultTable extends QbUserId{

        @JsonProperty("Index_Date")
        private String indexDate;
        @JsonProperty("Index_Value")
        private BigDecimal indexValue;
        @JsonProperty("unit_type_local")
        private String unittypelocal;
        @JsonProperty("series_name_local")
        private String seriesnamelocal;
        @JsonProperty("source_code")
        private String sourcecode;

        public void setIndexDate(String indexDate) {
            this.indexDate = indexDate;
        }

        public String getIndexDate() {
            return indexDate;
        }

        public void setIndexValue(BigDecimal indexValue) {
            this.indexValue = indexValue;
        }

        public BigDecimal getIndexValue() {
            return indexValue;
        }

        public String getUnittypelocal() {
            return unittypelocal;
        }

        public void setUnittypelocal(String unittypelocal) {
            this.unittypelocal = unittypelocal;
        }

        public String getSeriesnamelocal() {
            return seriesnamelocal;
        }

        public void setSeriesnamelocal(String seriesnamelocal) {
            this.seriesnamelocal = seriesnamelocal;
        }

        public String getSourcecode() {
            return sourcecode;
        }

        public void setSourcecode(String sourcecode) {
            this.sourcecode = sourcecode;
        }

    }

    public enum SourceCode {
        T1D("SHIBOR_O/N"),
        T7D("SHIBOR_1W"),
        T14D("SHIBOR_2W"),
        T1M("SHIBOR_1M"),
        T2M("SHIBOR_2M"),
        T3M("SHIBOR_3M"),
        T6M("SHIBOR_6M"),
        T9M("SHIBOR_9M"),
        T1Y("SHIBOR_1Y");

        private String displayName;

        SourceCode( String name) {
            this.displayName = name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

    @CacheMe(timeout = 60 * 60, synchornizeUpdate = false)
    public Double[] getShibor() {
        Double[] results = new Double[periods.length];
        for (int i = 0; i < periods.length; i++) {
            try {
                String url = urlTemplate.replace(PERIODCONSTANT, periods[i]);
                String response = service.sendHttpRequest(url, "");
                if (response != null) {
                    Map map = JsonUtil.readValue(response, Map.class);
                    double rate = parseShiborFromResult(map);
                    results[i] = rate;
                } else {
                    results[i] = 0.0;
                }
            } catch (Exception e) {
                results[i] = 0.0;
                ExceptionType et = BusinessRuntimeExceptionType.OTHER;
                LogManager.error(new BusinessRuntimeException(et, e));
            }
        }
        return results;
    }

    /**
     * 测试环境无法保证每次可以正确获得结果, 有可能取到 null 值 生产环境未知
     *
     * @param map
     * @return
     */
    private Double parseShiborFromResult(Map map) {
        try {
            List l = (List) map.get("data");
            if ("Success".equals(map.get("result")) && l.size() > 0) {
                Object rate = ((Map) (l.get(0))).get("indexValue");
                if (rate != null) {
                    return (Double) rate;
                } else {
                    return 0.0;
                }
            }
            return 0.0;
        } catch (Exception e) {
            LogManager.error("Get error when parse shibor result map :" + map);
            return 0.0;
        }
    }
}