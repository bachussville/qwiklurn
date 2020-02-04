/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.swing;

import com.bville.qwiklurn.repository.flora.DbManager;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.type.Tree;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author Bart
 */
public class StartUp extends JFrame {

    private final DbManager dbMgr; 
    private final StartUp meReference;

    public StartUp(DbManager dbM) throws HeadlessException {
        dbMgr = dbM;
        meReference = this;
        init();
    }

    private void init() {

        setTitle("Choose your mood");
        setLayout(new FlowLayout());
        JButton addNew = new JButton("Add new Flora");
        addNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Detail d = new Detail(dbMgr, "Add new Flora", ActionType.ADD_NEW, null);
                    List<IFloraSubType> dataList = new ArrayList<>();
                    dataList.add(new Tree());
                    d.setDataList(dataList);
                    d.refreshDataList(0, null, true);
                    d.setVisible(true);
                } catch (HeadlessException ex) {
                    Logger.getLogger(StartUp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(StartUp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JButton study = new JButton("Study..");
        study.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    StudyList d = new StudyList(dbMgr, ActionType.STUDY, null);
                    d.alertDefaultQueryActivated();
                    d.setVisible(true);
                } catch (HeadlessException ex) {
                    Logger.getLogger(StartUp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JButton interrogate = new JButton("Test..");
        interrogate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    InterrogationSetup interrogationSetup = new InterrogationSetup(meReference);
                    interrogationSetup.setVisible(true);
                } catch (HeadlessException ex) {
                    Logger.getLogger(StartUp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        getContentPane().add(addNew);
        getContentPane().add(study);
        getContentPane().add(interrogate);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JFrameUtils.centerComponent(this);

    }

    void interrogationSetupComplete(InterrogationSetup iSetup) {
        try {
            StudyList d = new StudyList(dbMgr,ActionType.INTERROGATION, iSetup);
            d.alertDefaultQueryActivated();
            d.setVisible(true);
        } catch (HeadlessException ex) {
            Logger.getLogger(StartUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
