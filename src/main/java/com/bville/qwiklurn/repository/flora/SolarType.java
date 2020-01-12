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
public enum SolarType implements CodeDescrEnum {
    SCHADUW("s", "schaduw"),
    HALFSCHADUW("hs", "halfschaduw"),
    ZON("z", "zon");

    private String code;
    private String description;

    SolarType(String code, String d) {
        this.code = code;
        this.description = d;
    }

    public static SolarType parse(String code) {
        if (code.equalsIgnoreCase(SCHADUW.getCode())) {
            return SCHADUW;
        }
        if (code.equalsIgnoreCase(HALFSCHADUW.getCode())) {
            return HALFSCHADUW;
        }
        if (code.equalsIgnoreCase(ZON.getCode())) {
            return ZON;
        }

        throw new RuntimeException("Unsupported code for SolarType: " + code);

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
