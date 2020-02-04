/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora.type;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.FloraSubTypeEnum;
import com.bville.qwiklurn.repository.flora.OptionalBooleanJCombobox;
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
public class LastingPlant extends GenericFlora implements IFloraSubType {

    private Boolean groundCoverable = null;
    private Boolean spreadsEasily = null;

    private OptionalBooleanJCombobox groundCoverableComboBox;
    private OptionalBooleanJCombobox spreadsEasilyComboBox;

    public Boolean getGroundCoverable() {
        return groundCoverable;
    }

    public void setGroundCoverable(Boolean groundCoverable) {
        this.groundCoverable = groundCoverable;
    }

    public Boolean getSpreadsEasily() {
        return spreadsEasily;
    }

    public void setSpreadsEasily(Boolean spreadsEasily) {
        this.spreadsEasily = spreadsEasily;
    }

    @Override
    public FloraSubTypeEnum getSubType() {
        return FloraSubTypeEnum.BUSH;
    }

    @Override
    public void setSubTypeAttributes(Document doc) {
        if (doc.getBoolean("groundCoverable") != null) {
            setGroundCoverable(doc.getBoolean("groundCoverable"));
        }
        if (doc.getBoolean("spreadsEasily") != null) {
            setSpreadsEasily(doc.getBoolean("spreadsEasily"));
        }
    }

    @Override
    public void setSubTypeAttributes(IFloraSubType source) {
        if (source == null) {
            return;
        }
        setGroundCoverable(((LastingPlant) source).getGroundCoverable());
        setSpreadsEasily(((LastingPlant) source).getSpreadsEasily());
    }

    @Override
    public HashMap<String, Object> getUpdateAttributesList() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("groundCoverable", groundCoverableComboBox.getSelectedBooleanValue());
        result.put("spreadsEasily", spreadsEasilyComboBox.getSelectedBooleanValue());

        return result;
    }

    @Override
    public JPanel getSubTypePropertiesPanel() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        PropertyLabel groundCoverableLabel = new PropertyLabel("is een bodembedekker");
        groundCoverableComboBox = new OptionalBooleanJCombobox();
        groundCoverableComboBox.setEditable(false);

        PropertyLabel spreadsEasilyLabel = new PropertyLabel("Verspreid zich makkelijk");
        spreadsEasilyComboBox = new OptionalBooleanJCombobox();
        spreadsEasilyComboBox.setEditable(false);

        panel.add(groundCoverableLabel, c);
        c.gridx++;
        panel.add(groundCoverableComboBox, c);

        c.gridy++;
        c.gridx = 0;
        panel.add(spreadsEasilyLabel, c);
        c.gridx++;
        panel.add(spreadsEasilyComboBox, c);

        return panel;
    }

    @Override
    public void refreshSubTypeComponents() {
        groundCoverableComboBox.setSelectedBooleanValue(groundCoverable);
        spreadsEasilyComboBox.setSelectedBooleanValue(spreadsEasily);
    }

    @Override
    public void setUpPanelForInterrogation(InterrogationSetup interrS) {
        groundCoverableComboBox.setEnabled(false);
        spreadsEasilyComboBox.setEnabled(false);
    }

    @Override
    public boolean validateInterrogatedValues() {
        boolean validationOk = true;

        if (groundCoverableComboBox.getSelectedBooleanValue() != groundCoverable) {
            validationOk = false;
            groundCoverableComboBox.setBackground(COLOR_VALIDATION_FAILED);
        }

        if (spreadsEasilyComboBox.getSelectedBooleanValue() != spreadsEasily) {
            validationOk = false;
            spreadsEasilyComboBox.setBackground(COLOR_VALIDATION_FAILED);
        }

        return validationOk;
    }

    @Override
    public void restoreBGColorForSubTypeComponents() {
        groundCoverableComboBox.setBackground(COLOR_READY_FOR_VALIDATION);
        spreadsEasilyComboBox.setBackground(COLOR_READY_FOR_VALIDATION);
    }

}
