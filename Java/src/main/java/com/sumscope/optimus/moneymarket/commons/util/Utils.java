package com.sumscope.optimus.moneymarket.commons.util;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteDetailsQueryParameters;
import com.sumscope.optimus.moneymarket.model.dbmodel.UserPreference;
import com.sun.corba.se.spi.orbutil.fsm.StateImpl;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {
    private  static final  Boolean ADD_HEADER_ORIGIN =true;

    private  static final String[] SPECIAL_CHARACTERS = new String[]{"'","\\"};
    private  static final String[] SPECIAL_CHARACTERS_SIGN = new String[]{"'","‘"};
    private  Utils(){}
    /**
     * 判断字符串里是否含有关键词
     * 
     * @param str
     * @param keyword
     * @return
     */
    public static boolean isMatched(String str, String keyword){
        if(StringUtils.isBlank(str)){
            return false;
        }
        if(StringUtils.isBlank(keyword)){
            return true;
        }
        return str.indexOf(keyword) >= 0;
    }
    

    //转换特殊符号\ '
    public static void keyWord(MmQuoteDetailsQueryParameters queryParameters){
            if(queryParameters.getMemo()!=null){
                for(int i=0;i<SPECIAL_CHARACTERS.length;i++){
                    if(queryParameters.getMemo().contains(SPECIAL_CHARACTERS[i])){
                        String keyWord=queryParameters.getMemo().replace(SPECIAL_CHARACTERS[i],"");
                        queryParameters.setMemo(keyWord);
                    }
                }
            }
    }

    //跨域请求addHeaderOrigin参数为可配置
    public static void addHeaderOrigin(HttpServletRequest request, HttpServletResponse response){
        if(ADD_HEADER_ORIGIN){
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
        }else{
            response.setHeader("Content-Type","application/json;charset=UTF-8");
        }
    }
    //跨域OPTIONS请求
    public static void getMethodOptions(HttpServletRequest request, HttpServletResponse response){
        try {
            response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.flush();
            return;
        } catch (IOException e) {
            LogManager.error("写入response出现错误。"+e.toString());
        }
    }
    public static Map jsonToObject(String jsonStr){
        JSONObject jsonObj = new JSONObject(jsonStr);
        Iterator<String> it = jsonObj.keys();
        String userId;
        String institutionId;
        Map<String, String> outMap = new HashMap<String, String>();
        while (it.hasNext()) {
            userId = it.next();
            institutionId=userId.contains("'")? userId.replace("'",""):userId;
            outMap.put(institutionId, jsonObj.getString(userId));
        }
        return outMap;
    }

    /**
     * BigDecimal 保留两位小数
     * @param param
     * @return
     */
    public static BigDecimal getBigDecimalToTwoDecimal(BigDecimal param){
        return param.setScale(2, BigDecimal.ROUND_CEILING);
    }

    public static String validate(String params){
            if(params!=null){
                for(int i=0;i<SPECIAL_CHARACTERS_SIGN.length;i++){
                    if(params.contains(SPECIAL_CHARACTERS_SIGN[i])){
                        params= params.replace(SPECIAL_CHARACTERS_SIGN[i],"");
                    }
                }
            }
        return params;
    }
    public static String validateStr(String param){
        if (param != null){
            String regEx="'";
            return validateUtils(param, regEx);
        }
        return param;
    }
    private static String validateUtils(String param, String regEx) {
        Pattern p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(param);
        return m.replaceAll("").trim();
    }

    public static Boolean isPositiveInteger(String param) {
        if (param != null) {
            String regEx = "[1-9]\\d*|0";
            Pattern p = Pattern.compile(regEx);
            return  p.matcher(param).matches();
        }

        return false;
    }
}
