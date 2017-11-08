package com.sumscope.optimus.moneymarket.facade;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by qikai.yu on 2016/4/21.
 * 同业理财业务Facade的实现
 */
@RestController
@RequestMapping(value = "/inner", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterbankFinancialProductFacadeImpl extends AbstractMatrixAndQuoteQueryFacadeImpl {

}
