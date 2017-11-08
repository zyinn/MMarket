package com.sumscope.optimus.moneymarket.gateway.busmessage.converter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteDetails;
import com.sumscope.optimus.moneymarket.service.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * QQ报价实体转化
 */
@Service
public class QQMessageToQuoteConverter {
    //同业理财
    private static final String FM_AMOUNT = "fm_amount"; //数量
    private static final String FM_DAYS_LOW = "fm_days_low"; //天数区间
    private static final String FM_DAYS_HIGH = "fm_days_high";
    private static final String FM_RATE = "fm_rate";  //利率没有区间
    private static final String FM_SIDE = "fm_side";  //进出方向
    private static final String FM_PARSER_RESULT = "fm_parser_result";
    private static final String FM_TENORS = "fm_tenors";
    private static final String FM_CURRENCY = "fm_currency";

    //共有区域数据.
    private static final String QQ_MSG_DATA = "msgData";  //备注
    private static final String QQ_PARSEDMSG = "parsedMsg";
    private static final String QQ_ID = "senderuin";  //发布报价的QQ号
    private static final int CNY = 156; //ISO中国货币数字代码

    //线下(同存)
    private static final String CD_AMOUNT = "cd_amount";  //数量
    private static final String CD_DAYS = "maturity";  //期限
    private static final String CD_RATE = "cd_rate";
    private static final String CD_SIDE = "cd_side";
    private static final String CD_PARSER_RESULT = "cd_parser_result";
    private static final String CD_TENORS = "cd_tenors";
    private static final String CD_CURRENCY = "cd_currency";

    @Autowired
    private UserBaseService userBaseService;

    //将map转为JsonNode对象,直接对数据操作.
    public JsonNode getJsonNode(Map<String, Object> qqMsg) {
        ObjectMapper mapper = new ObjectMapper();
        if (qqMsg != null) {
            try {
                return mapper.readTree(JSONObject.toJSONString(qqMsg));
            } catch (IOException e) {
                LogManager.error(e.toString());
            }
        }
        return null;
    }

    public MmQuote convertQQMsgToQuoteDto(QuoteType quoteType, Map<String, Object> qqMsg) {
        JsonNode params = getJsonNode(qqMsg);
        LogManager.debug(params.toString());
        return getMmQuote(params, quoteType);
    }

    private MmQuote getMmQuote(JsonNode qqMsg, QuoteType quoteType) {
        MmQuote mmQuote = new MmQuote();
        getMmQuotes(qqMsg, quoteType, mmQuote);
        mmQuote.setMmQuoteDetails(getMmQuoteDetailses(qqMsg, quoteType));
        return mmQuote;
    }

    private MmQuote getMmQuotes(JsonNode qqMsg, QuoteType quoteType, MmQuote mmQuote) {
        Direction direction;
        Integer side;
        if (quoteType.equals(QuoteType.IBD)) {
            side = Integer.valueOf(getPaserResults(qqMsg, CD_PARSER_RESULT, CD_SIDE).asInt());
        } else {
            side = Integer.valueOf(getPaserResults(qqMsg, FM_PARSER_RESULT, FM_SIDE).asInt());
        }
        if (side != null && side == 1) {
            direction = Direction.OUT;
        } else {
            direction = Direction.IN;
        }

        String qqMsgSource = qqMsg.path(QQ_MSG_DATA).asText();  // 取得备注信息
        String qqId = qqMsg.path(QQ_ID).asText();  //获取发送报价的QQ号
        QQConverterUtil.convertInfoToMmQuote(userBaseService, quoteType, mmQuote, direction, qqMsgSource, qqId);
        return mmQuote;
    }


    //获取所有的QQ报价详情.
    private List<MmQuoteDetails> getMmQuoteDetailses(JsonNode qqMsg, QuoteType quoteType) {
        JsonNode json;
        List<MmQuoteDetails> results = new ArrayList<MmQuoteDetails>();
        if (quoteType.equals(QuoteType.IBD)) {
            json = getPaserResults(qqMsg, CD_PARSER_RESULT, CD_TENORS); //获取报价详情部分内容
        } else {
            json = getPaserResults(qqMsg, FM_PARSER_RESULT, FM_TENORS);
        }
        //设置报价详情的具体值将币种除人民币以外全部排除.
        for (int i = 0; i < json.size(); i++) {
            if (quoteType.equals(QuoteType.IBD)) {
                getQuoteDetalisByCurrencyType(json, i, CD_CURRENCY, qqMsg, quoteType, results);
            } else {
                getQuoteDetalisByCurrencyType(json, i, FM_CURRENCY, qqMsg, quoteType, results);
            }
        }
        return results;
    }

