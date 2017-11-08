package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;

import java.util.List;

/**
 * 机构Dao接口
 */
public interface InstitutionDao {
    /**
     * @return 所有机构
     */
    List<Institution> retrieveInstitutions();
}   
