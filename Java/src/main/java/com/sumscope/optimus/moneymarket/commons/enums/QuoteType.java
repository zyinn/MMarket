package com.sumscope.optimus.moneymarket.commons.enums;

/**
 * Created by shaoxu.wang on 2016/4/21.
 * GTF = 理财-保本 : Guaranteed Fund(用于同业理财) Interbank Financial Product
 * UR2 = 理财-非保本R2: Unguaranteed R2(用于同业理财) Interbank Financial Product
 * UR3 = 理财-非保本R3: Unguaranteed R3(用于同业理财) Interbank Financial Product
 * IBD = 线下资金 Offline MoneyMarket
 */
public enum QuoteType {
    GTF("保本",ContactQuoteAttribute.FI),
    UR2("非保R2",ContactQuoteAttribute.FI),
    UR3("非保R3",ContactQuoteAttribute.FI),//理财-
    IBD("线下资金",ContactQuoteAttribute.CD);

    private String displayName;
    private ContactQuoteAttribute contactQuoteAttribute;

    QuoteType(String name,ContactQuoteAttribute qa) {
        this.displayName = name;
        this.contactQuoteAttribute = qa;
    }

    public ContactQuoteAttribute getContactQuoteAttribute() {
        return contactQuoteAttribute;
    }

    public void setContactQuoteAttribute(ContactQuoteAttribute contactQuoteAttribute) {
        this.contactQuoteAttribute = contactQuoteAttribute;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 用于判断是否属于线下资金
     * @param type
     * @return
     */
    public static boolean isOfflineMoneyMarket(QuoteType type){
        return type == IBD;
    }
}
