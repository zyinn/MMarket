package com.sumscope.optimus.moneymarket.facade;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.util.HTTPResponseUtil;
import com.sumscope.optimus.moneymarket.commons.util.Utils;
import com.sumscope.optimus.moneymarket.model.dto.MockRequestDto;
import com.sumscope.optimus.moneymarket.service.MockDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by kedong.li on 2016/5/31.
 * 报价单性能测试
 */
@RestController
@RequestMapping(value = "/mock", produces = MediaType.APPLICATION_JSON_VALUE)
@PropertySource("classpath:application.yml")
public class MockPublishQuoteFacadeImpl implements MockPublishQuoteFacade{

	@Autowired
	private MockDataService mockDataService;

	@Value(value = "${application.enableMockData}")
	private String enabled;

	@SuppressWarnings("static-access")
	@Override
	@RequestMapping(value="/publishtest",method=RequestMethod.POST)
	public void mockQuote(HttpServletRequest request, HttpServletResponse response,@RequestBody MockRequestDto mockRequestDto) {
		Utils.addHeaderOrigin(request,response);
		String info;
		if(enabled!= null && enabled.toUpperCase().equals("TRUE")){
			info = mockDataNow(mockRequestDto);
		}else{
			info="不允许生成虚拟数据！";
			LogManager.error("不合法的使用虚拟数据生成方法！");
		}
		HTTPResponseUtil.returnMessageThroughHttpServletResponse(info,response);
	}

	private String mockDataNow(@RequestBody MockRequestDto mockRequestDto) {
		long total = 0;
		for (int i = 0; i < mockRequestDto.getRound(); i++) {
			  total += mockDataService.startMock(mockRequestDto);
			try {
				Thread thread = new Thread();
				thread.sleep(mockRequestDto.getRoundTime());
			} catch (InterruptedException e) {
				LogManager.error(e.toString());
			}
		}
		return "{\"result\":\"Total created: " + Long.valueOf(total).toString() + (" Quotes.\"}");
	}

	//跨域OPTIONS请求
	@RequestMapping(value = "/publishtest",method=RequestMethod.OPTIONS)
	public void mockQuoteOptions(HttpServletRequest request, HttpServletResponse response) {
		Utils.getMethodOptions(request,response);
	}

}
