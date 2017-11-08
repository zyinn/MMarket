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
 * Created by fan.bai on 2016/5/7.
 * * QQ同业理财信息监听器，监听器本身不处理业务逻辑，仅将数据转交给业务服务进行处理
 */
@Component
public class QQInterbankMessageListener extends AbstractQQMsgListener {
    private static final String GUARANT_TYPE = "guaranteed"; //是否保本
    private static final String FM_PARSER_RESULT = "fm_parser_result";
    private static final String RISK_LEVEL ="risk_level";

    @Autowired
    private QQMessageArriveProcessor qqMessageArriveProcessor;


    @Autowired
    private QQMessageToQuoteConverter qqMessageToQuoteConverter;


    @Override
    protected void processQQJmsMessage(Map<String, Object> map) {
        try{
            MmQuote mmQuote = convertQQInterbankMsgToQuote(map);
            qqMessageArriveProcessor.processQQQuoteInTransaction(mmQuote);

        }catch (Exception e){
            LogManager.warn("自动解析QQ同业理财接入数据失败。"+map.toString());
        }
    }

    /**
     * 转换QQ 同业理财信息至报价单Dto
     *
     * @param qqMsg QQ报价信息
     * @return MmQuoteDto
     */
    private MmQuote convertQQInterbankMsgToQuote(Map<String, Object> qqMsg) {
        Integer guarantType = Integer.valueOf(qqMessageToQuoteConverter.getPaserResults(qqMessageToQuoteConverter.getJsonNode(qqMsg), FM_PARSER_RESULT, GUARANT_TYPE).asInt());
        QuoteType quoteType;
        if (guarantType != null && guarantType == 2) {
            quoteType = QuoteType.GTF;
        } else {
            quoteType = QuoteType.UR2;
            String riskLevel =qqMessageToQuoteConverter.getPaserResults(qqMessageToQuoteConverter.getJsonNode(qqMsg), FM_PARSER_RESULT, RISK_LEVEL).asText();
            if(riskLevel!=null && "R3".equals(riskLevel)){
                quoteType = QuoteType.UR3;
            }
        }
        return qqMessageToQuoteConverter.convertQQMsgToQuoteDto(quoteType, qqMsg);
    }
}
