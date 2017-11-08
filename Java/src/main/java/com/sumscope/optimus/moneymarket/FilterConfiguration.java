package com.sumscope.optimus.moneymarket;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web容器的过滤器控制类
 */
@Configuration
public class FilterConfiguration {
    @Bean
    public FilterRegistrationBean shallowEtagHeaderFilter() {
        FilterRegistrationBean registration=new FilterRegistrationBean();
        registration.setFilter(new RequestClientCacheFilter());
        return registration;
    }
}
