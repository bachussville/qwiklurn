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
public enum LeafageEnum implements CodeDescrEnum{

    GESLOTEN("wd", "weinig doorlatend"),
    DOORLATEND("md", "matig doorlatend"),
    OPEN("rd", "ruim doorlatend");

    private String code;
    private String description;

    public static LeafageEnum parse(String code) {
        if (code.equalsIgnoreCase(GESLOTEN.getCode())) {
            return GESLOTEN;
        }
        if (code.equalsIgnoreCase(DOORLATEND.getCode())) {
            return DOORLATEND;
        }
        if (code.equalsIgnoreCase(OPEN.getCode())) {
            return OPEN;
        }

        throw new RuntimeException("Unsupported code for GebladerteDichtheidType: " + code);

    }



    LeafageEnum(String code, String desc) {
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
