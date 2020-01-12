/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.swing;

import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

/**
 *
 * @author Bart
 */
public class InterrogationSetup extends JFrame {

    private final InterrogationSetup meReference;
    private final StartUp startUpFrame;
    public boolean testCommonName = true;
    public boolean testFunction = true;
    public boolean testSoilType = true;
    public boolean testSolarType = true;
    public boolean testLeafage = true;
    public boolean testTreeShape = true;

    public InterrogationSetup(StartUp s) throws HeadlessException {
        meReference = this;
        startUpFrame = s;
        init();
    }

    private void init() {

        setTitle("How to you want to be interrogated ?");
        setLayout(new GridBagLayout());

        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        JCheckBox interrCommonName = new JCheckBox("interrogate common name ? ", testCommonName);
        interrCommonName.addActionListener(l -> {
            testCommonName = interrCommonName.isSelected();
        });

        JCheckBox interrFunction = new JCheckBox("interrogate function ? ", testFunction);
        interrFunction.addActionListener(l -> {
            testFunction = interrFunction.isSelected();
        });
        
        JCheckBox interrSoilType = new JCheckBox("interrogate soil type ? ", testSoilType);
        interrSoilType.addActionListener(l -> {
            testSoilType = interrSoilType.isSelected();
        });

        JCheckBox interrSolarType = new JCheckBox("interrogate solar type ? ", testSolarType);
        interrSolarType.addActionListener(l -> {
            testSolarType = interrSolarType.isSelected();
        });

        JCheckBox interrLeafage = new JCheckBox("interrogate leafage ? ", testLeafage);
        interrLeafage.addActionListener(l -> {
            testLeafage = interrLeafage.isSelected();
        });

        JCheckBox interrTreeShape = new JCheckBox("interrogate tree shape ? ", testTreeShape);
        interrTreeShape.addActionListener(l -> {
            testTreeShape = interrTreeShape.isSelected();
        });

        JButton launch = new JButton("Launch");
        launch.addActionListener(e -> {
            dispose();
            startUpFrame.interrogationSetupComplete(meReference);
        });

        getContentPane().add(interrCommonName, c);

        c.gridy++;
        getContentPane().add(interrSoilType, c);

        c.gridy++;
        getContentPane().add(interrSolarType, c);

        c.gridy++;
        getContentPane().add(interrLeafage, c);
        
        c.gridy++;
        getContentPane().add(interrTreeShape, c);

        
        
        c.gridy++;
        getContentPane().add(launch, c);

        JFrameUtils.centerComponent(this);

    }

}
