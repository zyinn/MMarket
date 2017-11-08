package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.CachedDataContainer;
import com.sumscope.optimus.commons.cachemanagement.SSCacheResolver;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.model.dbmodel.MmQuote;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by fan.bai on 2016/9/23.
 * 报价价格极值的矩阵计算相对比较消耗时间，它需要查询出所有有效报价并逐条比较报价明细取出价格对于几种参数（报价方向，
 * 报价类型等）的极限数值。因此我们使用@CacheMe标注进行缓存。当报价单没有发生变动时只需要计算一次即可。
 *
 * 当报价单发生变动时，wsandlocalmessage包内的处理程序将从总线监听到变动的报价单并触发相应的缓存更新处理。本类用于判断
 * 当前缓存是否需要更新。根据报价单的方向和类型，比较缓存方法对应的参数中的报价方向和类型，来确定该报价单的更新是否
 * 可能造成矩阵结果的改变。如果报价方向和类型与缓存方法对应的参数一致，则说明接收到的报价单有可能引起对应矩阵结果的改变，
 * 为简化处理，我们直接将缓存设置为脏，这样在下一次方法调用时将进行缓存的更新。
 *
 * 前端页面每分钟刷新一次矩阵结果，当报价非常频繁时，缓存数据将被不断更新。由于我们使用非同步模式更新缓存，因此有可能
 * 调用者取得的还是脏的数据。在某些情况极端情况下，可能出现某些调用者一直无法获取到最新的矩阵结果(总是差一分钟)。
 * 但是这种情况出现的可能性很低，因为不是每一次新的报价都造成矩阵结果的更新。
 */
public class QuoteMatrixCacheResolver implements SSCacheResolver<MmQuote> {
    private static Set<Class> acceptedResultsClass = new HashSet<>();

    static {
        acceptedResultsClass.add(MmQuote.class);
    }
    @Override
    public boolean isCacheable(Object[] params) {
        return true;
    }

    @Override
    public void updateCache(CachedDataContainer container, MmQuote convertedChangedData, String source, Object[] params) {
        //设置为脏，要求下一次矩阵计算重新进行
        System.out.println("报价单更新触发矩阵更新！" + source);
        if(Constant.QUOTE_CREATED.equals(source) ){
            container.setDirty(true);
        }

    }

    @Override
    public boolean isUpdateNeeded(MmQuote convertedChangedData, Object[] params) {
        if(params != null && params.length == 3){
            //对应的方法应为：calculateMatrixCells(Direction direction, QuoteType quoteType, Date time)
            Direction direction = (Direction) params[0];
            QuoteType quoteType = (QuoteType) params[1];
            return (convertedChangedData.getQuoteType() == quoteType && convertedChangedData.getDirection() == direction);
        }
        return false;
    }

    @Override
    public MmQuote convertToCachedType(Object changedData) {
        return (MmQuote)changedData;
    }

    @Override
    public Set<Class> acceptedResultTypes() {
        return acceptedResultsClass;
    }
}