    private void getQuoteDetalisByCurrencyType(JsonNode json, int i, String resultType, JsonNode qqMsg, QuoteType quoteType, List<MmQuoteDetails> results) {
        JsonNode path = json.get(i).path(resultType);
        if (quoteType.equals(QuoteType.IBD)) {
            if (path == null || path.asInt() == 0 || path.asInt() == CNY) {
                setQuoteDetails(qqMsg, quoteType, json, i, results);
            }
        } else {
            setQuoteDetails(qqMsg, quoteType, json, i, results);  //理财不需要过滤币种
        }
    }

    private List<MmQuoteDetails> setQuoteDetails(JsonNode qqMsg, QuoteType quoteType, JsonNode json, int i, List<MmQuoteDetails> results) {
        MmQuoteDetails detail = null;
        if (quoteType.equals(QuoteType.IBD)) {
            if (containsPeriodForIBD(json.get(i))) {
                detail = new MmQuoteDetails();
                setDetailsParam(json.get(i), CD_RATE, detail);  //利率
                setDetailsParam(json.get(i), CD_AMOUNT, detail); //数量
                setPeroidForIBD(json.get(i), detail);  //期限
            }
        } else {
            if (containsPeroidForFM(json.get(i))) {
                detail = new MmQuoteDetails();
                setDetailsParam(json.get(i), FM_RATE, detail); //利率
                setDetailsParam(json.get(i), FM_AMOUNT, detail); //数量
                setupPeriodForFM(json.get(i), detail); //期限

            }
        }
        if (detail != null) {
            detail.setQqMessage(qqMsg.path(QQ_MSG_DATA).asText());
            detail.setActive(true);
            if (detail.getDaysLow() == null) {
                detail.setDaysLow(1);
            }
            if (detail.getDaysHigh() == null) {
                detail.setDaysHigh(1);
            }
            detail.setLastUpdateTime(Calendar.getInstance().getTime());
            results.add(detail);
        }
        return results;
    }

    private boolean containsPeroidForFM(JsonNode jsonNode) {
        return jsonNode.path(FM_DAYS_HIGH) != null || jsonNode.path(FM_DAYS_LOW) != null;
    }

    private boolean containsPeriodForIBD(JsonNode jsonNode) {
        return jsonNode.path(CD_DAYS) != null;
    }


    private void setupPeriodForFM(JsonNode qqMsg, MmQuoteDetails detail) {
        if (qqMsg.path(FM_DAYS_HIGH) != null && qqMsg.path(FM_DAYS_HIGH).asInt() != 0 || qqMsg.path(FM_DAYS_LOW) != null && qqMsg.path(FM_DAYS_LOW).asInt() != 0) {
            Integer daysHigh = null, daysLow = null;
            if (qqMsg.path(FM_DAYS_HIGH) != null && qqMsg.path(FM_DAYS_HIGH).asInt() != 0) {
                daysHigh = qqMsg.path(FM_DAYS_HIGH).asInt();
            }
            if (qqMsg.path(FM_DAYS_LOW) != null && qqMsg.path(FM_DAYS_LOW).asInt() != 0) {
                daysLow = qqMsg.path(FM_DAYS_LOW).asInt();

            }
            // QQ数据中，当不设置期限区间时，仅在daysLow有数值。对于QB的报价，实际上是1-daysLow的区间
            QQConverterUtil.setupDaysPeroidInModel(detail, daysHigh, daysLow);
        }
    }

    private void setDetailsParam(JsonNode qqMsg, String param, MmQuoteDetails details) {
        JsonNode path = qqMsg.path(param);
        if (path != null && path.asDouble() != 0) {
            if (param.equals(CD_RATE) || param.equals(FM_RATE)) {
                details.setPrice(BigDecimal.valueOf(path.asDouble()));
                details.setPriceLow(details.getPrice());
                return;
            }

            if (param.equals(CD_AMOUNT) || param.equals(FM_AMOUNT)) {
                details.setQuantity(BigDecimal.valueOf(path.asInt()));
                details.setQuantityLow(details.getQuantity());
                return;
            }
        }

    }

    private void setPeroidForIBD(JsonNode qqMsg, MmQuoteDetails details) {
        JsonNode path = qqMsg.path(CD_DAYS);
        Integer daysHigh = null;
        Integer daysLow = null;
        if (path != null && path.asInt() != 0) {
            daysHigh = path.asInt();
            daysLow = daysHigh;
        }
        QQConverterUtil.setupDaysPeroidInModel(details, daysHigh, daysLow);
    }

    //这个方法提供获取到QQ cd_results,fm_results,该层的所有json对象.
    public JsonNode getPaserResults(JsonNode qqMsg, String parserResult, String value) {
        return qqMsg.path(QQ_PARSEDMSG).path(parserResult).get(0).path(value);
    }
}
