package com.sumscope.optimus.moneymarket.commons.exception;

import com.sumscope.optimus.commons.exceptions.ExceptionType;

/**
 *
 * 异常代码:
 *  可以按需增加, 建议在后期统一梳理所有异常类型之后再添加, 目前遇到异常可以直接先 增加 todo 标签
 *
 *
 * Created by qikai.yu on 2016/4/26.
 */
public enum MMExceptionCode implements ExceptionType {
    QM_STATUS_ERROR("E4001","QM总线错误"),
    EXCEL_ERROR("E5001","Excel解析异常");

    MMExceptionCode(String code,String info) {
        this.code = code;
        this.errorInfoCN = info;
    }

    private String code;
    private String errorInfoCN;

    @Override
    public String getExceptionCode() {
        return code;
    }

    @Override
    public String getExceptionInfoCN() {
        return errorInfoCN;
    }
}
