package com.sumscope.optimus.moneymarket.model.dto;

public class MockRequestDto {

    // QQ用户数量
    private int numberOfQQUser;

    // QB用户数量
    private int numberOfQBUser;

    // 每个次发送报价的时间间隔
    private int delayTime;

    // 轮数
    private int round;

    // 每轮间隔时间
    private int roundTime;

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getRoundTime() {
        return roundTime;
    }

    public void setRoundTime(int roundTime) {
        this.roundTime = roundTime;
    }


    public int getNumberOfQQUser() {
        return numberOfQQUser;
    }

    public void setNumberOfQQUser(int numberOfQQUser) {
        this.numberOfQQUser = numberOfQQUser;
    }

    public int getNumberOfQBUser() {
        return numberOfQBUser;
    }

    public void setNumberOfQBUser(int numberOfQBUser) {
        this.numberOfQBUser = numberOfQBUser;
    }
}
