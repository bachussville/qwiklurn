/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import javax.swing.JComboBox;

/**
 *
 * @author Bart
 */
public class OptionalBooleanJCombobox extends JComboBox<OptionalBooleanJCombobox.ValueEnum> {

    public enum ValueEnum {

        NONE(null, "?"),
        YES(Boolean.TRUE, "Ja"),
        NO(Boolean.FALSE, "Nee");

        private Boolean booleanValue;
        private String description;

        private ValueEnum(Boolean value, String description) {
            this.booleanValue = value;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public Boolean getBooleanValue() {
            return booleanValue;
        }

        @Override
        public String toString() {
            return getDescription(); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public OptionalBooleanJCombobox() {
        addItem(ValueEnum.NONE);
        addItem(ValueEnum.YES);
        addItem(ValueEnum.NO);
    }

    public Boolean isValueSet() {
        return ((ValueEnum) getSelectedItem()) != ValueEnum.NONE;
    }

    public Boolean getSelectedBooleanValue() {
        return ((ValueEnum) getSelectedItem()).getBooleanValue();
    }

    public String getSelectedDescription() {
        return ((ValueEnum) getSelectedItem()).getDescription();
    }

    public void setSelectedBooleanValue(Boolean v) {
        if (v == null) {
            setSelectedIndex(0);
            return;
        }
        if (v.booleanValue()) {
            setSelectedIndex(1);
        } else {
            setSelectedIndex(2);
        }
    }

}
