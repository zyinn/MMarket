package com.sumscope.optimus.moneymarket.dao;

import java.util.List;

public interface MmAllianceInstitutionDao {

	/**
	 * 通过报价人所属机构获取其联盟机构
	 * 通过@MyCache缓存查询到的联盟机构数据
	 * 
	 * @param userInstitutionId 某用户机构Id
	 * @return List 所有联盟机构Id
	 */
	List<String> getAvailableInstitutionIds(String userInstitutionId);

	/**
	 * 用户是否有权进行联盟报价
	 * @param userId 用户id
	 * @return true 有权限
	 */
	boolean isAuthorizedForAllianceQuote(String userId);
	/**
	 * 用户是否有权进行代报价
	 * @param userId 用户id
	 * @return true 有权限
	 */
	boolean isAuthorizedForBrokerQuote(String userId);

	String getPrimaryInstitution(String userInstitutionId);

}
