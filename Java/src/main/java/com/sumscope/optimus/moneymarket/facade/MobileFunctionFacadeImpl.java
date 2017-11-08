package com.sumscope.optimus.moneymarket.facade;

import com.sumscope.optimus.moneymarket.commons.enums.ContactQuoteAttribute;
import com.sumscope.optimus.moneymarket.commons.util.Utils;
import com.sumscope.optimus.moneymarket.facade.converter.MmQuoteDtoConverter;
import com.sumscope.optimus.moneymarket.facade.converter.MobileQueryQuoteMainParameterDtoConverter;
import com.sumscope.optimus.moneymarket.facade.converter.QuoteUserInfoDtoConverter;
import com.sumscope.optimus.moneymarket.gatewayinvoke.GatewayMobileService;
import com.sumscope.optimus.moneymarket.gatewayinvoke.model.QbUserId;
import com.sumscope.optimus.moneymarket.model.dbmodel.ContactUser;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuoteMainQueryParameters;
import com.sumscope.optimus.moneymarket.model.dbmodel.MobileMatrixResult;
import com.sumscope.optimus.moneymarket.model.dto.*;
import com.sumscope.optimus.moneymarket.service.MobileFunctionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 向手机理财应用提供服务的Facade实现类
 * Created by huaijie.chen on 2016/7/21.
 */
@RestController
@RequestMapping(value = "/mobile", produces = MediaType.APPLICATION_JSON_VALUE)
public class MobileFunctionFacadeImpl extends AbstractFacadeImpl implements MobileFunctionFacade {
    @Autowired
    private MobileFunctionService mobileFunctionService;
    @Autowired
    private MmQuoteDtoConverter quoteConverter;
    @Autowired
    private QuoteUserInfoDtoConverter quoteUserInfoDtoConverter;

    @Autowired
    private MobileQueryQuoteMainParameterDtoConverter mobileQueryQuoteMainParameterDtoConverter;

    @Autowired
    private GatewayMobileService gatewayMobileService;

    @Autowired
    private  QbBaseFacadeImpl qbBaseFacadeImpl;
    /**
     * 根据输入的参数获取矩阵计算结果。
     * 输入参数类型为MobileMatrixParameterDto
     * 返回前端的参数类型为List<MobileMatrixResponseDto>
     */
    @Override
    @RequestMapping(value = "/getMobileMatrix", method = RequestMethod.POST)
    public void getMobileMatrix(HttpServletRequest request, HttpServletResponse response, @RequestBody MobileMatrixParameterDto matrixParameter) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            List<MobileMatrixResponseDto> ret = new ArrayList<>();
            List<MobileMatrixResult> mobileMatrixResults = mobileFunctionService.getMobileMatrix(matrixParameter);
            for (MobileMatrixResult mobileMatrixResult : mobileMatrixResults) {
                MobileMatrixResponseDto mobileMatrixResponseDto = new MobileMatrixResponseDto();
                BeanUtils.copyProperties(mobileMatrixResult, mobileMatrixResponseDto);
                ret.add(mobileMatrixResponseDto);
            }
            return ret;
        });
    }

    /**
     * 根据机构及报价类型获取该精品机构的对应联系人
     * 参数中的机构应为精品机构，否则将获得一个空的列表
     * 返回：List<QuoteUserInfoDto>
     */
    @Override
    @RequestMapping(value = "/getInstitutionContacts", method = RequestMethod.POST)
    public void getInstitutionContacts(HttpServletRequest request, HttpServletResponse response, @RequestBody InstitutionContactsParameterDto parameterDto) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            List<QuoteUserInfoDto> ret = new ArrayList<>();
            //同业理财 CD("CD");线下资金FI("FI")
            ContactQuoteAttribute contactQuoteAttribute = parameterDto.getQuoteType().getContactQuoteAttribute();
            String institutionId = parameterDto.getInstitutionId();
            List<ContactUser> contacts = mobileFunctionService.getPrimeInstitutionContarct(institutionId, contactQuoteAttribute);
            if (contacts != null && contacts.size() > 0) {
                ret = quoteUserInfoDtoConverter.convertContactUserListToQuoteUserInfoDtoList(contacts);
            }
            return ret;
        });
    }

    /**
     * 查询报价单。可能根据期限的价格值进行排序。若排序期限设置为0则按报价日期降序排序。
     * 返回类型为List<MmQuoteDto>
     */
    @Override
    @RequestMapping(value = "/queryQuoteMain", method = RequestMethod.POST)
    public void queryQuoteMain(HttpServletRequest request, HttpServletResponse response, @RequestBody MobileQueryQuoteMainParameterDto parameter) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            List<MmQuoteDto> ret = new ArrayList<>();
            MmQuoteMainQueryParameters queryP = mobileQueryQuoteMainParameterDtoConverter.convertToMainQueryParameter(parameter);
            List<MmQuote> mmQuotes = mobileFunctionService.retrieveMmQuoteMain(queryP);
            if (mmQuotes != null && mmQuotes.size() > 0) {
                Map<String,QbUserId> qbUserInfo = gatewayMobileService.getQbUserId();
                ret = quoteConverter.converterToMobileMmQuoteDto(mmQuotes,parameter.isSortByInstitutionPY(),qbUserInfo);
            }
            return ret;
        });
    }

    @Override
    @RequestMapping(value = "/getDictTag", method = RequestMethod.POST)
    public void getDictTag(HttpServletRequest request, HttpServletResponse response) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            Map<String, Object> map = new HashMap<>();
            map.put("tags", qbBaseFacadeImpl.retrieveAllValidTags());
            return map;
        });
    }

    //跨域OPTIONS请求
    @RequestMapping(value = "/getMobileMatrix", method = RequestMethod.OPTIONS)
    public void getMobileMatrixOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/getInstitutionContacts", method = RequestMethod.OPTIONS)
    public void getInstitutionContactsOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/queryQuoteMain", method = RequestMethod.OPTIONS)
    public void queryQuoteMainOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/getDictTag", method = RequestMethod.OPTIONS)
    public void getDictTagOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }
}
