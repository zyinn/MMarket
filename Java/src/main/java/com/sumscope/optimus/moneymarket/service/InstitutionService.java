package com.sumscope.optimus.moneymarket.service;
/**
 * Created by fan.bai on 2016/5/28.
 * 机构服务，用于机构相关查询
 */

import com.sumscope.optimus.moneymarket.model.dbmodel.ContactUser;
import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;
import com.sumscope.optimus.moneymarket.model.dbmodel.PrimeCompany;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import com.sumscope.optimus.moneymarket.model.dto.ContactNumber;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fan.bai on 2016/6/17.
 * 提供机构相关服务
 */
public interface InstitutionService {

    /**
     * 获取所有精品机构和其对应联系人的关系
     *
     * @return 精品机构ID为Key，值为所对应的联系人列表
     */
    Map<String, Set<ContactUser>> retrieveAllPrimeContacts();

    /**
     * 获取精品机构
     *
     * @return 精品机构ID（即QB机构）为key，PrimeCompany为值
     */
    Map<String, PrimeCompany> retrievePrimeInstitutions();


    /**
     * 获取所有的机构并且查询机构的规模和父节点id
     *
     * @return
     */
    Map<String, Institution> retrieveInstitutionsById();

    /**
     * 获取所有精品机构和其对应联系人
     * @return 精品机构ID为Key，值为所对应的联系人列表
     */
    Map<String, List<User>> retrieveAllUser();
}
