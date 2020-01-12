/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.swing;

import java.awt.Dimension;
import javax.swing.JLabel;

/**
 *
 * @author Bart
 */
public class PropertyLabel extends JLabel{

    public PropertyLabel() {
        super();
        this.init();
    }

    public PropertyLabel(String text) {
        super(text);
        this.init();
    }
    
    private void init(){
        setPreferredSize(new Dimension(150,30));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());

    }
    
    
}
