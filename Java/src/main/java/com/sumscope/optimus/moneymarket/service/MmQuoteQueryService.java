package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.moneymarket.commons.enums.MethodType;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;

import java.util.List;
import java.util.Map;

/**
 * Created by fan.bai on 2016/8/6.
 * 报价单相关查询服务
 */
public interface MmQuoteQueryService {
    /**
     * 查询报价详细信息，详细信息包括报价主表信息及报价明细表信息，但是不包括报价单标签信息
     * 该方法一次性从数据库中读取明细及主表信息效率较高，比较适合用于计算获取数据
     *
     * @param parameters 查询参数
     * @return List<MmQuoteDetailsQueryResult>
     */
    List<MmQuoteDetailsQueryResult> queryMmQuoteDetails(MmQuoteDetailsQueryParameters parameters);

    /**
     * 以报价明细数据为一行查询报价单及详细表信息，并将数据转换为MmQuote模式。该查询与同名方法使用相同的方式从数据库读取，
     * 既一次性从数据库中读取详细及主表信息。在分页逻辑允许的情况下（既可以按明细数据进行分页）建议使用该方法获取查询结果。
     * 获取的查询结果将转换为MmQuote数据模式，该模式在涉及到单一报价单呈现的相关业务时比较容易处理。模式转换在service
     * 内部处理。
     * 根据参数还可确定是否获取相关的报价单标签信息。
     * @param parameters 查询参数
     * @param withTags true 对返回的MmQuote读取其标签信息放入相应字段
     * @return 以MmQuote模式呈现的查询结果
     */
    List<MmQuote> queryMmQuoteDetails(MmQuoteDetailsQueryParameters parameters, boolean withTags);

    /**
     * 以报价单主表数据为行查询报价单信息。该查询以查询主表为基础，根据参数决定是否获取对应的明细及标签数据。该查询与
     * 上述查询不同点在于分页逻辑，本查询使用主表数据为分页基础，而上诉两个查询则以明细表数据为分页基础。在没有分页要求
     * 的情况下应使用queryMmQuoteDetails（）方法获取数据，效率更高。
     * 与明细查询相比，该查询支持按某一个期限价格进行排序的功能，该功能和页面显示逻辑相关。
     * @param parameters MmQuoteMainQueryParameters
     * @param withDetails true 获取结果集中报价单明细数据
     * @param withTags true 获取结果集中报价单标签数据
     * @return MmQuote列表
     */
    List<MmQuote> queryMmQuoteMain(MmQuoteMainQueryParameters parameters, boolean withDetails,boolean withTags);

    /**
     * 获取输入用户ID进行的报价单列表，该列表内的报价单拥有报价明细及标签数据。
     * 该方法使用与queryMmQuoteDetails一样的数据库读取语句一次性读取报价，再根据结果一一获取对应的标签数据
     * 本方法可以使用queryMmQuoteDetails（）实现相同功能，但是为了使用方便特此增加。
     * @param userId 用户ID
     * @return 该用户的所有仍在有效期内的报价数据，包含报价明细及标签数据
     */
    List<MmQuote> queryMyValidQuotes(String userId,MethodType methodType);

    /**
     * 获取输入机构的有效报价单列表，该列表内的报价单拥有报价明细及标签数据。
     * 该方法使用与queryMmQuoteDetails一样的数据库读取语句一次性读取报价，再根据结果一一获取对应的标签数据
     * 本方法可以使用queryMmQuoteDetails（）实现相同功能，但是为了使用方便特此增加。
     * @param institutionIdList 机构ID列表，列表为null或空时不处理
     * @return 该用户的所有仍在有效期内的报价数据，包含报价明细及标签数据。若参数为null或空则返回一个无数据的列表
     */
    List<MmQuote> queryValidQuotesByInstitutions(List<String> institutionIdList);

    /**
     * 获取报价单ID列表获取报价单列表，该列表内的报价单拥有报价明细及标签数据。
     * 该方法使用与queryMmQuoteDetails一样的数据库读取语句一次性读取报价，再根据结果一一获取对应的标签数据
     * 本方法可以使用queryMmQuoteDetails（）实现相同功能，但是为了使用方便特此增加。
     * @param quoteIdList 报价单ID列表，列表为null或空时不处理
     * @return 对应输入ID列表的所有报价单（有效及无效），包含报价明细及标签数据。若参数为null或空则返回一个无数据的列表
     */
    List<MmQuote> queryMmQuotesByIdList(List<String> quoteIdList);

    Map<String,User> retrieveQuoteUsers();

    Map<String,Institution> retrieveQuoteInstitutions();

    /**
     * 获取 机构用户mapping关系
     * @return
     */
    Map<String,UserPreference> retrieveInstitutionUserMappingByUserID();

    /**
     * 根据机构id获取UserId
     * @return
     */
    Map<String, String> retrieveAllUserIdMappingByInstitutionId(String userId);
}
