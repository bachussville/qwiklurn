/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

/**
 * @author Bart
 */
public enum FunctieEnum implements CodeDescrEnum {
    VULLING("vulling", "groenmassa"),
    DECO("decoratief", "decoratief"),
    EETBAAR("eetbaar", "eetbaar"),
    BIO("biostimuli", "biostimulans");

    public static FunctieEnum parse(String code) {
        return (FunctieEnum) EnumUtil.parse(code, values());
    }

    private String code;
    private String description;

    FunctieEnum(String code, String desc) {
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
