package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteDetailsQueryParameters;
import com.sumscope.optimus.moneymarket.model.dto.MobileMatrixParameterDto;

/**
 * 用于手机矩阵计算相关的模型转换器
 */
public class MobileMatrixConverter {

	/**
	 * 为了复用现有查询服务，需要将矩阵计算的参数Dto转换为查询服务搜索模型。
	 */
	public MmQuoteDetailsQueryParameters convertDtoToModel(MobileMatrixParameterDto paramDto) {
		return null;
	}

}
