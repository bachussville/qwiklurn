/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.*;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import org.bson.Document;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Bart
 */
public enum FloraSubTypeEnum {
    KLIMPLANT("Klimplant", Klimplant.class),
    BODEMBEDEKKER("Bodembedekker", Bodembedekker.class),
    MEERJARIG("Meerjarige plant", MeerjarigePlant.class),
    EENJARIG("Eenjarige plant", EenjarigePlant.class),
    HEESTER("Heester", Heester.class),
    LOOFBOOM("Loofboom", Loofboom.class),
    NAALDBOOM("Naaldboom", Naaldboom.class),
    CONIFEER("Conifeer", Conifeer.class),
    ;

    private String code;
    private String descr;
    private Class classImpl;

    FloraSubTypeEnum(String code, Class defaultImpl) {
        this.code = code;
        this.descr = code;
        this.classImpl = defaultImpl;
    }

    public String getCode() {
        return code;
    }

    public String getDescr() {
        return descr;
    }

    public IFloraSubType getInstance(Document content, DbManager dbMgr) {
        try {
            IFloraSubType elem = (IFloraSubType) classImpl.getDeclaredConstructor().newInstance();
            ((AbstractFlora) elem).setAttributes(content, dbMgr);
            elem.setSubTypeAttributes(content);
            return elem;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public IFloraSubType getInstance(IFloraSubType content) {
        try {
            IFloraSubType elem = (IFloraSubType) classImpl.getDeclaredConstructor().newInstance();
            ((AbstractFlora) elem).setAttributes(content, true);

            elem.setSubTypeAttributes(content.getClass() == elem.getClass() ? content : null);
            return elem;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static FloraSubTypeEnum parse(String code) {
        Optional<FloraSubTypeEnum> optionalFloraSubTypeEnum = Arrays.stream(values()).filter(e -> e.getCode().equalsIgnoreCase(code)).findFirst();
        if (optionalFloraSubTypeEnum.isPresent()) {
            return optionalFloraSubTypeEnum.get();
        }

        throw new RuntimeException("Unsupported code for FloraSubType: " + code);

    }

    @Override
    public String toString() {
        return getDescr();
    }

}
