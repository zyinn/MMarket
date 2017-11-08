package com.sumscope.optimus.moneymarket.dao;

import java.util.List;
import java.util.Map;

import com.sumscope.optimus.moneymarket.model.dbmodel.UserPreference;

/**
 * 用户偏好表Dao接口
 */
public interface UserPreferenceDao {
	String PREFERENCE_TYPE = "preferenceType";
	String USER_ID = "userId";

	/**
	 * 获取所有地区列表
	 * @return 地区列表
	 */
	List<Map<String,Object>> getAreaList();
	
	/**
	 * 根据用户ID及偏好类型获取当前用户的偏好
	 * @param map userId preferenceType
	 * @return UserPreference 用户偏好信息
	 */
	UserPreference isRecordExist(Map<String, Object> map);
	
	/**
	 * 插入用户偏好数据
	 * @param userPreference userPreference
	 * @return 影响数据条数
	 */
	int updateUserPreference(UserPreference userPreference);
	
	/**
	 * 更新用户偏好数据
	 * @param userPreference userPreference
	 * @return 影响数据条数
	 */
	int insertUserPreference(UserPreference userPreference);

	/**
	 * 把报价时的机构-人员mapping关系保存到user_preference表中
	 * @param list
	 */
	int insertInstitutionUserMapping(List<UserPreference> list);

	/**
	 * 根据 preference_type= 'INSTITUTION_USER_MAP' 查询出所有的机构-人员关系
	 * @return List
     */
	List<UserPreference> getInstitutionUserMapping();

	/**
	 * 更新 机构-人员mapping关系
	 * @param list
	 * @return
     */
	int updateInstitutionUserMapping(List<UserPreference> list);
}
