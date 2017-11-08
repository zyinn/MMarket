package com.sumscope.optimus.moneymarket.gateway.qbwebserver;

import com.sumscope.QBWebServer;
import com.sumscope.QBWebServerManager;
import com.sumscope.model.QMUserWithStatus;
import com.sumscope.optimus.commons.log.LogManager;
import databus.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qikai.yu on 2016/5/3.
 */
@Component
public class QMBusMessageManager {
    private QBWebServer client;

//    @Autowired
//    private QMMessageCallbackImpl callbackImpl;

//    @Autowired
//    private QMStatusUpdateProcessor processor;

//    @Value("${external.useMockQMUser}")
//    private boolean useMockQMUser;


    public void init() {
        //初始化QMWebServerManager
        // start
        String usrdir = System.getProperty("user.dir");
        String binPath = usrdir + File.separator + "xbin" + File.separator
                + "x64";
        AppConfiguration.GetInstance().SetBinPath(binPath);// your
        // c++
        // library
        // path
        String configPath = usrdir + File.separator + "config";
        AppConfiguration.GetInstance().SetConfigPath(configPath);


//        List<QMUserWithStatus> results = null;
//        boolean useMockQMUser = true;//todo change to property

//        LogManager.info("----QMStatus data list....." + results);

//        if (results != null) {
//            processor.loadAllQMStatus(results);
//            LogManager.info("----QMStatus data loaded.....");
//        }
        LogManager.info("----QMStatus data loaded.....");


    }


//    public QMUserWithStatus getUserByQMId(String qmId) {
//        return QBWebServerManager.getInstance(client).requestUserByQmId(qmId);
//    }

//    public QMUserWithStatus requestUserByQmId(String qmId) {
//        qmId = "10004837";
//        QMUserWithStatus status = QBWebServerManager.getInstance(client).requestUserByQmId(qmId);
//        return status;
//    }

    /**
     * 根据qbId获取QMUserWithStatus 并且按照abId进行分组
     *
     * @param qbIds
     * @return Map<String, QMUserWithStatus>
     */
    public Map<String, QMUserWithStatus> requestUserByQmId(List<String> qbIds) {
        Map<String, QMUserWithStatus> result = new HashMap<String, QMUserWithStatus>();
        if (qbIds == null || qbIds.size() == 0) {
            return null;
        }
        List<QMUserWithStatus> qmUserWithStatuses = QBWebServerManager.getInstance(client).requestQmUserByQbIds(qbIds);

        LogManager.debug(getLogString(qbIds, qmUserWithStatuses));
        if (qmUserWithStatuses != null && qmUserWithStatuses.size() > 0) {
            for (QMUserWithStatus qmUserWithStatus : qmUserWithStatuses) {
                result.put(qmUserWithStatus.getQbId(), qmUserWithStatus);
            }
        }
        return result;
    }

    private String getLogString(List<String> qbIds, List<QMUserWithStatus> qmUserWithStatuses) {
        String logString = "QB ID:";
        if (qbIds != null) {
            for (String id : qbIds) {
                logString += id;
            }
        }
        logString += " QM Status: ";
        if (qmUserWithStatuses != null) {
            for (QMUserWithStatus status : qmUserWithStatuses) {
                logString += " QBID:" + status.getQbId() + " QMID:" + status.getQmId();
            }
        }
        return logString;

    }

}
