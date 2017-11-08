//package com.sumscope.optimus.moneymarket.model.dbmodel;
//
//import com.sumscope.optimus.moneymarket.commons.enums.CalculatedBankNature;
//import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;
//import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
//import junit.framework.TestCase;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Administrator on 2016/5/9.
// */
//public class MatrixPriceTest extends TestCase {
//    public void testUpdatePriceData() throws Exception {
//
//    }
//
//    public void testGetMatrixUnitForUpdate() throws Exception {
//        MatrixPrice matrixPrice = new MatrixPrice();
//
//        List<MatrixPriceUnit> obj = matrixPrice.getMatrixUnitForUpdate(CalculatedBankNature.BIG_BANK, 15, 30);
//        assertEquals(1, obj.size());
//        assertEquals(QuoteTimePeriod.T1M.name(), obj.get(0).getPeriod());
//
//        obj = matrixPrice.getMatrixUnitForUpdate(CalculatedBankNature.BIG_BANK, -2, -1);
//        assertEquals(0, obj.size());
//
//        obj = matrixPrice.getMatrixUnitForUpdate(CalculatedBankNature.BIG_BANK, 20, 50);
//        assertEquals(2, obj.size());
//        assertEquals(QuoteTimePeriod.T1M.name(), obj.get(0).getPeriod());
//        assertEquals(QuoteTimePeriod.T2M.name(), obj.get(1).getPeriod());
//        assertEquals(CalculatedBankNature.BIG_BANK.name(), obj.get(1).getType());
//
//        obj = matrixPrice.getMatrixUnitForUpdate(CalculatedBankNature.BIG_BANK, 1, 365);
//        assertEquals(QuoteTimePeriod.values().length, obj.size());
//        assertEquals(QuoteTimePeriod.T7D.name(), obj.get(0).getPeriod());
//        assertEquals(QuoteTimePeriod.T14D.name(), obj.get(1).getPeriod());
//        assertEquals(QuoteTimePeriod.T1M.name(), obj.get(2).getPeriod());
//        assertEquals(QuoteTimePeriod.T2M.name(), obj.get(3).getPeriod());
//        assertEquals(QuoteTimePeriod.T3M.name(), obj.get(4).getPeriod());
//        assertEquals(QuoteTimePeriod.T6M.name(), obj.get(5).getPeriod());
//        assertEquals(QuoteTimePeriod.T9M.name(), obj.get(6).getPeriod());
//        assertEquals(QuoteTimePeriod.T1Y.name(), obj.get(7).getPeriod());
//
//    }
//
//    public void testUpdatePriceDataWithDaysBothNull() throws Exception {
//        MatrixPrice matrixPrice = new MatrixPrice();
//
//        MmQuoteForMatrixCalculation calculation = getMmQuoteForMatrixCalculation("1",QuoteType.UR2,null,null,1.0,1.0);
//
//        matrixPrice.updatePriceData(calculation);
//        matrixPrice.generateOriginalMatrix();
//    }
//
//    public void testUpdatePriceDataWithHighDaysNull() throws Exception {
//        MatrixPrice matrixPrice = new MatrixPrice();
//
//        MmQuoteForMatrixCalculation calculation = getMmQuoteForMatrixCalculation("1",QuoteType.UR2,1,null,1.0,2.0);
//
//        matrixPrice.updatePriceData(calculation);
//        MatrixPriceUnit unit = matrixPrice.generateOriginalMatrix()[0][0];
//        assertEquals(1.0, unit.getMin());
//        assertEquals(2.0, unit.getMax());
//    }
//
//    public void testUpdatePriceDataWithHighPriceNull() throws Exception {
//        MatrixPrice matrixPrice = new MatrixPrice();
//
//        MmQuoteForMatrixCalculation calculation = getMmQuoteForMatrixCalculation("1",QuoteType.UR2,1,null,1.0,null);
//
//        matrixPrice.updatePriceData(calculation);
//        MatrixPriceUnit unit = matrixPrice.generateOriginalMatrix()[0][0];
//        assertEquals(1.0, unit.getMin());
//        assertEquals(1.0, unit.getMax());
//    }
//
//    public void testUpdatePriceDataWithNegativePrice() throws Exception {
//        MatrixPrice matrixPrice = new MatrixPrice();
//
//        MmQuoteForMatrixCalculation calculation1 = getMmQuoteForMatrixCalculation("1",QuoteType.UR2,1,null,-1.0,null);
//        MmQuoteForMatrixCalculation calculation2 = getMmQuoteForMatrixCalculation("1",QuoteType.UR2,1,null,-25.0,null);
//        MmQuoteForMatrixCalculation calculation3 = getMmQuoteForMatrixCalculation("1",QuoteType.UR2,1,null,1.0,null);
//
//        matrixPrice.updatePriceData(calculation1);
//        matrixPrice.updatePriceData(calculation2);
//        matrixPrice.updatePriceData(calculation3);
//        MatrixPriceUnit unit = matrixPrice.generateOriginalMatrix()[0][0];
//        assertEquals(-25.0, unit.getMin());
//        assertEquals(1.0, unit.getMax());
//    }
//
//
//    public void testUpdateMatrixUnit(){
//        MatrixPrice matrixPrice = new MatrixPrice();
//        MatrixPriceUnit unit = new MatrixPriceUnit(CalculatedBankNature.BIG_BANK,QuoteTimePeriod.T1M);
//        double oldMin = 1.0;
//        double oldMax = 16.0;
//        unit.setMax(oldMax);
//        unit.setMin(oldMin);
//        List<MatrixPriceUnit> l = new ArrayList<>();
//        l.add(unit);
//        double newMin = 0.5;
//        double newMax = 17.0;
//        matrixPrice.updateMatrixUnit(l,newMin,newMax);
//        assertEquals(unit.getMin() , newMin);
//        assertEquals(unit.getMax() , newMax);
//    }
//
//    public void testDaysWithMoreThanOneYear(){
//        MatrixPrice matrixPrice = new MatrixPrice();
//        List<MatrixPriceUnit> units = matrixPrice.getMatrixUnitForUpdate(CalculatedBankNature.BIG_BANK,120,240);
//        assertEquals(2, units.size());
//        units = matrixPrice.getMatrixUnitForUpdate(CalculatedBankNature.BIG_BANK,271,360);
//        assertEquals(1, units.size());
//        units = matrixPrice.getMatrixUnitForUpdate(CalculatedBankNature.BIG_BANK,299,350);
//        assertEquals(1, units.size());
//        units = matrixPrice.getMatrixUnitForUpdate(CalculatedBankNature.BIG_BANK,351,400);
//        assertEquals(1, units.size());
//        units = matrixPrice.getMatrixUnitForUpdate(CalculatedBankNature.BIG_BANK,361,400);
//        assertEquals(0, units.size());
//    }
//
//
//
//    private MmQuoteForMatrixCalculation getMmQuoteForMatrixCalculation(
//            String bankNature, QuoteType quoteType,
//            Integer daysLow, Integer daysHigh, Double priceLow, Double priceHigh) {
//        MmQuoteForMatrixCalculation calculation = new MmQuoteForMatrixCalculation();
//        MmQuoteDetailsQueryResult detail = new MmQuoteDetailsQueryResult();
//        calculation.setMmQuoteDetailsQueryResult(detail);
//        calculation.setBankNature(bankNature);
//        detail.setDaysHigh(daysHigh);
//        detail.setDaysLow(daysLow);
//        detail.setPrice(priceHigh!=null?new BigDecimal(priceHigh):null);
//        detail.setPriceLow(priceLow!=null?new BigDecimal(priceLow):null);
//        detail.setQuoteType(quoteType);
//        return calculation;
//    }
//
//}