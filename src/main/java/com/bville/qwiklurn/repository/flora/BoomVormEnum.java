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
public enum BoomVormEnum implements CodeDescrEnum {

    BOL("rond", "appelvormig"),
    KEGEL("kegel", "kegelvormig"),
    LANGWERPIG("langwerpig", "langwerpig"),
    GRILLIG("grillig", "grillig"),
    ;

    private String code;
    private String description;

    public static BoomVormEnum parse(String code) {
        return (BoomVormEnum) EnumUtil.parse(code, values());
    }

    BoomVormEnum(String code, String desc) {
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
