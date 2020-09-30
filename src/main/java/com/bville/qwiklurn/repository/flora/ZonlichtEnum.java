/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

/**
 * @author Bart
 */
public enum ZonlichtEnum implements CodeDescrEnum {
    SCHADUW("schaduw", "schaduw"),
    HALFSCHADUW("halfschaduw", "halfschaduw"),
    ZON("zon", "zon");

    private String code;
    private String description;

    ZonlichtEnum(String code, String d) {
        this.code = code;
        this.description = d;
    }

    public static ZonlichtEnum parse(String code) {
        return (ZonlichtEnum) EnumUtil.parse(code, values());
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
