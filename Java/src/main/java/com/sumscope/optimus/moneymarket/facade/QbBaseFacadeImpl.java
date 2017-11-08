package com.sumscope.optimus.moneymarket.facade;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sumscope.optimus.commons.util.JsonUtil;
import com.sumscope.optimus.moneymarket.commons.enums.CalculatedBankNature;
import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.commons.enums.PreferenceType;
import com.sumscope.optimus.moneymarket.commons.enums.QueryFundSize;
import com.sumscope.optimus.moneymarket.commons.util.HTTPResponseUtil;
import com.sumscope.optimus.moneymarket.commons.util.Utils;
import com.sumscope.optimus.moneymarket.facade.converter.MmTagDtoConverter;
import com.sumscope.optimus.moneymarket.facade.converter.QuoteMethodsConverter;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmTag;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import com.sumscope.optimus.moneymarket.model.dbmodel.UserPreference;
import com.sumscope.optimus.moneymarket.model.dto.*;
import com.sumscope.optimus.moneymarket.service.AuthorizationService;
import com.sumscope.optimus.moneymarket.service.MmAllianceInstitutionService;
import com.sumscope.optimus.moneymarket.service.MmTagService;
import com.sumscope.optimus.moneymarket.service.QbBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by qikai.yu on 2016/4/21.
 * 同业理财业务Facade的实现
 */
@RestController
@RequestMapping(value = "/base", produces = MediaType.APPLICATION_JSON_VALUE)
public class QbBaseFacadeImpl extends AbstractFacadeImpl implements QbBaseFacade {

    @Autowired
    private QbBaseService qbBaseService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private MmAllianceInstitutionService mmAllianceInstitutionService;

    @Autowired
    private QuoteMethodsConverter quoteMethodsConverter;

    @Autowired
    private MmTagService mmTagService;

    @Autowired
    private MmTagDtoConverter mmTagDtoConverter;

    @Value(value = "${application.enableMockData}")
    private String enabled;

    @Override
    @RequestMapping(value = "/validateUser", method = RequestMethod.POST)
    public void validateUser(HttpServletRequest request, HttpServletResponse response, @RequestBody ValidationUserRequestDto validationUserRequestDto) {
        Utils.addHeaderOrigin(request, response);
        if(enabled!= null && enabled.toUpperCase().equals("TRUE")) {
            String username = validationUserRequestDto.getUsername();
            String plainPassword = validationUserRequestDto.getPlainPassword();
            performWithExceptionCatch(response, () -> {
                User user = authorizationService.validateUser(username, plainPassword);
                ValidationUserResponseDto responseDto = new ValidationUserResponseDto();
                if (user != null) {
                    responseDto.setUserID(user.getUserId());
                    responseDto.setPassword(user.getPassword());
                    responseDto.setUsername(user.getUserName());
                    responseDto.setSuccess(true);
                } else {
                    responseDto.setSuccess(false);
                }
                return responseDto;
            });
        }else{
            HTTPResponseUtil.returnMessageThroughHttpServletResponse("Invalid use of Method!",response);
        }

    }

