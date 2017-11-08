package com.sumscope.optimus.moneymarket;

import com.sumscope.optimus.commons.cachemanagement.CacheMeAnnAOPProcess;
import com.sumscope.optimus.moneymarket.commons.Constant;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;

/** 
 *  
 * 数据库的连接信息，在application.yml中配置，并指定特定的前缀 
 *  
 */  
@Configuration  
@AutoConfigureAfter({ DataBaseConfiguration.class })  
@ComponentScan
public class MybatisConfiguration{  

    @Resource(name= Constant.BUSINESS_DATA_SOURCE)
    private DataSource dataSource;

    @Resource(name = Constant.HISTORY_DATA_SOURCE)
    private DataSource historyDataSource;
    
    @Bean
    public CacheMeAnnAOPProcess cacheMeAnnAOPProcess(){
        return new CacheMeAnnAOPProcess();
    }

    @Bean(name = Constant.BUSINESS_TRANSACTION_MANAGER)
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = Constant.HISTORY_TRANSACTION_MANAGER)
    public PlatformTransactionManager historyTransactionManager() {
        return new DataSourceTransactionManager(historyDataSource);
    }

    @Bean(name=Constant.BUSINESS_SQL_SESSION_TEMPLATE)
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
            return getSqlSessionTemplate(dataSource);
    }
    
    @Bean(name=Constant.HISTORY_SQL_SESSION_TEMPLATE)
    public SqlSessionTemplate historysqlSessionTemplate() throws Exception {
        return getSqlSessionTemplate(historyDataSource);
    }

    private SqlSessionTemplate getSqlSessionTemplate(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = newSqlSessionFactory(dataSource);
        return new SqlSessionTemplate(sessionFactory.getObject());
    }

    private SqlSessionFactoryBean newSqlSessionFactory(DataSource dataSource) throws IOException {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setFailFast(true);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/com/sumscope/optimus/moneymarket/mapping/*.xml"));
        return sessionFactory;
    }


}  
