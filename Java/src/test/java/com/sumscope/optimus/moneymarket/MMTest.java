package com.sumscope.optimus.moneymarket;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * 本类中的 start, stop 方法暂时不需要使用, 因为通过标注可以自行启动 web 实例
 *
 * 本类中可以添加各个测试类需要的共用方法
 *
 * Created by Qikai.Yu on 2016/5/16.
 */

public class MMTest{
    protected static ConfigurableApplicationContext context = null;

    public void start(){
        if(context == null){
            System.out.println("prepare start ..................");
            System.out.println(Thread.currentThread().getName()+" prepare start ..................");
            String[] args = {};
            System.setProperty("application.port", "9999");
            ConfigurableApplicationContext cac = new SpringApplicationBuilder(
                    ServletInitializer.class).run(args);
            context = cac;
            System.out.println("start finished ..................");
        }
    }

    public static void stop(){
        context.close();
    }
}