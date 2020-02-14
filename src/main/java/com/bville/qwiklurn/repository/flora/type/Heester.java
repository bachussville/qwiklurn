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
public class Heester extends VastePlant implements IFloraSubType {

    private Boolean hedgeable = null;

    private OptionalBooleanJCombobox hedgeableComboBox;

    public Boolean getHedgeable() {
        return hedgeable;
    }

    public void setHedgeable(Boolean hedgeable) {
        this.hedgeable = hedgeable;
    }

    @Override
    public FloraSubTypeEnum getSubType() {
        return FloraSubTypeEnum.HEESTER;
    }

    @Override
    public void setSubTypeAttributes(Document doc) {
        super.setSubTypeAttributes(doc);
        if (doc.getBoolean("hedgeable") != null) {
            setHedgeable(doc.getBoolean("hedgeable"));
        }
    }

    @Override
    public void setSubTypeAttributes(IFloraSubType source) {
        if (source == null) {
            return;
        }
        setHedgeable(((Heester) source).getHedgeable());
    }

    @Override
    public HashMap<String, Object> getUpdateAttributesList() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("hedgeable", hedgeableComboBox.getSelectedBooleanValue());

        return result;
    }

    @Override
    public JPanel getSubTypePropertiesPanel() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        PropertyLabel hedgeableLabel = new PropertyLabel("Toepasbaar als haag");
        hedgeableComboBox = new OptionalBooleanJCombobox();
        hedgeableComboBox.setEditable(false);

        if (panel.getComponents().length > 0) {
            c.gridy += panel.getComponents()[panel.getComponents().length].getY();
        }

        panel.add(hedgeableLabel, c);
        c.gridx++;
        panel.add(hedgeableComboBox, c);

        return panel;
    }

    @Override
    public void refreshSubTypeComponents() {
        hedgeableComboBox.setSelectedBooleanValue(hedgeable);
    }

    @Override
    public void setUpPanelForInterrogation(InterrogationSetup interrS) {
        hedgeableComboBox.setEnabled(false);

    }

    @Override
    public boolean validateInterrogatedValues() {
        boolean validationOk = true;

        if (hedgeableComboBox.getSelectedBooleanValue() != hedgeable) {
            validationOk = false;
            hedgeableComboBox.setBackground(COLOR_VALIDATION_FAILED);
        }

        return validationOk;
    }

    @Override
    public void restoreBGColorForSubTypeComponents() {
        hedgeableComboBox.setBackground(COLOR_READY_FOR_VALIDATION);
    }

}
