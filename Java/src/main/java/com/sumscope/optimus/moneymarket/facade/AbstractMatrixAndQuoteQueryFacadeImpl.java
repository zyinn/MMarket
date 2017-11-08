package com.sumscope.optimus.moneymarket.facade;

import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.commons.util.JsonUtil;
import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.commons.util.QuoteDateUtils;
import com.sumscope.optimus.moneymarket.commons.util.Utils;
import com.sumscope.optimus.moneymarket.facade.converter.*;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import com.sumscope.optimus.moneymarket.model.dto.*;
import com.sumscope.optimus.moneymarket.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by fan.bai on 2016/5/13.
 * 同业理财与线下资金的实际处理一致，仅仅是报价类型字段不同。但是为了逻辑清晰，开发给前端的Rest分为两个部分。
 * 该类进行实际业务的处理，其子类进行url的定义以及定义特殊类型
 */
public abstract class AbstractMatrixAndQuoteQueryFacadeImpl extends AbstractFacadeImpl implements MatrixAndQuoteQueryFacade {
    //[OPM-427] 检索时不显示内部机构，根据机构ID限制
    private final static String[] internalCompanyIds = {"8a8a82893d68ce11013d7b60d62e2f91", "ff8081814816ef6d01490c5c24bc05b5", "ff80818144fdf16701452b46e6627725", "10"};

    @Autowired
    private QbBaseService qbBaseService;

    @Autowired
    private MmQuoteQueryService mmQuoteQueryService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private MmQuoteDtoConverter mmQuoteDtoConverter;

    @Autowired
    private ContactUserInfoDtoConverter contactUserInfoDtoConverter;

    @Autowired
    private MmQuoteWithContactDtoConverter mmQuoteWithContactDtoConverter;

    @Autowired
    private MmQuoteQueryParameterDtoConverter mmQuoteQueryParameterDtoConverter;

    @Autowired
    private MmQuoteQueryListResultDtoConverter mmQuoteQueryListResultDtoConverter;

    @Autowired
    private MatrixCalcuationAndQueryService matrixCalcuationAndQueryService;

    @Autowired
    private PriceMatrixDtoConverter priceMatrixDtoConverter;

    @Autowired
    private PriceTrendQueryParameterDtoConverter priceTrendQueryParameterDtoConverter;

    @Autowired
    private PriceTrendDtoConverter priceTrendDtoConverter;

    @Autowired
    private MmAllianceInstitutionService mmAllianceInstitutionService;

