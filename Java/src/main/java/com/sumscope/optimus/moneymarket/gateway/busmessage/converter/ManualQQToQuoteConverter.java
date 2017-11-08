package com.sumscope.optimus.moneymarket.gateway.busmessage.converter;

import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteDetails;
import com.sumscope.optimus.moneymarket.service.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by fan.bai on 2016/8/3.
 * 用于旧版本QQ数据的接收转换。
 */
@Component
public class ManualQQToQuoteConverter {
    private static final String QQ_MSG_DATA = "msgdata";

    private static final String VOLUME_LOW = "Volume_low";

    private static final String VOLUME_HIGH = "Volume_high";

    private static final String DAYS_LOW = "Days_Low";

    private static final String DAYS_HIGH = "Days_High";

    private static final String RATE_LOW = "Rate_Low";

    private static final String RATE_HIGH = "Rate_High";

    private static final String GUARANT_TYPE = "IsGuaranteed";

    private static final String SIDE = "Side";
    private static final String QQ_ID = "senderuin";


    @Autowired
    private UserBaseService userBaseService;

    /**
     * 转换QQ 线下资金信息至报价单Dto
     *
     * @param qqMsg QQ报价信息
     * @return MmQuoteDto
     */
    public MmQuote convertManualQQOfflineMsgToQuoteDto(Map<String, Object> qqMsg) {
        QuoteType quoteType = QuoteType.IBD;
        return convertQQToQuote(qqMsg, quoteType);
    }

    /**
     * 转换QQ 同业理财信息至报价单Dto
     *
     * @param qqMsg QQ报价信息
     * @return MmQuoteDto
     */
    public MmQuote convertManualQQInterbankMsgToQuote(Map<String, Object> qqMsg) {
        Integer guarantType = (Integer) qqMsg.get(GUARANT_TYPE);
        QuoteType quoteType;
        if (guarantType != null && guarantType == 1) {
            quoteType = QuoteType.GTF;
        } else {
            quoteType = QuoteType.UR2;

        }
        return convertQQToQuote(qqMsg, quoteType);
    }

    private MmQuote convertQQToQuote(Map<String, Object> qqMsg, QuoteType quoteType) {
        //修改：线下资金和理财的数据相同，1为OUT、0为IN
        Integer side = (Integer) qqMsg.get(SIDE);
        Direction direction;
        if (side != null && side == 1) {
            direction = Direction.OUT;
        } else {
            direction = Direction.IN;
        }
        String qqMsgSource = (String) qqMsg.get(QQ_MSG_DATA);
        String qqId = (String) qqMsg.get(QQ_ID);


        MmQuote mmQuote = new MmQuote();
        QQConverterUtil.convertInfoToMmQuote(userBaseService, quoteType, mmQuote, direction, qqMsgSource, qqId);

        mmQuote.setMmQuoteDetails(convertToMmQuoteDetails(qqMsg));
        mmQuote.setQuoteOperatorId(qqId);
        return mmQuote;
    }

    private List<MmQuoteDetails> convertToMmQuoteDetails(Map<String, Object> qqMsg) {
        List<MmQuoteDetails> results = new ArrayList<>();
        if (qqMsg.get(DAYS_HIGH) != null || qqMsg.get(DAYS_LOW) != null) {
            MmQuoteDetails detail = new MmQuoteDetails();

            setupPrice(qqMsg, detail);

            setupVolume(qqMsg, detail);

            setupPeriod(qqMsg, detail);

            detail.setQqMessage((String) qqMsg.get(QQ_MSG_DATA));
            detail.setActive(true);
            detail.setLastUpdateTime(Calendar.getInstance().getTime());
            results.add(detail);
        }
        return results;
    }

    private void setupPeriod(Map<String, Object> qqMsg, MmQuoteDetails detail) {
        Integer daysHigh = (Integer) qqMsg.get(DAYS_HIGH);
        Integer daysLow = (Integer) qqMsg.get(DAYS_LOW);
        // QQ数据中，当不设置期限区间时，仅在daysLow有数值。对于QB的报价，实际上是1-daysLow的区间
        QQConverterUtil.setupDaysPeroidInModel(detail, daysHigh, daysLow);
    }


    private void setupVolume(Map<String, Object> qqMsg, MmQuoteDetails detail) {
        Integer volumeHigh = (Integer) qqMsg.get(VOLUME_HIGH);
        Integer volumeLow = (Integer) qqMsg.get(VOLUME_LOW);
        if (volumeHigh == null && volumeLow == null) {
            detail.setQuantity(null);
            detail.setQuantityLow(null);
        } else if (volumeHigh == null) {
            detail.setQuantityLow(BigDecimal.valueOf(volumeLow));
            detail.setQuantity(detail.getQuantityLow());
        } else if (volumeLow == null) {
            detail.setQuantityLow(BigDecimal.valueOf(0));
            detail.setQuantity(BigDecimal.valueOf(volumeHigh));
        } else {
            if (volumeLow > volumeHigh) {
                //若解析不正规造成高低值颠倒
                Integer tmp = volumeHigh;
                volumeHigh = volumeLow;
                volumeLow = tmp;
            }
            detail.setQuantity(BigDecimal.valueOf(volumeHigh));
            detail.setQuantityLow(BigDecimal.valueOf(volumeLow));
        }
    }

    private void setupPrice(Map<String, Object> qqMsg, MmQuoteDetails detail) {
        Double rateHigh = (Double) qqMsg.get(RATE_HIGH);
        Double rateLow = (Double) qqMsg.get(RATE_LOW);

        if (rateHigh == null && rateLow == null) {
            detail.setPrice(null);
            detail.setPriceLow(null);
        } else if (rateHigh == null) {
            detail.setPriceLow(BigDecimal.valueOf(rateLow));
            detail.setPrice(detail.getPriceLow());
        } else if (rateLow == null) {
            detail.setPriceLow(BigDecimal.valueOf(0));
            detail.setPrice(BigDecimal.valueOf(rateHigh));
        } else {
            if (rateLow > rateHigh) {
                //若解析不正规造成高低值颠倒
                Double tmp = rateHigh;
                rateHigh = rateLow;
                rateLow = tmp;
            }
            detail.setPrice(BigDecimal.valueOf(rateHigh));
            detail.setPriceLow(BigDecimal.valueOf(rateLow));
        }

    }
}
