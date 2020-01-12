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
public enum SpecialsType implements CodeDescrEnum{
    BLAD_KLEUR("lc", "bladkleur"),
    BLAD_VORM("ls", "bladvorm"),
    BLAD_TEXTUUR("lt", "bladtextuur"),
    BLOEM_KLEUR("ls", "bloemkleur"),
    BLOEM_VORM("ls", "bloemvorm"),
    GEUR("s", "geur"),    
    BIO_DIVERSITEIT("s", "bio diversiteit"),    
    EETBAAR("s", "eetbare delen"),    
    ANDERE("o", "andere"),    
    ;

    public static SpecialsType parse(String code) {
        if(code.equalsIgnoreCase(BLAD_KLEUR.getCode())) return BLAD_KLEUR;
        if(code.equalsIgnoreCase(BLAD_VORM.getCode())) return BLAD_VORM;
        if(code.equalsIgnoreCase(BLAD_TEXTUUR.getCode())) return BLAD_TEXTUUR;
        if(code.equalsIgnoreCase(BLOEM_KLEUR.getCode())) return BLOEM_KLEUR;
        if(code.equalsIgnoreCase(BLOEM_VORM.getCode())) return BLOEM_VORM;
        if(code.equalsIgnoreCase(GEUR.getCode())) return GEUR;
        if(code.equalsIgnoreCase(BIO_DIVERSITEIT.getCode())) return BIO_DIVERSITEIT;
        if(code.equalsIgnoreCase(EETBAAR.getCode())) return EETBAAR;
        if(code.equalsIgnoreCase(ANDERE.getCode())) return ANDERE;

        throw new RuntimeException("Unsupported code for SpecialsType: " + code);

    }

    private final String code;
    private final String description;

    SpecialsType(String code, String desc) {
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
