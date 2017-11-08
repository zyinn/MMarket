package com.sumscope.optimus.moneymarket.facade;

import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.commons.exceptions.ValidationExceptionDetails;
import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.commons.util.Utils;
import com.sumscope.optimus.moneymarket.facade.converter.InstitutionConverter;
import com.sumscope.optimus.moneymarket.facade.converter.MmQuoteDtoConverter;
import com.sumscope.optimus.moneymarket.facade.converter.MmQuoteExcelConverter;
import com.sumscope.optimus.moneymarket.facade.converter.QuoteMethodsConverter;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import com.sumscope.optimus.moneymarket.model.dto.InstitutionDto;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteDto;
import com.sumscope.optimus.moneymarket.model.dto.QuoteRequestDto;
import com.sumscope.optimus.moneymarket.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by qikai.yu on 2016/4/21.
 * 报价单管理facade的抽象实例。该类进行报价单管理的实际操作
 */
@RestController
@RequestMapping(value = "/quote", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuoteManagerFacadeImpl extends AbstractFacadeImpl implements QuoteManagerFacade {

    @Autowired
    private MmAllianceInstitutionService mmAllianceInstitutionService;

    @Autowired
    private MmQuoteExcelConverter mmQuoteExcelConverter;

    @Autowired
    private InstitutionConverter institutionConverter;

    @Autowired
    private UserBaseService userBaseService;

    @Autowired
    private BusinessMmQuoteManagementService mmQuoteManagementService;

    @Autowired
    private MmQuoteDtoConverter mmQuoteDtoConverter;

    @Autowired
    private MmQuoteQueryService mmQuoteQueryService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private QuoteMethodsConverter quoteMethodsConverter;
    /**
     * 获取当前用户的有效报价
     */
    @Override
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public void getMyValidQuotes(HttpServletRequest request, HttpServletResponse response) {
        performWithExceptionCatch(response, (ExceptionCatchableProcessWithResult) () -> {
            Utils.addHeaderOrigin(request, response);
            Map<String, Object> map = new HashMap<>();
            MethodType methodType = getMyQuoteMethods(getLoginUser(request).getUserId()).get(0);
            List<MmQuote> myValidQuotes = mmQuoteQueryService.queryMyValidQuotes(getLoginUser(request).getUserId(),methodType);
            List<MmQuoteDto> mmQuoteDtoList = mmQuoteDtoConverter.convertMmQuoteListToMmQuoteDtoList(myValidQuotes,false);
            map.put("offer_data", mmQuoteDtoList);
            return map;
        });
    }

    @Override
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void publishQuote(HttpServletRequest request, HttpServletResponse response, @RequestBody QuoteRequestDto quoteRequestDto) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> processSaveMmQuotes(request, quoteRequestDto,false));
    }

    public List<MethodType> getMyQuoteMethods(String userId) {
        boolean isValidForAllianceQuote = mmAllianceInstitutionService.isAuthorizedForAllianceQuote(userId); //联盟报价权限
        boolean isValidForBrokerQuote = mmAllianceInstitutionService.isAuthorizedForBrokerQuote(userId);  //中介报价权限
        return quoteMethodsConverter.convertQuoteMethods(isValidForAllianceQuote, isValidForBrokerQuote);
    }

    /**
     * 获取联盟机构任务
     */
    @Override
    @RequestMapping(value = "/getAlliance", method = RequestMethod.POST)
    public void getMyAvailableInstitutions(HttpServletRequest request, HttpServletResponse response) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, (ExceptionCatchableProcessWithResult) () -> {
            String userId = getLoginUser(request).getUserId();
            User loginUser = userBaseService.retreiveAllUsersGroupByUserID().get(userId);
            List<Institution> institutions = mmAllianceInstitutionService.getAllianceInstitutionsByGivenInstitutionId(loginUser.getCompanyId());
            Map<String, List<User>> stringSetMap = institutionService.retrieveAllUser();
            List<InstitutionDto> institutionDtos = institutionConverter.convertInstitutionDtos(institutions,userId);
            institutionConverter.convertInstitutionUsers(institutionDtos,stringSetMap);
            Map<String, Object> map = new HashMap<>();
            map.put("alliance", institutionDtos);
            return map;
        });

    }

    /**
     * 联盟机构报价保存
     */
    @Override
    @RequestMapping(value = "/allianceSave", method = RequestMethod.POST)
    public void publishAllianceQuotes(HttpServletRequest request, HttpServletResponse response, @RequestBody QuoteRequestDto quoteRequestDto) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> processSaveMmQuotes(request, quoteRequestDto,true));
    }


    @Override
    @RequestMapping(value = "/excelSave", method = RequestMethod.POST)
    public void excelSave(HttpServletRequest request, HttpServletResponse response, @RequestBody QuoteRequestDto quoteRequestDto) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            List<MmQuoteDto> mmQuoteDtoList = new ArrayList<>();
            mmQuoteDtoList.addAll(quoteRequestDto.getOffer_data());
            List<MmQuote> mmQuotes = mmQuoteDtoConverter.convertMmQuoteDtoToModelForNormalSave(mmQuoteDtoList, getLoginUser(request));
            mmQuoteManagementService.setupMmQuotesInTransaction(mmQuotes, BusinessMmQuoteManagementService.Source.EXCEL);
            return true;
        });
    }

    private boolean processSaveMmQuotes(HttpServletRequest request, QuoteRequestDto quoteRequestDto, boolean allienceSave) {
        User currentUser = getLoginUser(request);
        List<MmQuoteDto> mmQuoteDtoList = quoteRequestDto.getOffer_data();
        if (mmQuoteDtoList != null) {
            List<MmQuote> mmQuotes;
            if(allienceSave){
                mmQuotes = mmQuoteDtoConverter.convertMmQuoteDtoToModelForAllienceSave(mmQuoteDtoList, currentUser);
            }else{
                mmQuotes = mmQuoteDtoConverter.convertMmQuoteDtoToModelForNormalSave(mmQuoteDtoList, currentUser);
            }
            mmQuoteManagementService.setupMmQuotesInTransaction(mmQuotes, BusinessMmQuoteManagementService.Source.QB_CLIENT);

            // 转换 保存机构-用户关系到User_preference表
            processSetupInstitutionUserMapper(currentUser, mmQuoteDtoList);
        }
        return true;
    }

    private void processSetupInstitutionUserMapper(User currentUser, List<MmQuoteDto> mmQuoteDtoList) {
        if(mmQuoteDtoList!=null && mmQuoteDtoList.size()>0 && (mmQuoteDtoList.get(0).getMethodType()!=null) && !"".equals(mmQuoteDtoList.get(0).getMethodType())){
               List<UserPreference> userPreferences = mmQuoteDtoConverter.convertMmQuoteDtoToInstitutionUserMapperUpdate(mmQuoteDtoList,currentUser);
               mmQuoteManagementService.updateInstitutionUserMapping(userPreferences);//修改已存在的机构人员关系
               List<UserPreference> list=mmQuoteDtoConverter.convertMmQuoteDtoToInstitutionUserMappingSave(mmQuoteDtoList,currentUser);
               if(list!=null&&list.size()>0){
                   mmQuoteManagementService.setupInstitutionUserMappingTransaction(list);//保存偏好表中没有对应的机构-用户关系，即新增 机构-用户关系
               }
        }
    }

    /**
     * 解析excel表格
     */
    @Override
    @RequestMapping(value = "/getExcelContent", method = RequestMethod.POST)
    public void parserQuotesInExcelFile(HttpServletRequest request, HttpServletResponse response, @RequestBody ExcelFile fileContent) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, (ExceptionCatchableProcessWithResult) () -> {
            List<ValidationExceptionDetails> invalids = new ArrayList<>(); //用于存储表格中错误信息的集合
            Map<String, Object> map = new HashMap<>();
            if (fileContent == null) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "后台没有接受到文件信息!");
            }
            List<MmQuoteDto> mmQuoteDtos = mmQuoteExcelConverter.parserQuoteInExcelFile(fileContent, invalids, getLoginUser(request).getUserId());
            if (mmQuoteDtos != null) {
                map.put("fileContent", mmQuoteDtos);
                map.put("error", invalids);
                return map;
            }
            return null;
        });
    }


    //跨域OPTIONS请求
    @RequestMapping(value = "/get", method = RequestMethod.OPTIONS)
    public void getMyValidQuotesOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/save", method = RequestMethod.OPTIONS)
    public void publishQuoteOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/getAlliance", method = RequestMethod.OPTIONS)
    public void getMyAvailableInstitutionsOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/allianceSave", method = RequestMethod.OPTIONS)
    public void publishAllianceQuotesOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/getExcelContent", method = RequestMethod.OPTIONS)
    public void parserQuotesInExcelFile(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/excelSave", method = RequestMethod.OPTIONS)
    public void excelSave(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

}
