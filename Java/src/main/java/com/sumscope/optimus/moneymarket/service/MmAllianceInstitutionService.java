package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;

import java.util.List;

/**
 * Created by kedong.li on 2016/5/26.
 * 联盟机构服务，用于提供关于联盟机构的数据
 */
public interface MmAllianceInstitutionService {
	
	/**
     * 根据输入的机构id获取与该机构同属一个盟主的所有联盟机构。若该机构没有盟主机构返回空list。
	 * 输入的机构也有可能是盟主机构
	 * 返回值包含盟主机构
     * 
     */
	List<Institution> getAllianceInstitutionsByGivenInstitutionId(String institutionId);


	/**
	 * 用户是否有联盟报价权限
	 * @param userId 用户id
	 * @return true 有权限
	 */
	boolean isAuthorizedForAllianceQuote(String userId);

	/**
	 * 用户是否有代报价权限
	 * @param userId 用户id
	 * @return true 有权限
	 */
	boolean isAuthorizedForBrokerQuote(String userId);

	/**
	 * 根据机构id获取本机构的盟主机构信息
	 * @param institutionId
	 * @return
     */
	Institution getPrimaryInstitutionId(String institutionId);


}
