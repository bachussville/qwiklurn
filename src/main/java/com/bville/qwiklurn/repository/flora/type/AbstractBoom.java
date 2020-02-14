/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora.type;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.LeafageEnum;
import com.bville.qwiklurn.repository.flora.TreeShapeEnum;
import com.bville.qwiklurn.swing.DefaultGridBagConstraints;
import com.bville.qwiklurn.swing.InterrogationSetup;
import com.bville.qwiklurn.swing.PropertyLabel;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.bson.Document;

/**
 *
 * @author Bart
 */
abstract class AbstractBoom extends AbstractFlora implements IFloraSubType {

    private LeafageEnum leafage = null;
    private TreeShapeEnum treeShape = null;
    
    private JComboBox<LeafageEnum> leafageComboBox;
    private JComboBox<TreeShapeEnum> treeShapeComboBox;

    protected AbstractBoom(Boolean defaultWinterLeaves) {
        super(defaultWinterLeaves);
        this.leafageComboBox = new JComboBox<>();
        this.treeShapeComboBox = new JComboBox<>();
    }

    public LeafageEnum getLeafage() {
        return leafage;
    }

    public void setLeafage(LeafageEnum leafage) {
        this.leafage = leafage;
    }

    public TreeShapeEnum getTreeShape() {
        return treeShape;
    }

    public void setTreeShape(TreeShapeEnum treeShape) {
        this.treeShape = treeShape;
    }

    @Override
    public void setSubTypeAttributes(Document doc) {
        if (doc.get("leafage", String.class) != null) {
            setLeafage(LeafageEnum.parse(doc.get("leafage", String.class)));
        }
        if (doc.get("treeShape", String.class) != null) {
            setTreeShape(treeShape.parse(doc.get("treeShape", String.class)));
        }


    }

    @Override
    public JPanel getSubTypePropertiesPanel() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        PropertyLabel leafageLabel = new PropertyLabel("Type gebladerte");
        leafageComboBox = new JComboBox<>();
        leafageComboBox.setEditable(false);
        for (int i = 0; i < LeafageEnum.values().length; i++) {
            leafageComboBox.insertItemAt(LeafageEnum.values()[i], i);
        }

        PropertyLabel shapeLabel = new PropertyLabel("Vorm");
        treeShapeComboBox = new JComboBox<>();
        treeShapeComboBox.setEditable(false);
        for (int i = 0; i < TreeShapeEnum.values().length; i++) {
            treeShapeComboBox.insertItemAt(TreeShapeEnum.values()[i], i);
        }

        panel.add(leafageLabel, c);
        c.gridx++;
        panel.add(leafageComboBox, c);

        c.gridy++;
        c.gridx = 0;
        panel.add(shapeLabel, c);
        c.gridx++;
        panel.add(treeShapeComboBox, c);

        return panel;
    }

    @Override
    public void refreshSubTypeComponents() {
        if (leafage != null) {
            leafageComboBox.setSelectedItem(leafage);
        }
        if (treeShape != null) {
            treeShapeComboBox.setSelectedItem(treeShape);
        }

    }

    @Override
    public void setSubTypeAttributes(IFloraSubType source) {
        if(source == null){
            return;
        }
        
        if (source instanceof AbstractBoom) {
            leafage = ((AbstractBoom) source).getLeafage();
            treeShape = ((AbstractBoom) source).getTreeShape();
        }

    }

    @Override
    public HashMap<String, Object> getUpdateAttributesList() {
        HashMap<String, Object> result = new HashMap<>();
        if (leafageComboBox.getSelectedItem() != null) {
            result.put("leafage", ((LeafageEnum) leafageComboBox.getSelectedItem()).getCode());
        }
        if (treeShapeComboBox.getSelectedItem() != null) {
            result.put("treeShape", ((TreeShapeEnum) treeShapeComboBox.getSelectedItem()).getCode());
        }

        return result;
    }

    @Override
    public void setUpPanelForInterrogation(InterrogationSetup interrogationSetup) {

        if (interrogationSetup.testLeafage) {
            leafageComboBox.setSelectedIndex(-1);
        }else{
            leafageComboBox.setEnabled(false);
        }
        
        if(interrogationSetup.testTreeShape){
            treeShapeComboBox.setSelectedIndex(-1);
        }else{
            treeShapeComboBox.setEnabled(false);
        }

    }

    @Override
    public boolean validateInterrogatedValues() {
        boolean validationOk = true;

        if (leafage != leafageComboBox.getSelectedItem()) {
            validationOk = false;
            leafageComboBox.setBackground(Color.YELLOW);
        }

        if (treeShape != treeShapeComboBox.getSelectedItem()) {
            validationOk = false;
            treeShapeComboBox.setBackground(Color.YELLOW);
        }

        return validationOk;
    }

    @Override
    public void restoreBGColorForSubTypeComponents() {
        if (leafageComboBox != null) {
            leafageComboBox.setBackground(Color.WHITE);
        }
        if (treeShapeComboBox != null) {
            treeShapeComboBox.setBackground(Color.WHITE);
        }        
    }

    
}
