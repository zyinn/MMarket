package com.sumscope.optimus.moneymarket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.web.SpringBootServletInitializer;


/**
 * 用于开发人员本地使用mvn spring-boot:run 启动使用
 */
@SpringBootApplication
public class ServletInitializer extends SpringBootServletInitializer implements
        EmbeddedServletContainerCustomizer {
    @Value("${application.port}")
    private int applicationPort;

    public static void main(String[] args) {
        SpringApplication.run(ServletInitializer.class, args);
    }

    public void customize(
            ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
        configurableEmbeddedServletContainer.setContextPath("/money_market");
        configurableEmbeddedServletContainer.setPort(applicationPort);
    }

}
