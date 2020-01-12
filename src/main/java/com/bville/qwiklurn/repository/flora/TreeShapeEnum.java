/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import java.util.Arrays;

/**
 *
 * @author Bart
 */
public enum TreeShapeEnum implements CodeDescrEnum {

    BROAD("b", "vol (breed vertakt)"),
    ROUND("r", "appelvormig"),
    PEAR("p", "peervormig"),
    PIRAMID("pi", "kegelvormig"),
    ELIPS_V("ev", "verticale elips");

    private String code;
    private String description;

    public static TreeShapeEnum parse(String code) {
        return Arrays.asList(TreeShapeEnum.values()).stream().filter(x -> {
            return x.getCode().equalsIgnoreCase(code);
        }).findFirst().get();
    }

    TreeShapeEnum(String code, String desc) {
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
