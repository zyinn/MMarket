package com.sumscope.optimus.moneymarket.model.dto;

/**
 * Created by fan.bai on 2016/5/18.
 * 用户验证响应DTO
 */
public class ValidationUserResponseDto {
    private String userID;
    private String username;
    /**
     * 加密后的密码
     */
    private String password;
    private boolean success;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
