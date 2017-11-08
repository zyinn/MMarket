package com.sumscope.optimus.moneymarket.wsandlocalmessage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.util.JsonUtil;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 从总线获取的报价消息处理类
 * 发送到Websocket并触发缓存更新。具体代码在WebSocketInvoke对应实现类中实现
 * <p>
 * Created by qikai.yu on 2016/4/29.
 */
@Component
public class NewQuoteArriveProcessorImpl implements NewQuoteArriveProcessor {
    private static final Log logger = LogFactory.getLog(NewQuoteArriveProcessorImpl.class);

    @Autowired
    private WebSocketInvoke webSocketInvoke;



    public void processNewQuote(String content) {
        //从总线获取了新发生的报价信息，进行矩阵缓存的更新，并将新的报价信息通过WebSocket发送前端Web
        List<MmQuote> quotes;
        try {
            //需要使用try catch 语句捕捉所有可能的异常。否则一些发送异常可能导致监听器失效。
            quotes = JsonUtil.readValue(content, new TypeReference<List<MmQuote>>() {
            });
            quotes.forEach(
                    quote -> {
                        if (quote.getMmQuoteDetails() == null || quote.getMmQuoteDetails().size() == 0) {
                            LogManager.warn(logger, "No QuoteDetails in Quote:" + quote.getId());
                        }

                        //send to web
                        webSocketInvoke.sendQuoteInListResponseDtoFomart(quote);
                    }
            );
        } catch (Exception e) {
            LogManager.warn("发送WebSocket信息出现错误："+ e.getLocalizedMessage());
        }



    }


}
