package com.sumscope.optimus.moneymarket.facade;

import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import com.sumscope.optimus.moneymarket.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    
    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
       
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
            Object arg2, ModelAndView arg3) throws Exception {
        
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse arg1,
            Object arg2) throws Exception {
            if(request.getMethod().equals("OPTIONS")){
                return true;
            }
            // 通过http header取得用户名和密码
            String username = request.getHeader("username");
            String password = request.getHeader("password");
            // 验证用户名和密码是否正确
            User account = authorizationService.authorizeUser(username, password);
            if(account == null){
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.AUTHORIZE_INVALID, "fail to authorize username=" + username + ", password=" + password);
            }
            request.setAttribute(Constant.AUTHORIZED_USER, account);
            return true;
    }

}
