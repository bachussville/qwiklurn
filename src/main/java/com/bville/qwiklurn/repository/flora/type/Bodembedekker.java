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
public class Bodembedekker extends VastePlant implements IFloraSubType {

    private Boolean zelfVullend;
    private OptionalBooleanJCombobox zelfVullendComboBox;

    public Boolean getZelfVullend() {
        return zelfVullend;
    }

    public void setZelfVullend(Boolean zelfVullend) {
        this.zelfVullend = zelfVullend;
    }

    public OptionalBooleanJCombobox getZelfVullendComboBox() {
        return zelfVullendComboBox;
    }

    public void setZelfVullendComboBox(OptionalBooleanJCombobox zelfVullendComboBox) {
        this.zelfVullendComboBox = zelfVullendComboBox;
    }

    
    @Override
    public FloraSubTypeEnum getSubType() {
        return FloraSubTypeEnum.BODEMBEDEKKER;
    }

    @Override
    public void setSubTypeAttributes(Document doc) {
        super.setSubTypeAttributes(doc);
        if (doc.getBoolean("zelfVullend") != null) {
            setZelfVullend(doc.getBoolean("zelfVullend"));
        }
    }

    @Override
    public void setSubTypeAttributes(IFloraSubType source) {
        if(source == null){
            return;
        }
        setZelfVullend(((Bodembedekker) source).getZelfVullend());
    }

    @Override
    public HashMap<String, Object> getUpdateAttributesList() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("zelfVullend", zelfVullendComboBox.getSelectedBooleanValue());

        return result;
    }

    @Override
    public JPanel getSubTypePropertiesPanel() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        PropertyLabel zelfVullendLabel = new PropertyLabel("zelfvullend");
        zelfVullendComboBox = new OptionalBooleanJCombobox();
        zelfVullendComboBox.setEditable(false);

        if (panel.getComponents().length > 0) {
            c.gridy += panel.getComponents()[panel.getComponents().length].getY();
        }
        
        panel.add(zelfVullendLabel, c);
        c.gridx++;
        panel.add(zelfVullendComboBox, c);

        return panel;
    }

    @Override
    public void refreshSubTypeComponents() {
        zelfVullendComboBox.setSelectedBooleanValue(zelfVullend);
    }

    @Override
    public void setUpPanelForInterrogation(InterrogationSetup interrS) {
        zelfVullendComboBox.setEnabled(false);

    }

    @Override
    public boolean validateInterrogatedValues() {
        boolean validationOk = true;

        if (zelfVullendComboBox.getSelectedBooleanValue() != zelfVullend) {
            validationOk = false;
            zelfVullendComboBox.setBackground(COLOR_VALIDATION_FAILED);
        }

        return validationOk;
    }

    @Override
    public void restoreBGColorForSubTypeComponents() {
        zelfVullendComboBox.setBackground(COLOR_READY_FOR_VALIDATION);
    }

    
}
