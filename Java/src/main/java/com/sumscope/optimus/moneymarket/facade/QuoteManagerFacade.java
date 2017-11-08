package com.sumscope.optimus.moneymarket.facade;

import com.sumscope.optimus.moneymarket.model.dbmodel.ExcelFile;
import com.sumscope.optimus.moneymarket.model.dto.QuoteRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * 报价管理 *
 * Created by qikai.yu on 2016/4/21.
 */
public interface QuoteManagerFacade {
    /**
     * 获取用户前一次报价数据;
     * @param request
     * @param response
     */
    void getMyValidQuotes(HttpServletRequest request, HttpServletResponse response);

    /**
     * 发布用户的报价数据
     * @param request
     * @param response
     * 要在QuoteRequestDto添加一个机构ID字段以便于后台可以接受到值,并且在MmQuote添加机构字段用于数据库存值,
     * 可在mapMMQuoteVoToDto时对报价单的联盟机构进行验证,对于报价单存在与否查询增加一个机构ID字段
     * QueryQuoteListResponseDto查询返回的报价单中,判断该报价单是否本机构ID,否则就把备注设置为报价的机构名  
     * 
     */
    void publishQuote(HttpServletRequest request, HttpServletResponse response,QuoteRequestDto quoteRequestDto);

    /**
     * 获取当前用户的可用联盟机构或者代报价机构列表，该列表使用List<InstitutionDto>作为返回值。用户所属机构也在列表中
     * 若当前用户没有联盟机构，列表为空。前端页面显示标准报价单界面。否则显示联盟机构报价单界面。
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    void getMyAvailableInstitutions(HttpServletRequest request, HttpServletResponse response);

    /**
     * 发布联盟报价数据，报价单Dto的机构字段必须设置
     * @param request
     * @param response
     */
    void publishAllianceQuotes(HttpServletRequest request, HttpServletResponse response, QuoteRequestDto quoteRequestDto);

    /**
     * 根据用户所选excel解析报价单，并返回前端解析结果
     * @param request
     * @param response
     * @param fileContent excel文件内容
     */
    void parserQuotesInExcelFile(HttpServletRequest request, HttpServletResponse response, ExcelFile fileContent);

    /**
     * excel导入保存
     * @param request
     * @param response
     * @param quoteRequestDto
     */
    void excelSave(HttpServletRequest request, HttpServletResponse response, QuoteRequestDto quoteRequestDto);

}
