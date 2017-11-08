package com.sumscope.optimus.moneymarket.facade;

/**
 * Created by kedong.li on 2016/5/31.
 * 报价单性能测试
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sumscope.optimus.moneymarket.model.dto.MockRequestDto;

public interface MockPublishQuoteFacade {

	void mockQuote(HttpServletRequest request, HttpServletResponse response,MockRequestDto mockRequestDto);
	
}
