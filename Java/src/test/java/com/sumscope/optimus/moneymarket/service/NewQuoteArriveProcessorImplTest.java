package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.util.JsonUtil;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteSource;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteDetails;
import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.wsandlocalmessage.NewQuoteArriveProcessorImpl;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/9.
 */
public class NewQuoteArriveProcessorImplTest extends TestCase {

    public void testConvertToResponseVO() throws Exception {
        NewQuoteArriveProcessorImpl impl = new NewQuoteArriveProcessorImpl();

        MmQuote quote = new MmQuote();
        quote.setId("1234abcd");
        quote.setDirection(Direction.IN);
        quote.setMemo("test");
        quote.setQuoteType(QuoteType.GTF);
        quote.setInstitutionId("-1");
        quote.setSource(QuoteSource.QB);
        quote.setQuoteUserId("qbuserId?");
        List<MmQuoteDetails> l = new ArrayList<>();
        MmQuoteDetails vo = new MmQuoteDetails();
        vo.setDaysLow(1);
        vo.setDaysHigh(10);
        vo.setPriceLow(new BigDecimal("3.4"));
        vo.setPrice(new BigDecimal("4.4"));
        vo.setQuantityLow(new BigDecimal("8000"));
        vo.setQuantity(new BigDecimal("10000"));
        l.add(vo);
        quote.setMmQuoteDetails(l);

        System.out.println(JsonUtil.writeValueAsString(quote));
//        String str = "{\"quoteType\":\"GTF\",\"direction\":\"IN\",\"quoteUserId\":\"qbuserId?\",\"quoteDetailsVOs\":[{\"limitType\":\"T1Y\",\"price\":3.4,\"quantity\":10000}],\"memo\":\"test\",\"source\":\"QB\"}";

//        String s = "{\"id\":\"3615b92b7953412a9b62a55b3f257cde\",\"quoteType\":\"UR2\",\"direction\":\"IN\",\"quoteUserId\":\"004687495e234a7682f3bd8c4ff84426\",\"memo\":\"123\",\"source\":\"QB\",\"createTime\":1462928749397,\"mmQuoteDetails\":[{\"id\":\"724ddac056434ff19bad651606d99912\",\"quoteId\":\"3615b92b7953412a9b62a55b3f257cde\",\"price\":88,\"priceLow\":88,\"quantity\":88,\"quantityLow\":88,\"lastUpdateTime\":1462928749397,\"daysLow\":0,\"daysHigh\":7,\"active\":1}]}";

//        MmQuote dbmodel = JsonUtil.readValue(s,MmQuote.class);
//        System.out.println(dbmodel.toString());

//        MmQuoteDto newQuote = JsonUtil.readValue(str, MmQuoteDto.class);


//        List<QueryQuoteListResponseDto> list = impl.convertToResponseVO(quote);
    }
}