package com.sumscope.optimus.moneymarket.commons.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by fan.bai on 2016/10/31.
 * HTTPResponse使用的一些公用方法
 */
public class HTTPResponseUtil {

    /**
     * 将字符串直接写入HttpServletResonse返回前端.
     */
    public static void returnMessageThroughHttpServletResponse(String message,HttpServletResponse response){
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter e ;
        try {
            e = response.getWriter();
            e.append(message);
            e.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
