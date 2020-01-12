/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.swing;

import javax.swing.JFrame;

/**
 *
 * @author Bart
 */
public class JFrameUtils {
    
    public static void centerComponent(JFrame c){
        c.pack();
        c.setLocationRelativeTo(null);
    }
}
