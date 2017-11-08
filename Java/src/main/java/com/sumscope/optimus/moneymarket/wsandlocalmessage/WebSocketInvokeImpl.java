package com.sumscope.optimus.moneymarket.wsandlocalmessage;

import com.sumscope.optimus.commons.cachemanagement.SSCacheManagementFactory;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.util.JsonUtil;
import com.sumscope.optimus.commons.websocket.WebSocketManager;
import com.sumscope.optimus.commons.websocket.WebSocketSender;
import com.sumscope.optimus.moneymarket.WebSocketConfig;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.facade.converter.MmQuoteDtoConverter;
import com.sumscope.optimus.moneymarket.facade.converter.MmQuoteQueryListResultDtoConverter;
import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteDto;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteQueryListResultDto;
import com.sumscope.optimus.moneymarket.service.InstitutionService;
import com.sumscope.optimus.moneymarket.service.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fan.bai on 2016/7/20.
 * WebSocketInvoke 接口实现类
 * 向前端发送Dto，并通知可能的缓存更新
 */
@Component
public class WebSocketInvokeImpl implements WebSocketInvoke {
    @Autowired
    @Qualifier(value = WebSocketConfig.DETAILS_WEBSOCKET_SENDER)
    private WebSocketSender webSocketSender;

    @Autowired
    @Qualifier(value = WebSocketConfig.MAIN_WEBSOCKET_SENDER)
    private WebSocketSender webMainSocketSender;

    @Autowired
    private MmQuoteDtoConverter mmQuoteDtoConverter;

    @Autowired
    private MmQuoteQueryListResultDtoConverter mmQuoteQueryListResultDtoConverter;

    @Autowired
    private UserBaseService userBaseService;

    @Autowired
    private InstitutionService institutionService;

    private Timer timer;
    public WebSocketInvokeImpl() {
        timerOnlineNum();
    }

    public void timerOnlineNum() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Integer integer = new Integer(webSocketSender.webSocketCount());
                if (integer != null) {
                    LogManager.onlineNum(integer.toString());
                }
            }
        },100, 5*60*1000);
    }

    @Override
    public void sendQuoteInListResponseDtoFomart(MmQuote mmQuote) {
        notifyPossibleCacheUpdate(mmQuote);

        MmQuoteQueryListResultDto resultDtos = mmQuoteQueryListResultDtoConverter.convertSingleModelToDto(mmQuote);
        //send to wsandlocalmessage
        String message = generateWebSocketMessage(resultDtos);
        webSocketSender.sendToAllClient(message);

        // 通过webMainSocketSender通道向web端发送MmQuoteDto
        List<MmQuoteDto> mmQuoteDtos = new ArrayList<>();
        MmQuoteDto mmQuoteDto = mmQuoteDtoConverter.convertMmQuoteToMmQuoteDto(mmQuote);
        mmQuoteDtos.add(mmQuoteDto);
        String jsonStr = generateWebMainSocketMessage(mmQuoteDtos);
        webMainSocketSender.sendToAllClient(jsonStr);
    }

    //查询机构或者用户时根据报价机构及报价人列表查询。该查询做了缓存，因此新增的报价单如果是一个用户新创建的，需要通知
    //更新缓存
    private void notifyPossibleCacheUpdate(MmQuote mmQuote) {
        String quoteUserId = mmQuote.getQuoteUserId();
        User user = userBaseService.retreiveAllUsersGroupByUserID().get(quoteUserId);
        if(user != null){
            SSCacheManagementFactory.notifyUpdateResult(user, Constant.QUOTE_CREATED);
        }

        String institutionId = mmQuote.getInstitutionId();
        Institution institution = institutionService.retrieveInstitutionsById().get(institutionId);
        if(institution != null){
            SSCacheManagementFactory.notifyUpdateResult(institution,Constant.QUOTE_CREATED);
        }

        SSCacheManagementFactory.notifyUpdateResult(mmQuote,Constant.QUOTE_CREATED);
    }

    private String generateWebSocketMessage(MmQuoteQueryListResultDto dto) {
        String message = JsonUtil.writeValueAsString(dto);
        return "{\"messageType\" : \"QUOTES_CHANGE\", \"message\" : " + message + "}";
    }

    private String generateWebMainSocketMessage(List<MmQuoteDto> mmQuoteDtos) {
        String message = JsonUtil.writeValueAsString(mmQuoteDtos);
        return "{\"return_code\" : 0,\"return_message\" : \"Success\",\"return_type\" : 2,\"result_count\" : "
                + mmQuoteDtos.size() + ",\"result\" : " + message + "}";
    }
}
