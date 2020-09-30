/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

/**
 * @author Bart
 */
public enum GebladerteEnum implements CodeDescrEnum {

    GESLOTEN("gesloten", "weinig doorlatend"),
    DOORLATEND("gemiddeld", "matig doorlatend"),
    OPEN("open", "ruim doorlatend");

    private String code;
    private String description;

    public static GebladerteEnum parse(String code) {
        return (GebladerteEnum) EnumUtil.parse(code, values());
    }


    GebladerteEnum(String code, String desc) {
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
