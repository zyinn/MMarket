package com.sumscope.optimus.moneymarket.facade;

import com.sumscope.optimus.commons.facade.AbstractExceptionCatchedFacadeImpl;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 各个 Facade 实现的抽象父类
 * 提供一些处理 HTTP 请求的公用方法
 * <p>
 * Created by qikai.yu on 2016/4/21.
 */
abstract class AbstractFacadeImpl extends AbstractExceptionCatchedFacadeImpl {
    /**
     * 取得登陆用户信息
     */
    final User getLoginUser(HttpServletRequest request) {
        return (User) request.getAttribute(Constant.AUTHORIZED_USER);
    }


}
