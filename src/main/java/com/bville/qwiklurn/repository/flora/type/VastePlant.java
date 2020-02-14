/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora.type;

import com.bville.qwiklurn.repository.flora.FloraSubTypeEnum;
import com.bville.qwiklurn.repository.flora.OptionalBooleanJCombobox;
import static com.bville.qwiklurn.repository.flora.type.AbstractFlora.COLOR_READY_FOR_VALIDATION;
import static com.bville.qwiklurn.repository.flora.type.AbstractFlora.COLOR_VALIDATION_FAILED;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.swing.DefaultGridBagConstraints;
import com.bville.qwiklurn.swing.InterrogationSetup;
import com.bville.qwiklurn.swing.PropertyLabel;
import java.awt.GridBagLayout;
import java.util.HashMap;
import javax.swing.JPanel;
import org.bson.Document;

/**
 *
 * @author Bart
 */
abstract class VastePlant extends AbstractVastePlant implements IFloraSubType {

    public VastePlant() {
    }

    @Override
    public void setSubTypeAttributes(Document doc) {
    }

    @Override
    public void setSubTypeAttributes(IFloraSubType source) {
    }

    @Override
    public HashMap<String, Object> getUpdateAttributesList() {
        return new HashMap<String, Object>();
    }

    @Override
    public JPanel getSubTypePropertiesPanel() {
        return new JPanel();
    }

    @Override
    public void refreshSubTypeComponents() {
    }

    @Override
    public void setUpPanelForInterrogation(InterrogationSetup interrS) {
    }

    @Override
    public boolean validateInterrogatedValues() {
        return true;
    }

    @Override
    public void restoreBGColorForSubTypeComponents() {
    }

}
