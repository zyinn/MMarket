package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;
import com.sumscope.optimus.moneymarket.model.dbmodel.UserPreference;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by fan.bai on 2016/8/30.
 * 用于业务数据库的报价单管理服务
 */
public interface BusinessMmQuoteManagementService extends MmQuoteManagementService {
    @Override
    @Transactional(value = Constant.BUSINESS_TRANSACTION_MANAGER)
    void setupMmQuotesInTransaction(List<MmQuote> mmQuoteList, Source source);

    @Transactional(value = Constant.BUSINESS_TRANSACTION_MANAGER)
    void deleteQuotesFromDB(List<String> successList);

    /**
     * 报价单主表仍未作废，但是其中明细数据可能已经失效，比如对于有效期7天的报价单，其中对T1D进行了报价。
     * 根据期限定义，该明细数据仅有效1天。因此过期后需要将这些明细数据作废。
     * 该方法由定时器定时调用，应在每日晚上调用
     */
    @Transactional(value = Constant.BUSINESS_TRANSACTION_MANAGER)
    void deactivateExpiredMmQuoteDetails();

    /**
     * 报价时报存 机构--联系人对应的mapping关系
     * @param list
     */
    @Transactional(value = Constant.BUSINESS_TRANSACTION_MANAGER)
    void setupInstitutionUserMappingTransaction(List<UserPreference> list);


    /**
     * 更新 机构用户mapping关系
     */
    void updateInstitutionUserMapping(List<UserPreference> userPreferences);
}
