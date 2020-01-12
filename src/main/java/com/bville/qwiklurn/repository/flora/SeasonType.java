/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

/**
 *
 * @author Bart
 */
public enum SeasonType implements CodeDescrEnum {
    VOORJAAR_VROEG("se", "vroeg voorjaar (jan -> maart)"),
    VOORJAAR("s", "voorjaar (jan -> juni"),
    ZOMER("s", "zomer (juni -> aug)"),
    HERFST_VROEG("fe", "vroege herfst (sept -> okt)"),
    HERFST("f", "herfst (sept -> november)"),
    WINTER("w", "winter (nov -> febr)");

    public static SeasonType parse(String code) {
        if (code.equalsIgnoreCase(VOORJAAR_VROEG.getCode())) {
            return VOORJAAR_VROEG;
        }
        if (code.equalsIgnoreCase(VOORJAAR.getCode())) {
            return VOORJAAR;
        }
        if (code.equalsIgnoreCase(ZOMER.getCode())) {
            return ZOMER;
        }
        if (code.equalsIgnoreCase(HERFST_VROEG.getCode())) {
            return HERFST_VROEG;
        }
        if (code.equalsIgnoreCase(HERFST.getCode())) {
            return HERFST;
        }
        if (code.equalsIgnoreCase(WINTER.getCode())) {
            return WINTER;
        }

        throw new RuntimeException("Unsupported code for SeasonType: " + code);

    }

    private final String code;
    private final String description;

    SeasonType(String code, String desc) {
        this.code = code;
        this.description = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
