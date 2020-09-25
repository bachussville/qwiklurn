/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora.type;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.swing.DefaultGridBagConstraints;
import com.bville.qwiklurn.swing.InterrogationSetup;
import com.bville.qwiklurn.swing.PropertyLabel;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.bson.Document;

/**
 *
 * @author Bart
 */
abstract class AbstractVastePlant extends AbstractFlora implements IFloraSubType {

    private int plantafstand;

    private JTextField plantafstandText;

    public AbstractVastePlant() {
    }

    public int getPlantafstand() {
        return plantafstand;
    }

    public void setPlantafstand(int plantafstand) {
        this.plantafstand = plantafstand;
    }

    @Override
    public void setSubTypeAttributes(Document doc) {
        if (doc.getInteger("plantafstand") != null) {
            setPlantafstand(doc.getInteger("plantafstand"));
        }
    }

    @Override
    public void setSubTypeAttributes(IFloraSubType source) {
        if (source == null) {
            return;
        }
        setPlantafstand(((AbstractVastePlant) source).getPlantafstand());
    }

    @Override
    public HashMap<String, Object> getUpdateAttributesList() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("plantafstand", Integer.parseInt(plantafstandText.getText().trim()));

        return result;
    }

    @Override
    public JPanel getSubTypePropertiesPanel() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        PropertyLabel plantafstandLabel = new PropertyLabel("plantafstand");
        plantafstandText = new JTextField();
        plantafstandText.setPreferredSize(new Dimension(35, 20));

        if (panel.getComponents().length > 0) {
            c.gridy += panel.getComponents()[panel.getComponents().length].getY();
        }

        panel.add(plantafstandLabel, c);
        c.gridx++;
        panel.add(plantafstandText, c);

        return panel;
    }

    @Override
    public void refreshSubTypeComponents() {
        plantafstandText.setText(""+plantafstand);
    }

    @Override
    public void setUpPanelForInterrogation(InterrogationSetup interrS) {
        plantafstandText.setEnabled(false);
    }

    @Override
    public boolean validateInterrogatedValues() {
        boolean validationOk = true;

        if (!plantafstandText.getText().trim().equalsIgnoreCase(""+plantafstand)) {
            validationOk = false;
            plantafstandText.setBackground(COLOR_VALIDATION_FAILED);
        }

        return validationOk;
    }

    @Override
    public void restoreBGColorForSubTypeComponents() {
        plantafstandText.setBackground(COLOR_READY_FOR_VALIDATION);
    }

}
