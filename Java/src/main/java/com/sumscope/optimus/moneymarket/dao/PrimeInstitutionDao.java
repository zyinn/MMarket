package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.model.dbmodel.Contact;
import com.sumscope.optimus.moneymarket.model.dbmodel.PrimeCompany;
import com.sumscope.optimus.moneymarket.model.dto.ContactNumber;

import java.util.List;

/**
 * Created by fan.bai on 2016/5/12.
 * 精品机构相关Dao
 */
public interface PrimeInstitutionDao {

    /**
     * @return 所有精品机构信息，以机构id为key
     */
    List<PrimeCompany> retrieveAllPrimeCompanys();

    /**
     * 获取所有精品机构和其对应联系人的关系
     * @return 精品机构ID为Key，值为所对应的联系人ID列表
     */
   List<Contact> retrieveAllPrimeContacts();


}
