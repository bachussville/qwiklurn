/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora.type;

import com.bville.qwiklurn.repository.flora.FloraSubTypeEnum;
import com.bville.qwiklurn.repository.flora.OptionalBooleanJCombobox;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.swing.DefaultGridBagConstraints;
import com.bville.qwiklurn.swing.InterrogationSetup;
import com.bville.qwiklurn.swing.PropertyLabel;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 *
 * @author Bart
 */
public class MeerjarigePlant extends AbstractVastePlant implements IFloraSubType {
    @Override
    public FloraSubTypeEnum getSubType() {
        return FloraSubTypeEnum.MEERJARIG;
    }

    @Override
    public void setSubTypeAttributes(Document doc) {
        super.setSubTypeAttributes(doc);
    }

    @Override
    public void setSubTypeAttributes(IFloraSubType source) {
    }

    @Override
    public HashMap<String, Object> getUpdateAttributesList() {
        HashMap<String, Object> result = new HashMap<>();
        return result;
    }

    @Override
    public JPanel getSubTypePropertiesPanel() {
        JPanel panel = new JPanel();
        return panel;
    }

    @Override
    public void refreshSubTypeComponents() {

    }

    @Override
    public void setUpPanelForInterrogation(InterrogationSetup interrS) {
    }

    @Override
    public boolean validateInterrogatedValues() {
        boolean validationOk = true;
        return validationOk;
    }

    @Override
    public void restoreBGColorForSubTypeComponents() {

    }

    
}
