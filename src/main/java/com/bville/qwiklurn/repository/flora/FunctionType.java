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
public enum FunctionType  implements CodeDescrEnum{
    MASS("m", "groenmassa"),
    DECO("d", "decoratief"),
    EDIBLE("e", "eetbaar"),
    BIO("b", "biostimulans")
;

    public static FunctionType parse(String code) {
        if(code.equalsIgnoreCase(MASS.getCode())) return MASS;
        if(code.equalsIgnoreCase(DECO.getCode())) return DECO;
        if(code.equalsIgnoreCase(EDIBLE.getCode())) return EDIBLE;
        if(code.equalsIgnoreCase(BIO.getCode())) return BIO;

        throw new RuntimeException("Unsupported code for FunctionType: " + code);

    }

    private String code;
    private String description;

    FunctionType(String code, String desc) {
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

}
