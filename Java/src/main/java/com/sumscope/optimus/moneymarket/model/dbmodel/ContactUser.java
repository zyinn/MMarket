package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.moneymarket.commons.enums.ContactQuoteAttribute;

/**
 * 联系人ContactUser
 */
public class ContactUser extends User{
    //标记 同业理财:FI 线下资金:CD
    private ContactQuoteAttribute quoteAttribute;

    public ContactQuoteAttribute getQuoteAttribute() {
        return quoteAttribute;
    }

    public void setQuoteAttribute(ContactQuoteAttribute quoteAttribute) {
        this.quoteAttribute = quoteAttribute;
    }

}