    @Override
    @RequestMapping(value = "/queryAllianceQuote", method = RequestMethod.POST)
    public void queryAllianceInstitutionQuote(HttpServletRequest request, HttpServletResponse response, @RequestBody String institutionIdS) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            //获取联盟机构institutionId;
            int returnCode = 0; // return code == 0表明该机构不是联盟机构， 1表示是联盟机构
            String institutionId = (String) JsonUtil.readValue(institutionIdS, HashMap.class).get("institutionId");
            List<Institution> allianceInstitutions = mmAllianceInstitutionService.getAllianceInstitutionsByGivenInstitutionId(institutionId);
            List<MmQuoteWithContactDto> MmQuoteWithContactDto = new ArrayList<>();
            List<QuoteUserInfoDto> primaryInstitutionContactInfos = new ArrayList<>();
            if (allianceInstitutions != null) {
                List<String> institutionIds = getInstitutionIdList(allianceInstitutions);
                List<MmQuote> mmQuotes = mmQuoteQueryService.queryValidQuotesByInstitutions(institutionIds);
                List<MmQuoteDto> mmQuoteDtos = mmQuoteDtoConverter.convertMmQuoteListToMmQuoteDtoList(mmQuotes, false);
                Institution institution = mmAllianceInstitutionService.getPrimaryInstitutionId(institutionId);
                if (institution != null && institution.getId() != null) {
                    Set<ContactUser> contactSet = institutionService.retrieveAllPrimeContacts().get(institution.getId());
                    primaryInstitutionContactInfos = contactUserInfoDtoConverter.convertToDto(contactSet);
                }
                MmQuoteWithContactDto = mmQuoteWithContactDtoConverter.convertMMQuoteWithContactsAndPrimeInstitution(mmQuoteDtos, institution);
                returnCode = 1;
            }
            Map<String, Object> ret = new HashMap<>();
            ret.put("MmQuoteWithContactDto", MmQuoteWithContactDto);
            ret.put("contactsList", primaryInstitutionContactInfos);
            ret.put("return_code", returnCode);
            ret.put("return_message", "Success");
            ret.put("result_count", MmQuoteWithContactDto.size());
            return ret;
        });
    }

    private List<String> getInstitutionIdList(List<Institution> allianceInstitutions) {
        List<String> result = new ArrayList<>();
        for (Institution ins : allianceInstitutions) {
            result.add(ins.getId());
        }
        return result;
    }


    @Override
    @RequestMapping(value = "/search_preview", method = RequestMethod.POST)
    public void searchKeyword(HttpServletRequest request, HttpServletResponse response, @RequestBody SearchKeywordRequestDto requestVO) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            List<Institution> companyList = qbBaseService.queryByKeyword(requestVO.getKeyword());
            List<List<User>> userList = qbBaseService.queryAllUserWithPinYin(requestVO.getKeyword());
            return processSearchKeywordResult(companyList, userList);
        });
    }


    /**
     * 处理关键词搜索结果
     */
    private Object processSearchKeywordResult(List<Institution> companyList, List<List<User>> userList) {
        List<User> qbUserList = userList.size() > 0 ? userList.get(0) : new ArrayList<>();
        List<User> qqUserList = userList.size() > 0 ? userList.get(1) : new ArrayList<>();

        List<SearchKeywordResponseDto.Result> companyDtoList = new ArrayList<>(companyList.size());
        List<SearchKeywordResponseDto.Result> qbUserDtoList = new ArrayList<>(qbUserList.size());
        List<SearchKeywordResponseDto.Result> qqUserDtoList = new ArrayList<>(qqUserList.size());

        SearchKeywordResponseDto companyDto = new SearchKeywordResponseDto();
        companyDto.setType("机构");
        companyDto.setList(companyDtoList);
        for (Institution company : companyList) {
            if (!isInternalCompany(company.getId())) {
                SearchKeywordResponseDto.Result resultDto = new SearchKeywordResponseDto.Result();
                resultDto.setId(company.getId());
                resultDto.setName(company.getName());
                companyDtoList.add(resultDto);
            }
        }

        SearchKeywordResponseDto qbDto = new SearchKeywordResponseDto();
        qbDto.setType("QB用户");
        qbDto.setList(qbUserDtoList);
        addResponseDtoToUserDtoList(qbUserList, qbUserDtoList);

        SearchKeywordResponseDto qqDto = new SearchKeywordResponseDto();
        qqDto.setType("QQ用户");
        qqDto.setList(qqUserDtoList);
        addResponseDtoToUserDtoList(qqUserList, qqUserDtoList);
        return Arrays.asList(companyDto, qbDto, qqDto);
    }

    private void addResponseDtoToUserDtoList(List<User> UserList, List<SearchKeywordResponseDto.Result> resultDtoList) {
        for (User user : UserList) {
            SearchKeywordResponseDto.Result resultDto = new SearchKeywordResponseDto.Result();
            resultDto.setId(user.getUserId());
            resultDto.setName(user.getDisplayName());
            resultDtoList.add(resultDto);
        }
    }

    private boolean isInternalCompany(String id) {
        for (String internalCompanyId : internalCompanyIds) {
            if (id.equals(internalCompanyId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @RequestMapping(value = "/queryMmQuotes", method = RequestMethod.POST)
    public void queryMmQuotes(HttpServletRequest request, HttpServletResponse response, @RequestBody MmQuoteQueryParameterDto parameterDto) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            MmQuoteMainQueryParameters mmQuoteMainQueryParameters = mmQuoteQueryParameterDtoConverter.convertToMainQueryModel(parameterDto);
            mmQuoteMainQueryParameters.setOnlyValidQuotes(true); //前端仅查询有效报价
            long step0Time = System.currentTimeMillis();
            //对于拥有联盟或代报价权限的用户来说，查询我的报价等于查询操作人是自己的报价
            if(parameterDto.isPrivateQuotesOnly() ){
                if(isAlliaceOrBrokerUser(getLoginUser(request).getUserId())){
                    mmQuoteMainQueryParameters.setQuoteOperatorId(parameterDto.getQuoteUserId());
                    mmQuoteMainQueryParameters.setQuoteUserId(null);
                }
            }
            List<MmQuote> mmQuotes = mmQuoteQueryService.queryMmQuoteMain(mmQuoteMainQueryParameters, true, true);
            long step1Time = System.currentTimeMillis();
            LogManager.debug("查询报价单耗时：" + (step1Time - step0Time) + "毫秒");
            List<MmQuoteQueryListResultDto> mmQuoteQueryListResultDtos = mmQuoteQueryListResultDtoConverter.convertModelToDto(mmQuotes);
            if(parameterDto.getOrderByPeriod() != null){
                List<MmQuoteQueryListResultDto> mmQuoteQueryListResultDtos1 = mmQuoteQueryListResultDtoConverter.MmQuoteMainQueryParameters(parameterDto, mmQuoteQueryListResultDtos);
                return mmQuoteQueryListResultDtos1;
            }
            long step2Time = System.currentTimeMillis();
            LogManager.debug("组装报价单耗时：" + (step2Time - step1Time) + "毫秒");
            return mmQuoteQueryListResultDtos;
        });
    }

    private boolean isAlliaceOrBrokerUser(String userId) {
        return mmAllianceInstitutionService.isAuthorizedForAllianceQuote(userId) || mmAllianceInstitutionService.isAuthorizedForBrokerQuote(userId);
    }


    @Override
    @RequestMapping(value = "/queryPriceTrend", method = RequestMethod.POST)
    public void queryPriceTrend(HttpServletRequest request, HttpServletResponse response, @RequestBody PriceTrendQueryParameterDto parameterDto) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            PriceTrendsQueryParameters parameters = priceTrendQueryParameterDtoConverter.convertToModel(parameterDto);
            List<PriceTrend> priceTrends = matrixCalcuationAndQueryService.queryPriceTrends(parameters);
            List<PriceTrend> priceTrend = priceTrendQueryParameterDtoConverter.convertToPriceTrendsForList(parameterDto, priceTrends);
            return priceTrendDtoConverter.convertToDto(priceTrend);
        });
    }

    @Override
    @RequestMapping(value = "/queryPriceMatrix", method = RequestMethod.POST)
    public void queryPriceMatrix(HttpServletRequest request, HttpServletResponse response, @RequestBody PriceMatrixQueryParameterDto parameterDto) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            List<PriceTrend> priceTrends = matrixCalcuationAndQueryService.calculateMatrixCells(parameterDto.getDirection(),
                    parameterDto.getQuoteType(), QuoteDateUtils.getMatrixCalculationTimeOfDate(new Date()));
            List<PriceTrend> priceTrend = priceMatrixDtoConverter.convertShiborResultTableToPriceTrends(priceTrends, parameterDto.getDirection(),
                    parameterDto.getQuoteType(), QuoteDateUtils.getMatrixCalculationTimeOfDate(new Date()));
            return priceMatrixDtoConverter.convertModelsToDtos(priceTrend);
        });
    }

    //跨域OPTIONS请求
    @RequestMapping(value = "/search_preview", method = RequestMethod.OPTIONS)
    public void searchKeywordOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/quote_list", method = RequestMethod.OPTIONS)
    public void searchQuoteOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/queryAllianceQuote", method = RequestMethod.OPTIONS)
    public void queryAllianceQuoteOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/queryMmQuotes", method = RequestMethod.OPTIONS)
    public void queryMmQuotes(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/queryPriceTrend", method = RequestMethod.OPTIONS)
    public void queryPriceTrend(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/queryPriceMatrix", method = RequestMethod.OPTIONS)
    public void queryPriceMatrix(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

}
