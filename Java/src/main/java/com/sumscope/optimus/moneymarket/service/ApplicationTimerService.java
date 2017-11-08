package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.log.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Created by fan.bai on 2016/5/11.
 * 系统定时任务服务
 */
@Component
@Configurable
@EnableScheduling
@PropertySource("classpath:application.yml")
public class ApplicationTimerService {
    @Autowired
    private DatabaseManagementService databaseManagementService;

    /**
     * 每日过期数据归档服务，定时为每工作日23点开始
     */
    @Scheduled(cron = "${application.achievedata.schedule}")
    public void archiveData(){
        LogManager.info("开始执行系统定时任务，当前时间："+ Calendar.getInstance().getTime());

        //先对价格矩阵数据进行计算并存储，再移除本日过期数据。
        databaseManagementService.takeSnapshotForPriceMatrix();

        databaseManagementService.archiveExpiredData();

        databaseManagementService.deactivateExpiredMmQuoteDetails();

    }
}