    @Override
    @RequestMapping(value = "/areaList", method = RequestMethod.POST)
    public void getAreaList(HttpServletRequest request, HttpServletResponse response) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> qbBaseService.getAreaList());
    }

    @Override
    @RequestMapping(value = "/setarea", method = RequestMethod.POST)
    public void setUserArea(@RequestBody UserAreaDto userAreaDto, HttpServletRequest request, HttpServletResponse response) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, () -> {
            UserPreference userPreference = new UserPreference();
            userPreference.setPreferenceType(PreferenceType.AREA);
            userPreference.setPreferenceValue(JsonUtil.writeValueAsString(userAreaDto.getProvince()));
            userPreference.setUserId(getLoginUser(request).getUserId());
            return qbBaseService.setUserArea(userPreference);
        });
    }

    @Override
    @RequestMapping(value = "/init_data", method = RequestMethod.POST)
    public void getUserArea(HttpServletRequest request,
                            HttpServletResponse response) {
        Utils.addHeaderOrigin(request, response);
        performWithExceptionCatch(response, (ExceptionCatchableProcessWithResult) () -> {
            String userid = getLoginUser(request).getUserId();
            Map<String, Object> map = new HashMap<>();

            //获取到用户的报价方法.
            map.put("region", getUserProvinces(userid));
            map.put("quote_type", qbBaseService.quoteType());
            map.put("period", qbBaseService.period());
            map.put("trust_type", qbBaseService.trustType());
            map.put("quote_method", getMyQuoteMethods(userid));
            map.put("tags", retrieveAllValidTags());
            map.put("fundSizes", retrieveAllFundSizes());
            map.put("calculationBankNature", retrieveAllCalculatedBankNature());
            return map;
        });
    }

    private List<Map<String, Object>> getUserProvinces(String userid) {
        UserPreference userPreference = qbBaseService.getUserArea(userid);
        List<String> list;
        if (userPreference == null) {
            list = initArea(userid);
        } else {
            list = JsonUtil.readValue(userPreference.getPreferenceValue(), new TypeReference<List<String>>() {
            });
            if (list == null) {
                list = initArea(userid);
            }
        }

        //转化省份及城市数据格式
        List<Map<String, Object>> userareato = new ArrayList<>();
        for (String province : list) {
            Map<String, Object> provincemap = new HashMap<>();
            provincemap.put("province", province);
            userareato.add(provincemap);
        }
        return userareato;
    }

    private Object retrieveAllCalculatedBankNature() {
        List<GeneralEnumDto> result = new ArrayList<>();
        CalculatedBankNature[] bankNatures = CalculatedBankNature.values();
        for (CalculatedBankNature bankNature : bankNatures) {
            convertEnumToDto(result, bankNature.name(), bankNature.getDisplayName());
        }
        return result;
    }

    private void convertEnumToDto(List<GeneralEnumDto> result, String name, String displayName) {
        GeneralEnumDto dto = new GeneralEnumDto();
        dto.setName(name);
        dto.setDisplayName(displayName);
        result.add(dto);
    }

    private Object retrieveAllFundSizes() {
        List<GeneralEnumDto> result = new ArrayList<>();
        QueryFundSize[] queryFundSizes = QueryFundSize.values();
        for (QueryFundSize size : queryFundSizes) {
            convertEnumToDto(result, size.name(), size.getDisplayName());
        }
        return result;

    }

    public List<MmTagDto> retrieveAllValidTags() {
        List<MmTagDto> result = new ArrayList<>();
        Map<String, MmTag> tagsMap = mmTagService.retrieveAllTags();
        Collection<MmTag> values = tagsMap.values();
        List<MmTag> orderList = new ArrayList<>(values);
        orderList.sort((o1, o2) -> o1.getSeq() - o2.getSeq());
        for (MmTag tag : orderList) {
            if (tag.isActive()) {
                MmTagDto tagDto = mmTagDtoConverter.convertToDto(tag);
                result.add(tagDto);
            }
        }
        return result;
    }

    /**
     * 获取用户拥有的报价方式
     */
    private List<MethodType> getMyQuoteMethods(String userId) {
        boolean isValidForAllianceQuote = mmAllianceInstitutionService.isAuthorizedForAllianceQuote(userId); //联盟报价权限
        boolean isValidForBrokerQuote = mmAllianceInstitutionService.isAuthorizedForBrokerQuote(userId);  //中介报价权限
        return quoteMethodsConverter.convertQuoteMethods(isValidForAllianceQuote, isValidForBrokerQuote);
    }

    private List<String> initArea(String userid) {
        List<String> list = new ArrayList<>();
        //获取用户所在省份或城市
        String userprovince = qbBaseService.getUserProvince(userid);
        Set<String> set = new HashSet<>();
        set.add("北京");
        set.add("上海");
        set.add("广东");
        set.add(userprovince);
        list.addAll(set);
        return list;
    }

    //跨域OPTIONS请求
    @RequestMapping(value = "/validateUser", method = RequestMethod.OPTIONS)
    public void validateUserOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/areaList", method = RequestMethod.OPTIONS)
    public void getAreaListOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/setarea", method = RequestMethod.OPTIONS)
    public void setUserAreaOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }

    @RequestMapping(value = "/init_data", method = RequestMethod.OPTIONS)
    public void getUserAreaOptions(HttpServletRequest request, HttpServletResponse response) {
        Utils.getMethodOptions(request, response);
    }
}
