package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.dao.MmAllianceInstitutionDao;
import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shaoxu.wang on 2016/6/12.
 */
@Service
public class MMAllianceInstitutionServiceImpl implements MmAllianceInstitutionService {

    @Autowired
    private MmAllianceInstitutionDao mmAllianceInstitutionDao;
    @Autowired
    private InstitutionService institutionService;



    /**
     * 通过报价人本机构的机构ID，获取本机构的联盟机构list
     * 若输入的机构不属于联盟机构则返回null
     **/
    @Override
    @CacheMe(timeout =  Constant.CACHE_TIMEOUT, synchornizeUpdate = false)
    public List<Institution> getAllianceInstitutionsByGivenInstitutionId(String institutionId) {
//        Institution primaryInstitution = getPrimaryInstitutionId(institutionId);
        if(institutionId != null){
            List<String> strings = queryAllianceInstitutionIdsByPrimaryInsitution(institutionId);
            return getInstitutionsFromIDs(strings);
        }
        return null;
    }

    private List<Institution> getInstitutionsFromIDs(List<String> institutionIDList) {
        Map<String, Institution> institutionMap = institutionService.retrieveInstitutionsById();
        List<Institution> result = new ArrayList<>();
        if(institutionIDList!=null&&institutionIDList.size()>0){
            for(String insId: institutionIDList){
                if(institutionMap.get(insId)!=null){
                    result.add(institutionMap.get(insId));
                }
            }
        }
        return result;
    }


    /**
     * 根据盟主institutionId获取联盟机构
     * @param primaryInstitutionId 盟主机构ID
     * @return 所有子机构ID与盟主机构ID 列表
     */
    private List<String> queryAllianceInstitutionIdsByPrimaryInsitution(String primaryInstitutionId) {
        if (primaryInstitutionId != null) {
                List<String> list=mmAllianceInstitutionDao.getAvailableInstitutionIds(primaryInstitutionId);
               if(!list.contains(primaryInstitutionId)){
                    list.add(primaryInstitutionId);
               }
                if(!list.contains(primaryInstitutionId)){
                    list.add(primaryInstitutionId);
                }
                return list;
        }
        return new ArrayList<>();
    }


    @Override
    public boolean isAuthorizedForAllianceQuote(String userId) {
        return mmAllianceInstitutionDao.isAuthorizedForAllianceQuote(userId);
    }

    @Override
    public boolean isAuthorizedForBrokerQuote(String userId) {
        return mmAllianceInstitutionDao.isAuthorizedForBrokerQuote(userId);
    }

    /**
     * @param institutionId 任意机构ID
     * @return 若该机构从属于某主机构则返回主机构对象，否则返回null
     */
    public Institution getPrimaryInstitutionId(String institutionId) {
        String primaryInstitutionId = mmAllianceInstitutionDao.getPrimaryInstitution(institutionId);
        return institutionService.retrieveInstitutionsById().get(primaryInstitutionId);
    }
}

