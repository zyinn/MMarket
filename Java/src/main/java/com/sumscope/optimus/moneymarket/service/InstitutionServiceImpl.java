package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.commons.log.LogManager;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.enums.IsHeadCompany;
import com.sumscope.optimus.moneymarket.commons.util.Pinyin4jUtil;
import com.sumscope.optimus.moneymarket.dao.InstitutionDao;
import com.sumscope.optimus.moneymarket.dao.PrimeInstitutionDao;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by fan.bai on 2016/6/17.
 * InstitutionService 的实现
 */
@Service
public class InstitutionServiceImpl implements InstitutionService {
    @Autowired
    private UserBaseService userBaseService;
    @Autowired
    private PrimeInstitutionDao primeInstitutionDao;
    @Autowired
    private InstitutionDao institutionDao;
    /**
     * Contact与ContactUser对象转换
     */
    private static ContactUser getContactUser(User user) {
        ContactUser contactUser = new ContactUser();
        BeanUtils.copyProperties(user, contactUser);
        return contactUser;
    }

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false)
    public Map<String, PrimeCompany> retrievePrimeInstitutions() {
        List<PrimeCompany> primeCompanies = primeInstitutionDao.retrieveAllPrimeCompanys();
        Map<String, PrimeCompany> result = new HashMap<>();
        for (PrimeCompany company : primeCompanies) {
            result.put(company.getCompanyId(), company);
        }
        return result;
    }

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false)
    public Map<String, Set<ContactUser>> retrieveAllPrimeContacts() {
        Map<String, Set<ContactUser>> contactsMap = new HashMap<>();
        Map<String, User> stringUserMap = userBaseService.retreiveAllUsersGroupByUserID();
        Map<String, List<Contact>> contactsByInstitutionId = retrievePrimeInstitutionContacts();
        if (contactsByInstitutionId != null && contactsByInstitutionId.size() > 0) {
            for (String key : contactsByInstitutionId.keySet()) {
                Set<ContactUser> contactEntitySet = new HashSet<>();
                List<Contact> contactList = contactsByInstitutionId.get(key);
                contactsMap.put(key, contactEntitySet);
                for (Contact contact : contactList) {
                    User user = stringUserMap.get(contact.getContactId());
                    if (user != null) {
                        ContactUser contactUser = getContactUser(user);
                        contactUser.setQuoteAttribute(contact.getQuoteAttribute());
                        contactEntitySet.add(contactUser);//添加contactUser对象到Set
                    }
                }
            }
        }
        return contactsMap;
    }

    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false)
    public Map<String, List<User>> retrieveAllUser() {
        Map<String, List<User>> contactsMap = new HashMap<>();
        Map<String, User> stringUserMap = userBaseService.retreiveAllUsersGroupByUserID();
        for (User user : stringUserMap.values()) {
            String companyId = user.getCompanyId();
            List<User> list = contactsMap.get(companyId);
            if(list== null){
                list=new ArrayList<>();
                contactsMap.put(companyId,list);
            }

            if (StringUtils.isEmpty(user.getDisplayNamePinYin())) {
                user.setDisplayNamePinYin(Pinyin4jUtil.getPinYin(user.getDisplayName()));
            }
            if (StringUtils.isEmpty(user.getDisplayNamePY())) {
                user.setDisplayNamePY(Pinyin4jUtil.getPinYinHeadChar(user.getDisplayName()));
            }
            list.add(user);
        }
        return contactsMap;
    }

    /**
     * 获取所有的精选机构的联系人的联系方式，并且按照机构id分类
     *
     * @return
     */
    private Map<String, List<Contact>> retrievePrimeInstitutionContacts() {
        Map<String, List<Contact>> result = new HashMap<String, List<Contact>>();
        List<Contact> list = primeInstitutionDao.retrieveAllPrimeContacts();
        for (Contact contact : list) {
            if (result.containsKey(contact.getInstitutionId())) {
                List<Contact> item = result.get(contact.getInstitutionId());
                item.add(contact);
                result.put(contact.getInstitutionId(), item);
            } else {
                List<Contact> item = new ArrayList<Contact>();
                item.add(contact);
                result.put(contact.getInstitutionId(), item);
            }
        }
        return result;
    }


    @Override
    @CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false)
    public Map<String, Institution> retrieveInstitutionsById() {
        List<Institution> institutions = institutionDao.retrieveInstitutions();
        Map<String, Institution> institutionMap = new HashMap<>();
        for (Institution institution : institutions) {
            institutionMap.put(institution.getId(), institution);
        }
        //处理集合 获取并设置fundSize和parentIds
        for (Institution institution : institutions) {
            //如果不是总公司
            if (!isHeadInstitution(institution)) {
                setFundSizeByParent(institution, institutionMap);
            }
            //如果拼音字段为空则设置拼音
            if (StringUtils.isBlank(institution.getPinyin())) {
                institution.setPinyin(Pinyin4jUtil.getPinYinHeadChar(institution.getName()));
            }
            if (StringUtils.isBlank(institution.getPinyinFull())) {
                institution.setPinyinFull(Pinyin4jUtil.getPinYin(institution.getName()));
            }
        }
        return institutionMap;
    }

    private boolean isHeadInstitution(Institution institution) {
        return StringUtils.equals(institution.getIsHead(), IsHeadCompany.HEAD_COMPANY.getValue());
    }

    private void setFundSizeByParent(Institution institution, Map<String, Institution> institutionMap) {
        if (isHeadInstitution(institution)) {
            return;
        } else {
            List<Institution> parentInstitutionList = new ArrayList<>(); //该list用以记录所有迭代到的父机构
            Institution parentHeadInstitution = getParentHeadInstitution(institution, institutionMap, parentInstitutionList);
            if (parentHeadInstitution != null) {
                institution.setFundSize(parentHeadInstitution.getFundSize());
                institution.setParentIds(parentInstitutionList);
            }
        }
    }

    private Institution getParentHeadInstitution(Institution institution, Map<String, Institution> institutionMap, List<Institution> parentInstitutionList) {
        String parentId = institution.getParentId();
        Institution parentInstitution = institutionMap.get(parentId);
        // 若有父机构id就是本机构id的数据错误，则可能造成迭代无法退出，因此判断条件中增加父机构ID和本机构ID的判断。
        if (parentInstitution != null && !parentInstitution.getId().equals(institution.getId())) {
            if (parentInstitutionList.contains(parentInstitution)) {
                // 有父机构的循环依赖，既：A.parentInstitution = B, B.parentinstitution = A 的数据错误，此时直接返回
                LogManager.error("出现父机构循环依赖的数据错误！当前机构：" + institution.getId());
                return parentInstitution;
            } else {
                parentInstitutionList.add(parentInstitution);
            }
            if (isHeadInstitution(parentInstitution)) {
//                父机构是head机构，直接返回。
                return parentInstitution;
            } else {
                //父机构仍然不是Head机构，迭代向上查询
                return getParentHeadInstitution(parentInstitution, institutionMap, parentInstitutionList);
            }

        } else {
            //数据错误，找不到对应的父机构。则退出迭代返回自己。
            LogManager.error("获取父机构失败，当前机构ID：" + institution.getId());
            return institution;
        }
    }
}
