/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora.type;

import com.bville.qwiklurn.repository.flora.FloraSubTypeEnum;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;

/**
 *
 * @author Bart
 */
public class Loofboom extends AbstractBoom implements IFloraSubType {

    public Loofboom() {
        super(Boolean.FALSE);
    }

    @Override
    public FloraSubTypeEnum getSubType() {
        return FloraSubTypeEnum.LOOFBOOM;
    }    
}
