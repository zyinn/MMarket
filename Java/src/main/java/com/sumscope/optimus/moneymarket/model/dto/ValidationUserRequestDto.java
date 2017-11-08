package com.sumscope.optimus.moneymarket.model.dto;

/**
 * Created by fan.bai on 2016/5/18.
 * 用于用户验证的请求DTO
 */
public class ValidationUserRequestDto {
    private String username;
    private String plainPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }
}
