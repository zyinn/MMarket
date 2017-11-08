package com.sumscope.optimus.moneymarket.facade;

import com.sumscope.optimus.moneymarket.model.dto.*;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by qikai.yu on 2016/4/21.
 * 矩阵及查询Facade
 */
public interface MatrixAndQuoteQueryFacade {

    /**
     * 支持界面右侧机构，用户查询窗口功能，按输入字符（可能是拼音）查询相应的机构及QB，QQ用户
     */
    void searchKeyword(HttpServletRequest request, HttpServletResponse response, @RequestBody SearchKeywordRequestDto requestVO);


    /**
     * 查询主机构下的联盟机构报价信息
     */
    void queryAllianceInstitutionQuote(HttpServletRequest request, HttpServletResponse response,@RequestBody String userId);

    /**
     * 查询报价
     *
     * @param request
     * @return
     */
    void queryMmQuotes(HttpServletRequest request, HttpServletResponse response, @RequestBody MmQuoteQueryParameterDto parameterDto);

    /**
     * 查询收益率历史走势信息，根据参数返回对应的历史信息
     */
    void queryPriceTrend(HttpServletRequest request, HttpServletResponse response, @RequestBody PriceTrendQueryParameterDto parameterDto);

    /**
     * 查询当前收益率矩阵，根据方向和类型进行计算。
     */
    void queryPriceMatrix(HttpServletRequest request, HttpServletResponse response, @RequestBody PriceMatrixQueryParameterDto parameterDto);
}
