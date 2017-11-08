//package com.sumscope.optimus.moneymarket.model.dto;
//
//import com.sumscope.optimus.moneymarket.model.dbmodel.MatrixQueryCondition;
//import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
//import junit.framework.TestCase;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by qikai.yu on 2016/5/19.
// */
//public class MatrixQueryConditionTest extends TestCase {
//	public void testHashCode() throws Exception {
//		MatrixQueryCondition con1 = new MatrixQueryCondition();
//		con1.setQuoteType(QuoteType.GTF);
//		MatrixQueryCondition con2 = new MatrixQueryCondition();
//		con2.setQuoteType(QuoteType.GTF);
//
//		Map map = new HashMap<>();
//		map.put(con1,"");
//		map.put(con2,"");
//		assertEquals(1,map.size());
//		map.clear();
//
//		con1 = new MatrixQueryCondition();
//		con1.setQuoteType(QuoteType.GTF);
//		con1.addArea("上海");
//		con2 = new MatrixQueryCondition();
//		con2.setQuoteType(QuoteType.GTF);
//		con2.addArea("北京");
//		map.put(con1,"");
//		map.put(con2,"");
//		assertEquals(2,map.size());
//
//	}
//	public void testForAllRegion() throws Exception {
//		MatrixQueryCondition con1 = new MatrixQueryCondition();
//		con1.setQuoteType(QuoteType.GTF);
//		MatrixQueryCondition con2 = new MatrixQueryCondition();
//		con2.setQuoteType(QuoteType.GTF);
//
//		con2.addArea("上海");
//		assertFalse(con1.equals(con2));
//		assertTrue(con1.equalsForAllRegion(con2));
//		assertTrue(con2.equalsForAllRegion(con1));
//
//		con1.addArea("北京");
//		assertFalse(con1.equals(con2));
//		assertFalse(con1.equalsForAllRegion(con2));
//		assertFalse(con2.equalsForAllRegion(con1));
//
//
//	}
//}