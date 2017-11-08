package com.sumscope.optimus.moneymarket.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.sumscope.optimus.moneymarket.facade.AuthorizationInterceptor;

@Configuration
public class AuthorizationFacadeConfig extends WebMvcConfigurerAdapter {
    
    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
                .excludePathPatterns("/base/validateUser")
                .addPathPatterns("/inner/**", "/offline/**", "/base/**", "/quote/**","/mobile/**");
    }
}
