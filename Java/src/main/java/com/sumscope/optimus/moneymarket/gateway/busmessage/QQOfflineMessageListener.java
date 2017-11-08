package com.sumscope.optimus.moneymarket.gateway.busmessage;

import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.gateway.busmessage.converter.QQMessageToQuoteConverter;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.service.listener.QQMessageArriveProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by fan.bai on 2016/5/9.
 * QQ线下资金信息监听器，监听器本身不处理业务逻辑，仅将数据转交给业务服务进行处理
 */
@Component
public class QQOfflineMessageListener  extends AbstractQQMsgListener {
    @Autowired
    private QQMessageArriveProcessor qqMessageArriveProcessor;

    @Autowired
    private QQMessageToQuoteConverter qqMessageToQuoteConverter;

    @Override
    protected void processQQJmsMessage(Map<String, Object> map) {
        try{
            MmQuote mmQuote = convertQQOfflineMsgToQuoteDto(map);
            qqMessageArriveProcessor.processQQQuoteInTransaction(mmQuote);
        }catch (Exception e){
            LogManager.warn("自动解析QQ线下资金接入数据失败。"+map.toString());
        }
    }
    /**
     * 转换QQ 线下资金信息至报价单Dto
     *
     * @param qqMsg QQ报价信息
     * @return MmQuoteDto
     */
    private MmQuote convertQQOfflineMsgToQuoteDto(Map<String, Object> qqMsg) {
        QuoteType quoteType = QuoteType.IBD;
        return qqMessageToQuoteConverter.convertQQMsgToQuoteDto(quoteType,qqMsg);
    }
}
