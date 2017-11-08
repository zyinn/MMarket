package com.sumscope.optimus.moneymarket.dao;

import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.UserPreference;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 接口实现类
 */
@Component
public class UserPreferenceDaoImpl implements UserPreferenceDao{

	private static final String QUERY_ALL_AREA = "com.sumscope.optimus.moneymarket.mapping.UserPreferenceDTOMapper.getAreaList";
	
	private static final String QUERY_USERPREFERENCE = "com.sumscope.optimus.moneymarket.mapping.UserPreferenceDTOMapper.isRecordExist";
	
	private static final String UPDATE_USERPREFERENCE = "com.sumscope.optimus.moneymarket.mapping.UserPreferenceDTOMapper.update";
	
	private static final String INSERT_USERPREFERENCE = "com.sumscope.optimus.moneymarket.mapping.UserPreferenceDTOMapper.insert";

	private static final String INSERT_INSTITUTION_USER_MAPPING = "com.sumscope.optimus.moneymarket.mapping.UserPreferenceDTOMapper.insertInstitutionUserMapping";

	private static final String QUERY_INSTITUTION_USER_MAPPING = "com.sumscope.optimus.moneymarket.mapping.UserPreferenceDTOMapper.getInstitutionUserMapping";

	private static final String UPDATE_INSTITUTION_USER_MAPPING = "com.sumscope.optimus.moneymarket.mapping.UserPreferenceDTOMapper.updateInstitutionUserMapping";

	@Autowired
	@Qualifier(value= Constant.BUSINESS_SQL_SESSION_TEMPLATE)
	private SqlSessionTemplate sqlSessionTemplate;
	
	@Override
	public List<Map<String, Object>> getAreaList() {
		return sqlSessionTemplate.selectList(QUERY_ALL_AREA);
	}

	@Override
	public UserPreference isRecordExist(Map<String, Object> map) {
		return sqlSessionTemplate.selectOne(QUERY_USERPREFERENCE, map);
	}

	@Override
	public int updateUserPreference(UserPreference userPreference) {
		return sqlSessionTemplate.update(UPDATE_USERPREFERENCE, userPreference);
	}

	@Override
	public int insertUserPreference(UserPreference userPreference) {
		return sqlSessionTemplate.insert(INSERT_USERPREFERENCE, userPreference);
	}

	@Override
	public int insertInstitutionUserMapping(List<UserPreference> list) {
		return sqlSessionTemplate.insert(INSERT_INSTITUTION_USER_MAPPING,list);
	}

	@Override
	public List<UserPreference> getInstitutionUserMapping() {
		return sqlSessionTemplate.selectList(QUERY_INSTITUTION_USER_MAPPING);
	}

	@Override
	public int updateInstitutionUserMapping(List<UserPreference> list) {
		return sqlSessionTemplate.update(UPDATE_INSTITUTION_USER_MAPPING, list);
	}
}
