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
import java.util.HashMap;
import javax.swing.JPanel;
import org.bson.Document;

/**
 *
 * @author Bart
 */
public class Klimplant extends AbstractVastePlant implements IFloraSubType {

    private Boolean requiresSupport;
    private OptionalBooleanJCombobox requiresSupportComboBox;

    
    public Klimplant() {
    }

    @Override
    public FloraSubTypeEnum getSubType() {
        return FloraSubTypeEnum.KLIMPLANT;
    }

    public Boolean getRequiresSupport() {
        return requiresSupport;
    }

    public void setRequiresSupport(Boolean requiresSupport) {
        this.requiresSupport = requiresSupport;
    }

    @Override
    public void setSubTypeAttributes(Document doc) {
        super.setSubTypeAttributes(doc);
        if (doc.getBoolean("requiresSupport") != null) {
            setRequiresSupport(doc.getBoolean("requiresSupport"));
        }
    }

    @Override
    public void setSubTypeAttributes(IFloraSubType source) {
        super.setSubTypeAttributes(source);
        if (source == null) {
            return;
        }
        setRequiresSupport(((Klimplant) source).getRequiresSupport());
    }

    @Override
    public HashMap<String, Object> getUpdateAttributesList() {
        HashMap<String, Object> result = super.getUpdateAttributesList();
                
        result.put("requiresSupport", requiresSupportComboBox.getSelectedBooleanValue());

        return result;
    }

    @Override
    public JPanel getSubTypePropertiesPanel() {
        JPanel panel = super.getSubTypePropertiesPanel();

        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        PropertyLabel requiresSupportLabel = new PropertyLabel("heeft ondersteuning nodig");
        requiresSupportComboBox = new OptionalBooleanJCombobox();
        requiresSupportComboBox.setEditable(false);

        if(panel.getComponents().length > 0){
            c.gridy+= panel.getComponents()[panel.getComponents().length -1].getY()+1;            
        }
                
        panel.add(requiresSupportLabel, c);
        c.gridx++;
        panel.add(requiresSupportComboBox, c);

        return panel;
    }

    @Override
    public void refreshSubTypeComponents() {
        super.refreshSubTypeComponents();
        requiresSupportComboBox.setSelectedBooleanValue(requiresSupport);
    }

    @Override
    public void setUpPanelForInterrogation(InterrogationSetup interrS) {
        super.setUpPanelForInterrogation(interrS);
        requiresSupportComboBox.setEnabled(false);
    }

    @Override
    public boolean validateInterrogatedValues() {
        boolean validationOk = super.validateInterrogatedValues();

        if (requiresSupportComboBox.getSelectedBooleanValue() != requiresSupport) {
            validationOk = false;
            requiresSupportComboBox.setBackground(COLOR_VALIDATION_FAILED);
        }

        return validationOk;
    }

    @Override
    public void restoreBGColorForSubTypeComponents() {
        super.restoreBGColorForSubTypeComponents();
        requiresSupportComboBox.setBackground(COLOR_READY_FOR_VALIDATION);
    }

}
