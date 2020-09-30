/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author Bart
 */
public enum OpvallendeEigenschapEnum implements CodeDescrEnum{
    BLAD_KLEUR("bladkleur", "bladkleur"),
    BLAD_VORM("bladvorm", "bladvorm"),
    BLAD_TEXTUUR("bladtextuur", "bladtextuur"),
    BLOEM_KLEUR("bloemkleur", "bloemkleur"),
    BLOEM_VORM("bloemvorm", "bloemvorm"),
    BLOEM_AANTAL("bloemenaantal", "veel bloemen"),
    SNIJBLOEM("snijbloem", "snijbloem"),
    GEUR("geur", "geur"),
    BIO_DIVERSITEIT("biodiv", "bio diversiteit"),
    EETBAAR("eetbaar", "eetbare delen"),
    ;

    public static OpvallendeEigenschapEnum parse(String code) {
        return (OpvallendeEigenschapEnum) EnumUtil.parse(code, values());
    }

    private final String code;
    private final String description;

    OpvallendeEigenschapEnum(String code, String desc) {
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
