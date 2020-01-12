/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.swing;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Bart
 */
    class ImmutableTableModel extends DefaultTableModel {

        public ImmutableTableModel(Object[][] rowData, String[] columnNames) {
            super(columnNames, 0);
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

    }
