package com.sumscope.optimus.moneymarket.facade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sumscope.optimus.moneymarket.model.dto.InstitutionContactsParameterDto;
import com.sumscope.optimus.moneymarket.model.dto.MobileMatrixParameterDto;
import com.sumscope.optimus.moneymarket.model.dto.MobileQueryQuoteMainParameterDto;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 向手机理财应用提供服务的Facade
 */
public interface MobileFunctionFacade {

	/**
	 * 根据输入的参数获取矩阵计算结果。
	 * 输入参数类型为MobileMatrixParameterDto
	 * 返回前端的参数类型为List<MobileMatrixResponseDto>
	 */
	void getMobileMatrix(HttpServletRequest request, HttpServletResponse response, @RequestBody MobileMatrixParameterDto matrixParameter);

	/**
	 * 根据机构及报价类型获取该精品机构的对应联系人
	 * 参数中的机构应为精品机构，否则将获得一个空的列表
	 * 返回：List<QuoteUserInfoDto>
	 */
	void getInstitutionContacts(HttpServletRequest request, HttpServletResponse response,@RequestBody InstitutionContactsParameterDto parameterDto);

	/**
	 * 查询报价单。可能根据期限的价格值进行排序。若排序期限设置为0则按报价日期降序排序。
	 * 返回类型为List<MmQuoteDto>
	 */
	void queryQuoteMain(HttpServletRequest request, HttpServletResponse response,@RequestBody MobileQueryQuoteMainParameterDto parameter);

	/**
	 * 返回产品特征
	 * @param request
	 * @param response
     */
	void getDictTag(HttpServletRequest request, HttpServletResponse response);

}
