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
public enum TreePruneEnum implements CodeDescrEnum {
    
    NONE("n", "Geen snoei", "Geen snoei vereist"),
    FRUIT_PRUNING("f", "Typische fruitsnoei", "Jaarlijks bijsnoeien vanaf de 3de knoop\nOpgelet voor de vingers."),
 
    ;
    
    private final String code;
    private final String shortDescription;
    private final String longDescription;

    public static TreePruneEnum parse(String code) {
        return Arrays.asList(TreePruneEnum.values()).stream().filter(x -> {
            return x.getCode().equalsIgnoreCase(code);
        }).findFirst().get();
    }

    TreePruneEnum(String code, String shortDesc, String longDesc) {
        this.code = code;
        this.shortDescription = shortDesc;
        this.longDescription = longDesc;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public String getDescription() {
        return getLongDescription();
    }

    
    @Override
    public String toString() {
        return getShortDescription();
    }    
    
}
