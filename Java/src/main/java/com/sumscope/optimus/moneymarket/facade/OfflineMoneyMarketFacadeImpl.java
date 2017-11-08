package com.sumscope.optimus.moneymarket.facade;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Qikai.Yu on 2016/4/21.
 * 线下资金业务Facade的实现
 */
@RestController
@RequestMapping(value = "/offline", produces = MediaType.APPLICATION_JSON_VALUE)
public class OfflineMoneyMarketFacadeImpl extends AbstractMatrixAndQuoteQueryFacadeImpl {

}
