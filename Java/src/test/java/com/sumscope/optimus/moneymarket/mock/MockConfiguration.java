package com.sumscope.optimus.moneymarket.mock;

import com.sumscope.optimus.moneymarket.DataBaseConfiguration;
import com.sumscope.optimus.moneymarket.MybatisConfiguration;
import com.sumscope.optimus.moneymarket.dao.MmQuoteDao;
import com.sumscope.optimus.moneymarket.gatewayinvoke.ShiborManagerInvoke;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** 
 *  
 * 数据库的连接信息，在application.yml中配置，并指定特定的前缀 
 *  
 */  
@Configuration  
@AutoConfigureAfter({ DataBaseConfiguration.class,MybatisConfiguration.class })
@ComponentScan
public class MockConfiguration {

    @Bean
    public MmQuoteDao mmQuoteDao(){
        MmQuoteDao mock = mock(MmQuoteDao.class);
        return mock;
    }

    @Bean
    public ShiborManagerInvoke shiborManager(){
        ShiborManagerInvoke manager = mock(ShiborManagerInvoke.class);
        Double[] results = {1.0,1.0,1.0,1.0,1.0,1.0,1.0};
        when(manager.getShibor()).thenReturn(results);
        return manager;
    }

}  