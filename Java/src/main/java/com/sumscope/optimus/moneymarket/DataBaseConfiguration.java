package com.sumscope.optimus.moneymarket;

import com.alibaba.druid.pool.DruidDataSource;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.Constant;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DataBaseConfiguration implements EnvironmentAware {

    private RelaxedPropertyResolver propertyResolver;

    private RelaxedPropertyResolver historypropertyResolver;

    @Value("${zabbix.ip}")
    private String zabbixIp;
    @Value("${zabbix.port}")
    private int zabbixPort;
    @Value("${zabbix.hostName}")
    private String zabbixHostName;

    public void setEnvironment(Environment env) {
        this.propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource-primary.");
        this.historypropertyResolver = new RelaxedPropertyResolver(env, "spring.datasource-history.");
    }

    @Bean(name = Constant.BUSINESS_DATA_SOURCE, destroyMethod = "close", initMethod = "init")
    @Primary
    public DataSource dataSource() {
        LogManager.debug("Configruing DataSource");
        return getDataSource(propertyResolver);
    }

    @Bean(name = Constant.HISTORY_DATA_SOURCE, destroyMethod = "close", initMethod = "init")
    public DataSource historydataSource() {
        LogManager.debug("Configruing historyDataSource");
        return getDataSource(historypropertyResolver);
    }

    private DataSource getDataSource(RelaxedPropertyResolver resolver) {
        LogManager.initZabbix(zabbixIp, zabbixPort, zabbixHostName, "MM");


        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(resolver.getProperty("url"));
        datasource.setDriverClassName(resolver
                .getProperty("driverClassName"));
        datasource.setMaxActive(NumberUtils.toInt(resolver.getProperty("max-active")));
        datasource.setMinIdle(NumberUtils.toInt(resolver.getProperty("min-idle")));
        datasource.setInitialSize(NumberUtils.toInt(resolver.getProperty("initial-size")));
        datasource.setValidationQuery(resolver.getProperty("validation-query"));
        datasource.setUsername(resolver.getProperty("username"));
        datasource.setPassword(resolver.getProperty("password"));
        return datasource;
    }


}