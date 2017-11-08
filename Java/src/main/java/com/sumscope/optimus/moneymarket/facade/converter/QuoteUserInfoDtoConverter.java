package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.model.QMUserWithStatus;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.gateway.qbwebserver.QMBusMessageManager;
import com.sumscope.optimus.moneymarket.model.dbmodel.ContactUser;
import com.sumscope.optimus.moneymarket.model.dto.QuoteUserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by fan.bai on 2016/8/28.
 * Dto <--> Model 转换器 转换QuoteUserInfoDto 与ContactUser
 */
@Component
public class QuoteUserInfoDtoConverter {
    @Autowired
    private QMBusMessageManager qmManager;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    /**
     * 转换List<ContactUser>-->List<QuoteUserInfoDto>
     */
    public List<QuoteUserInfoDto> convertContactUserListToQuoteUserInfoDtoList(List<ContactUser> contacts) {
        //调取远程接口获取qmId
        List<String> qbIds = new ArrayList<>();
        for (ContactUser contactUser : contacts) {
            qbIds.add(contactUser.getUserId());
        }
        Map<String, QMUserWithStatus> results = getRemoteQMStatusByQbIdsWithCache(qbIds);
        //处理返回结果
        List<QuoteUserInfoDto> quoteUserInfoDtos = new ArrayList<>();
        for (ContactUser contactUser : contacts) {
            QuoteUserInfoDto dto = new QuoteUserInfoDto();
            dto.setQb_id(contactUser.getUserId());
            dto.setName(contactUser.getDisplayName());
            dto.setStatus("");//User表无此字段
            dto.setTelephone(contactUser.getTelephone());
            dto.setMobile(contactUser.getMobile());
            dto.setEmail(contactUser.getEmail());
            //调用接口获取qmId
            if (results != null) {
                QMUserWithStatus status = results.get(contactUser.getUserId());
                if (status == null) {
                    dto.setQmId("");
                } else {
                    dto.setQmId(status.getQmId());
                }
            } else {
                dto.setQmId("");
            }
            quoteUserInfoDtos.add(dto);
        }
        return quoteUserInfoDtos;
    }

    private static Map<String, QMUserWithStatus> cache;//缓存
    private synchronized Map<String, QMUserWithStatus> getCache() {
        if (cache == null) {
            cache = new HashMap<>();
        }
        return cache;
    }
    private Map<String, QMUserWithStatus> getRemoteQMStatusByQbIdsWithCache(List<String> qbIds) {
        Map<String, QMUserWithStatus> result = new HashMap<>();
        Map<String, QMUserWithStatus> cacheMe = getCache();//获取缓存
        List<String> notFoundQbIds = new ArrayList<>();//没有从缓存里找到记录的qbIds的集合
        for (String qbid : qbIds) {
            if (cacheMe.containsKey(qbid)) {
                //如果缓存存在记录-直接从缓存取出放到结果集里
                result.put(qbid, cacheMe.get(qbid));
            } else {
                //如果缓存里不存在记录，添加qbid到List集合
                notFoundQbIds.add(qbid);
            }
        }
        //存在没有从缓存里找到的记录 重新调用接口获取获取
        if (notFoundQbIds.size() > 0) {
            Map<String, QMUserWithStatus> notFoundInCache = getRemoteQMStatusWithinTimoutPeroid(notFoundQbIds);
            if (notFoundInCache.size() > 0) {//查询到了数据
                for (String qbid : notFoundInCache.keySet()) {
                    //新查询出来的数据放入缓存
                    cacheMe.put(qbid, notFoundInCache.get(qbid));
                    //新查询出来的数据放入返回结果集中
                    result.put(qbid, notFoundInCache.get(qbid));
                }
            }
        }
        return result;
    }

    private Map<String, QMUserWithStatus> getRemoteQMStatusWithinTimoutPeroid(List<String> qbIds) {
        Callable callable = new Callable(){
            @Override
            public Map<String, QMUserWithStatus> call() throws Exception {
                long time1 = System.currentTimeMillis();
                Map<String, QMUserWithStatus> stringQMUserWithStatusMap = qmManager.requestUserByQmId(qbIds);
                long time2 = System.currentTimeMillis();
                LogManager.debug("获取QM ID耗时： " + (time2 - time1) );
                return stringQMUserWithStatusMap;
            }
        };

        Future<Map<String, QMUserWithStatus>> qmFuture = executorService.submit(callable);
        try {
            return qmFuture.get(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LogManager.error(new BusinessRuntimeException(BusinessRuntimeExceptionType.OTHER,e));
            return new HashMap<>();
        }
    }
}
