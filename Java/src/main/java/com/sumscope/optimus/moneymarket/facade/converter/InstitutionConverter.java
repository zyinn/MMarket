package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.moneymarket.commons.util.Pinyin4jUtil;
import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import com.sumscope.optimus.moneymarket.model.dbmodel.UserPreference;
import com.sumscope.optimus.moneymarket.model.dto.InstitutionDto;
import com.sumscope.optimus.moneymarket.service.MmQuoteQueryService;
import com.sumscope.optimus.moneymarket.service.UserBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by fan.bai on 2016/6/7.
 * 机构Dto转换类
 */
@Service
public class InstitutionConverter {

    @Autowired
    private MmQuoteQueryService mMQuoteQueryService;

    @Autowired
    private UserBaseService userBaseService;

    /**
     * 根据机构id转换机构Dto
     *
     * @param institutionList 机构Id
     * @return 机构Dto，若未找到返回null
     */
    public List<InstitutionDto> convertInstitutionDtos(List<Institution> institutionList, String userid) {
        List<InstitutionDto> list = new ArrayList<InstitutionDto>();
        if (institutionList != null && institutionList.size() > 0) {
            Map<String, String> userPreferenceMap = mMQuoteQueryService.retrieveAllUserIdMappingByInstitutionId(userid);
            Map<String, User> userMap = userBaseService.retreiveAllUsersGroupByUserID();
            for (Institution institution : institutionList) {
                InstitutionDto institutionDto = new InstitutionDto();
                if (institution != null) {
                    //封装institutionDto对象
                    institutionDto.setInstitutionId(institution.getId() != null ? institution.getId() : "-1");
                    institutionDto.setDisplayName(institution.getName() != null ? institution.getName() : "-1");
                    institutionDto.setPinyin(institution.getPinyin());
                    institutionDto.setPinyinFull(institution.getPinyinFull());

                    //新增联系人名称 id字段
                    String userId=null;
                    if((userPreferenceMap!=null && userPreferenceMap.size()>0 ) && ( institution!=null&&institution.getId()!=null)) {
                        userId =userPreferenceMap.get(institution.getId());
                    }
                    User user = userMap.get(userId!=null? userId : null);
                    if(user!=null){
                    user.setDisplayNamePinYin(Pinyin4jUtil.getPinYin(user.getDisplayName()));
                    user.setDisplayNamePY( Pinyin4jUtil.getPinYinHeadChar(user.getDisplayName()));
                    }
                    institutionDto.setContactsDisplayNamePinYin(user != null ? user.getDisplayNamePinYin():"");
                    institutionDto.setContactsDisplayNamePY(user != null ? user.getDisplayNamePY():"");
                    institutionDto.setInstitutionContactsId(user != null ? userId : "");
                    institutionDto.setInstitutionContactsName(user != null ? user.getDisplayName() : "");
                    list.add(institutionDto);
                }

            }
        }
        //排序机构名排序
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<InstitutionDto>() {
                public int compare(InstitutionDto institution, InstitutionDto institutions) {
                    if(institution != null && institution.getDisplayName()!=null){
                        if(institutions.getDisplayName() != null){
                            return institution.getDisplayName().compareTo(institutions.getDisplayName());
                        }else{
                            return 1;
                        }
                    }else{
                        return -1;
                    }

                }
            });
        }
        return list;
    }

    /**
     * 机构信息 转 机构联系人信息
     * @param institutionDtos
     * @param stringSetMap
     * @return
     */
    public void convertInstitutionUsers(List<InstitutionDto> institutionDtos,Map<String, List<User>> stringSetMap){
        for(InstitutionDto institutionDto: institutionDtos){
            List<User> users = stringSetMap.get(institutionDto.getInstitutionId());
            institutionDto.setUserList(users);
        }
    }
}
