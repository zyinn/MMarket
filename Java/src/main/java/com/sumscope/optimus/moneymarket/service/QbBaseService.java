package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import com.sumscope.optimus.moneymarket.model.dbmodel.UserPreference;

import java.util.List;
import java.util.Map;

/**
 * 查询QB用户
 *
 * @author xingyue.wang
 *
 */
public interface QbBaseService {

    /**
     * 设置用户的常用地区
     */
    boolean setUserArea(UserPreference userPreference);


    /**
     * 获取用户的常用地区
     */
    UserPreference getUserArea(String userid);

    /**
     * 获取所有地区列表
     */
    List<Map<String, Object>> getAreaList();
    
    /**
     * 获取用户自身地区省份
     */
    String getUserProvince(String userid);
    
    /**
     * 获取期限区间
     */
    Map<String, Object> period();
    
    /**
     * 获取保本类型
     */
    List<Map<String, Object>> quoteType();
    
    /**
     * 获取托管类型
     */
    List<Map<String, Object>> trustType();

    /**
     * 关键词查询公司信息
     *
     * @param keyword 关键词
     */
    List<Institution> queryByKeyword(String keyword);

    /**
     * 关键词查询用户，关键词不能为空，至少是2位
     *
     *
     * @param keyword 关键词
     * @return 返回List，index为0，表示是QB用户，index为1，表示是QQ用户
     */
    List<List<User>> queryAllUserWithPinYin(String keyword);
}
