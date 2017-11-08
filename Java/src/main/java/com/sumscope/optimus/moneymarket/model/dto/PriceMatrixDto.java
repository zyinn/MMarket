package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.CalculatedBankNature;
import com.sumscope.optimus.moneymarket.commons.enums.MatrixFundSize;

import java.util.List;

/**
 * Created by fan.bai on 2016/9/19.
 * 矩阵计算结果Dto，用于前端显示矩阵数据
 */
public class PriceMatrixDto {
    /**
     * 机构类型枚举
     */
    private CalculatedBankNature matrixBankNature;

    /**
     * 机构规模枚举
     */
    private MatrixFundSize matrixFundSize;

    /**
     * 对应与机构类型与规模的一行数据的cell结果列表
     */
    private List<PriceMatrixCellDto> rowDtoList;

    public CalculatedBankNature getMatrixBankNature() {
        return matrixBankNature;
    }

    public void setMatrixBankNature(CalculatedBankNature matrixBankNature) {
        this.matrixBankNature = matrixBankNature;
    }

    public MatrixFundSize getMatrixFundSize() {
        return matrixFundSize;
    }

    public void setMatrixFundSize(MatrixFundSize matrixFundSize) {
        this.matrixFundSize = matrixFundSize;
    }

    public List<PriceMatrixCellDto> getRowDtoList() {
        return rowDtoList;
    }

    public void setRowDtoList(List<PriceMatrixCellDto> rowDtoList) {
        this.rowDtoList = rowDtoList;
    }
}
