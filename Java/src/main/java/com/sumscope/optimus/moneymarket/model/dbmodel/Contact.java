package com.sumscope.optimus.moneymarket.model.dbmodel;

import com.sumscope.optimus.moneymarket.commons.enums.ContactQuoteAttribute;

/**
 * 表 mm_prime_contact_x
 */
public class Contact {
    private String institutionId;
    private String contactId;
    private ContactQuoteAttribute quoteAttribute;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public ContactQuoteAttribute getQuoteAttribute() {
        return quoteAttribute;
    }

    public void setQuoteAttribute(ContactQuoteAttribute quoteAttribute) {
        this.quoteAttribute = quoteAttribute;
    }

}
