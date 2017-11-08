package com.sumscope.optimus.moneymarket;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 配置前端缓存，强制所有文件，除了图片外，不使用客户端缓存。否则在升级时容易引起意料之外的问题。
 */
public class RequestClientCacheFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //do logic before this filter
        if(servletResponse instanceof HttpServletResponse){
            HttpServletResponse response = (HttpServletResponse)servletResponse;
            if(isPictureRequest(servletRequest)){
                response.setDateHeader("expires", -1);
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");

            }else{
                response.setDateHeader("expires", -1);
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Pragma", "no-store");
            }

        }
        filterChain.doFilter(servletRequest,servletResponse);

        //do logic after this filter


    }

    private boolean isPictureRequest(ServletRequest servletRequest) {
        if(servletRequest instanceof  HttpServletRequest){
            HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
            String queryString = httpServletRequest.getRequestURI();
            if(queryString!=null && queryString.length() > 4){
                String endString = queryString.substring(queryString.length() - 3, queryString.length());
                endString = endString.toUpperCase();
                if("JPG".equals(endString) || "PNG".equals(endString) || "BMP".equals(endString)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
