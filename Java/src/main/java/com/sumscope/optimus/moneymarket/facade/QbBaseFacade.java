package com.sumscope.optimus.moneymarket.facade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sumscope.optimus.moneymarket.model.dto.UserAreaDto;
import com.sumscope.optimus.moneymarket.model.dto.ValidationUserRequestDto;

/**
 * Created by qikai.yu on 2016/4/21.
 * 同业理财业务Facade
 */
public interface QbBaseFacade {

	/**
     * 获取所有地区列表
     */ 
    void getAreaList(HttpServletRequest request, HttpServletResponse response);

    /**
     * 设置用户的常用地区
     */
    void setUserArea(UserAreaDto userAreaDto, HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取用户的常用地区,托管类型,期限区间以及保本类型
     */
    void getUserArea(HttpServletRequest request, HttpServletResponse response);

    /**
     * 验证用户
     */
    void validateUser(HttpServletRequest request, HttpServletResponse response, ValidationUserRequestDto validationUserRequestDto);
}
