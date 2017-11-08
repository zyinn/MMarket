package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by shaoxu.wang on 2016/4/22.
 * 报价时间区间
 */
public enum QuoteTimePeriod {
    T1D("1D", 1, 1, ContactQuoteAttribute.CD, ContactQuoteAttribute.FI),

    T7D("7D",0,7,ContactQuoteAttribute.CD),

    T14D("14D",8,14,ContactQuoteAttribute.CD),

    T1M("1M",15,30,ContactQuoteAttribute.CD, ContactQuoteAttribute.FI),

    T2M("2M",31,60,ContactQuoteAttribute.CD, ContactQuoteAttribute.FI),

    T3M("3M",61,90,ContactQuoteAttribute.CD, ContactQuoteAttribute.FI),

    T6M("6M",91,180,ContactQuoteAttribute.CD, ContactQuoteAttribute.FI),

    T9M("9M",181,270,ContactQuoteAttribute.CD, ContactQuoteAttribute.FI),

    T1Y("1Y",271,360,ContactQuoteAttribute.CD, ContactQuoteAttribute.FI);

    private String displayName;
    /**
     * 期限下区间
     */
    private int daysLow;
    /**
     * 期限上区间
     */
    private int daysHigh;
    private ContactQuoteAttribute[] quoteAttributes;

    QuoteTimePeriod(String name, int low, int high,ContactQuoteAttribute... qAttributes) {
        this.displayName = name;
        this.daysHigh = high;
        this.daysLow = low;
        this.quoteAttributes = qAttributes;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getDaysLow() {
        return daysLow;
    }

    public int getDaysHigh() {
        return daysHigh;
    }

    /**
     * 判断当前期限是否可用于理财
     * @return true 是， false 不是
     */
    public boolean isFISupported(){
        return isAttributeSupported(ContactQuoteAttribute.FI);
    }

    /**
     * @param supportedAttribute ContactQuoteAttribute
     * @return true 支持该类型
     */
    public boolean isAttributeSupported(ContactQuoteAttribute supportedAttribute) {
        for(ContactQuoteAttribute cAttribute: quoteAttributes){
            if( cAttribute == supportedAttribute){
                return true;
            }
        }
        return false;
    }
    /**
     * 判断当前期限是否可用于同存
     * @return true 是， false 不是
     */
    public boolean isCDSupported(){
        return isAttributeSupported(ContactQuoteAttribute.CD);
    }
}
