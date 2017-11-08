package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan.bai on 2016/6/7.
 * 根据参数生成报价单方法列表
 */
@Service
public class QuoteMethodsConverter {
    /**
     * 生成某用户可选的报价方法列表。普通报价仅在用户没有联盟报价及中介报价权限时赋予。即普通用户一定有普通报价权限，该权限不再写入用户权限表
     * @param isValidForAllianceQuote 是否有联盟报价权限
     * @param isValidForBrokerQuote 是否有中介报价权限
     * @return 可用的报价方法列表。若既没有联盟报价也没有中介报价权限，则自动获得普通报价权限。
     */
    public List<MethodType> convertQuoteMethods(boolean isValidForAllianceQuote, boolean isValidForBrokerQuote){
        List<MethodType> methods = new ArrayList<MethodType>();

        if(!isValidForAllianceQuote && !isValidForBrokerQuote){
            methods.add(MethodType.SEF);
        }
        if (isValidForBrokerQuote) {
            methods.add(MethodType.BRK);
        }
        if (isValidForAllianceQuote){
            methods.add(MethodType.ALC);
        }
            return methods;
    }
}
