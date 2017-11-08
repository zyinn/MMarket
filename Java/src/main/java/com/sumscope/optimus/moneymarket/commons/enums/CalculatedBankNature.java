package com.sumscope.optimus.moneymarket.commons.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaoxu.wang on 2016/4/22.
 * 用于查询的5个机构类型。其中“其他”表明所有不是国有大行，股份制，城商行以及农信机构的机构类型。
 */
public enum CalculatedBankNature {
    SHIBOR("0", "Shibor"),
    /**
     *
     */
    BIG_BANK("1", "国有大行"),
    /**
     *
     */
    JOINT_STOCK("3", "股份制"),
    /**
     *
     */
    CITY_COMMERCIAL_BANK("4", "城商行"),
    /**
     *
     */
    RURAL_CREDIT("6", "农信机构"),
    /**
     *
     */
    OTHERS(null, "其他");

    private static final Map<String, CalculatedBankNature> STRINGTOENUM = new HashMap<String, CalculatedBankNature>();

    static {
        // Initialize map from constant name to enum constant
        for (CalculatedBankNature value : values()) {
            STRINGTOENUM.put(value.getBankNatureCode(), value);
        }
    }

    /**
     * bank_nature代码，业务主键
     */
    private String bankNatureCode;
    private String displayName;

    CalculatedBankNature(String value, String name) {
        this.bankNatureCode = value;
        this.displayName = name;
    }

    // Returns Blah for string, or null if string is invalid
    public static CalculatedBankNature fromCode(String symbol) {
        CalculatedBankNature ot = STRINGTOENUM.get(symbol);
        if (ot == null) {
            ot = OTHERS;
        }
        return ot;
    }


    public String getDisplayName() {
        return displayName;
    }

    public String getBankNatureCode() {
        return bankNatureCode;
    }
}
