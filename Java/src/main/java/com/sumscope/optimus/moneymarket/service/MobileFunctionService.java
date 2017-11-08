package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.commons.enums.ContactQuoteAttribute;
import com.sumscope.optimus.moneymarket.model.dbmodel.ContactUser;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteMainQueryParameters;
import com.sumscope.optimus.moneymarket.model.dbmodel.MobileMatrixResult;
import com.sumscope.optimus.moneymarket.model.dto.MobileMatrixParameterDto;
import com.sumscope.optimus.moneymarket.model.dto.MobileQueryQuoteMainParameterDto;

import java.util.List;


/**
 * 向手机理财应用提供服务的Service
 */
public interface MobileFunctionService {

    /**
     * Mobile大厅矩阵数据
     *
     * @param matrixParameter
     * @return
     */
    List<MobileMatrixResult> getMobileMatrix(MobileMatrixParameterDto matrixParameter);

    /**
     * 根据机构id等获取精品机构联系人信息
     *
     * @return
     */
    List<ContactUser> getPrimeInstitutionContarct(String instituionId, ContactQuoteAttribute quoteAttribute);

    /**
     * 获取报价信息记录和详细记录
     *
     * @return
     */
    List<MmQuote> retrieveMmQuoteMain(MmQuoteMainQueryParameters parameter);
}
