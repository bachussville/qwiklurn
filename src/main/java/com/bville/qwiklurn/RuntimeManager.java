/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn;

import com.bville.qwiklurn.repository.flora.DbManager;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 *
 * @author Bart
 */
public class RuntimeManager {

    public static Environment activeEnvironment;
    public static DbManager dbMgr;

    public static enum Environment {
        TEST,
        DEV,
        PROD
    }

    private static Color backgroundColor = Color.MAGENTA;
    private static Color foregroundColor = Color.YELLOW;
    private static Color activeCaption = Color.RED;
    private static Color activeCaptionText = Color.GREEN;

    public static void setDevColors(Environment e) {
        if (e == Environment.PROD) {
            backgroundColor = Color.BLACK;
            foregroundColor = Color.RED;
        } else {
            backgroundColor = Color.ORANGE;
            foregroundColor = Color.BLACK;
        }
    }

    public static void applyColors(JComponent c) {
        c.setBackground(backgroundColor);
        c.setForeground(foregroundColor);
    }

    public static void setupFor(Environment e) {
        switch (e) {
            case TEST:
                dbMgr = new DbManager("QwiklurnTest");
                break;
            case DEV:
                dbMgr = new DbManager("QwiklurnDev");
                break;
            case PROD:
                dbMgr = new DbManager("Qwiklurn");
                break;
        }

        dbMgr.setUpDatabase();
        setDevColors(e);
        updateDefaultColors();
    }

    private static void updateDefaultColors() {
        UIDefaults uiDefaults = UIManager.getDefaults();
        uiDefaults.put("activeCaption", new javax.swing.plaf.ColorUIResource(foregroundColor));
        uiDefaults.put("activeCaptionText", new javax.swing.plaf.ColorUIResource(backgroundColor));
        JFrame.setDefaultLookAndFeelDecorated(true);
    }

}
