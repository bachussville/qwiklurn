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
public class Bodembedekker extends AbstractVastePlant implements IFloraSubType {

    private Boolean uitlopend;
    private OptionalBooleanJCombobox uitlopendComboBox;

    public Boolean getuitlopend() {
        return uitlopend;
    }

    public void setuitlopend(Boolean uitlopend) {
        this.uitlopend = uitlopend;
    }

    public OptionalBooleanJCombobox getuitlopendComboBox() {
        return uitlopendComboBox;
    }

    public void setuitlopendComboBox(OptionalBooleanJCombobox uitlopendComboBox) {
        this.uitlopendComboBox = uitlopendComboBox;
    }

    
    @Override
    public FloraSubTypeEnum getSubType() {
        return FloraSubTypeEnum.BODEMBEDEKKER;
    }

    @Override
    public void setSubTypeAttributes(Document doc) {
        super.setSubTypeAttributes(doc);
        if (doc.getBoolean("uitlopend") != null) {
            setuitlopend(doc.getBoolean("uitlopend"));
        }
    }

    @Override
    public void setSubTypeAttributes(IFloraSubType source) {
        if(source == null){
            return;
        }
        setuitlopend(((Bodembedekker) source).getuitlopend());
    }

    @Override
    public HashMap<String, Object> getUpdateAttributesList() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uitlopend", uitlopendComboBox.getSelectedBooleanValue());

        return result;
    }

    @Override
    public JPanel getSubTypePropertiesPanel() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        PropertyLabel uitlopendLabel = new PropertyLabel("uitlopend");
        uitlopendComboBox = new OptionalBooleanJCombobox();
        uitlopendComboBox.setEditable(false);

        if (panel.getComponents().length > 0) {
            c.gridy += panel.getComponents()[panel.getComponents().length].getY();
        }
        
        panel.add(uitlopendLabel, c);
        c.gridx++;
        panel.add(uitlopendComboBox, c);

        return panel;
    }

    @Override
    public void refreshSubTypeComponents() {
        uitlopendComboBox.setSelectedBooleanValue(uitlopend);
    }

    @Override
    public void setUpPanelForInterrogation(InterrogationSetup interrS) {
        uitlopendComboBox.setEnabled(false);

    }

    @Override
    public boolean validateInterrogatedValues() {
        boolean validationOk = true;

        if (uitlopendComboBox.getSelectedBooleanValue() != uitlopend) {
            validationOk = false;
            uitlopendComboBox.setBackground(COLOR_VALIDATION_FAILED);
        }

        return validationOk;
    }

    @Override
    public void restoreBGColorForSubTypeComponents() {
        uitlopendComboBox.setBackground(COLOR_READY_FOR_VALIDATION);
    }

    
}
