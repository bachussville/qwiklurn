/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.type.BushClass;
import com.bville.qwiklurn.repository.flora.type.FloraClass;
import com.bville.qwiklurn.repository.flora.type.TreeClass;
import org.bson.Document;

/**
 *
 * @author Bart
 */
public enum FloraSubTypeEnum {
    BUSH("h", "heester", BushClass.class),
    TREE("b", "boom", TreeClass.class);

    private String code;
    private String descr;
    private Class classImpl;

    FloraSubTypeEnum(String code, String name, Class defaultImpl) {
        this.code = code;
        this.descr = name;
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
            ((FloraClass)elem).setAttributes(content, dbMgr);
            elem.setSubTypeAttributes(content);
            return elem;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public IFloraSubType getInstance(IFloraSubType content) {
        try {
            IFloraSubType elem = (IFloraSubType) classImpl.getDeclaredConstructor().newInstance();
            ((FloraClass)elem).setAttributes(content, true);
            
            elem.setSubTypeAttributes(content.getClass() == elem.getClass() ? content : null);
            return elem;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    

    public static FloraSubTypeEnum parse(String code) {
        if (code.equalsIgnoreCase(BUSH.getCode())) {
            return BUSH;
        }
        if (code.equalsIgnoreCase(TREE.getCode())) {
            return TREE;
        }

        throw new RuntimeException("Unsupported code for FloraSubType: " + code);

    }

    @Override
    public String toString() {
        return getDescr();
    }

}
