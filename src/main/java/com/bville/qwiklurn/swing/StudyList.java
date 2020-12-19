/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.swing;

import com.bville.qwiklurn.repository.flora.DbManager;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.RowSorter.SortKey;

import org.bson.types.ObjectId;

/**
 * @author Bart
 */
public class StudyList extends JFrame {

    private final StudyList meReference;
    DbManager dbMgr;
    private JTable table;
    private ImmutableTableModel tModel;
    boolean defaultQuery = true;
    List<IFloraSubType> dataList;

    private final ActionType actionType;
    private Criteriator criteriator;
    private InterrogationSetup interrogationSetup;

    public void refreshData() {
        if (defaultQuery) {

            List<IFloraSubType> newList = dbMgr.listFloraTypesAlphabetically();
            if (actionType == ActionType.STUDY) {
                table.getRowSorter().setSortKeys(Arrays.asList(new SortKey(1, SortOrder.ASCENDING)));
            }
            if (actionType == ActionType.INTERROGATION) {
                table.getRowSorter().setSortKeys(Arrays.asList(new SortKey(4, SortOrder.ASCENDING)));
            }
            resetTableData(tModel, newList);

        } else {
            resetTableData(tModel, criteriator.runQuery(dbMgr));
        }

        repaint();
    }

    void alertCustomQuerySaved(String queryText) {
        defaultQuery = false;
        refreshData();
    }

    void alertDefaultQueryActivated() {
        defaultQuery = true;
        refreshData();
    }

    public StudyList(DbManager dbM, ActionType actionType, InterrogationSetup iSetup) throws HeadlessException {
        this.dbMgr = dbM;
        this.meReference = this;
        this.actionType = actionType;
        this.interrogationSetup = iSetup;
        init();
    }

    private void init() {
        setTitle("Data Explorer");

        criteriator = new Criteriator(this);

        String[] cols = {"ID", "Latijnse naam", "Vlaamse naam", "Laatste wijziging", "Laatste ondervraging"};
        Object[][] data = new Object[0][5];

        JButton queryButton = new JButton("Customize query..");
        queryButton.addActionListener((ActionEvent e) -> {
            criteriator.buildCriteria();
        });
        JButton defaultButton = new JButton("Default query");
        defaultButton.addActionListener((ActionEvent e) -> {
            alertDefaultQueryActivated();
        });

        tModel = new ImmutableTableModel(data, cols);
        dataList = dbMgr.listFloraTypesAlphabetically();

        resetTableData(tModel, dataList);

        table = getTable(tModel);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(800, 800));

        setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        getContentPane().add(queryButton, c);

        c.gridx++;
        getContentPane().add(defaultButton, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        getContentPane().add(scroll, c);

        JFrameUtils.centerComponent(this);

    }

    private void resetTableData(ImmutableTableModel tModel, List<IFloraSubType> newData) {
        dataList = newData;
        DateTimeFormatter fm = DateTimeFormatter.ISO_DATE_TIME;

        for (int i = 0; i < tModel.getRowCount(); ) {
            tModel.removeRow(0);
        }

        dataList.forEach(d -> {
            String[] dataRow = new String[5];
            int colId = 0;

            dataRow[colId] = d.getId().toHexString();
            dataRow[++colId] = d.getLatinName();
            dataRow[++colId] = d.getCommonName();

            if (d.getModificationDate() != null) {
                LocalDateTime date = LocalDateTime.ofInstant(d.getModificationDate().toInstant(), ZoneId.systemDefault());
                dataRow[++colId] = date.format(fm);
            } else {

            }
            if (d.getInterrogationDate() != null) {
                LocalDateTime date = LocalDateTime.ofInstant(d.getInterrogationDate().toInstant(), ZoneId.systemDefault());
                dataRow[++colId] = date.format(fm);
            } else {

            }
            tModel.addRow(dataRow);

        });

    }

    private JTable getTable(ImmutableTableModel tModel) {
        JTable table = new JTable(tModel);
        //table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == KeyEvent.VK_DELETE) {

                    List<ObjectId> oids = Arrays.stream(table.getSelectedRows())
                            .map(r -> {return table.getRowSorter().convertRowIndexToModel(r);})
                            .mapToObj(r -> {
                        return dataList.get(r).getId();
                    }).collect(Collectors.toList());

                    oids.forEach(oid -> {
                        dbMgr.deleteFloraById(oid);
                        dataList.removeIf(x -> x.getId().equals(oid));
                    });

                    resetTableData(tModel, dataList);
                }
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
        table.addMouseListener(new MouseListener() {
                                   @Override
                                   public void mouseClicked(MouseEvent e) {
                                       if (e.getClickCount() == 2) {
                                           try {
                                               Detail det = new Detail(dbMgr, "Detail", actionType, interrogationSetup);
                                               det.setDataList(dataList);

                                               List<IFloraSubType> sortedDataList = new ArrayList(dataList.size());
                                               for (int i = 0; i < dataList.size(); i++) {
                                                   int modelIndex = table.getRowSorter().convertRowIndexToModel(i);
                                                   sortedDataList.add(dataList.get(modelIndex));
                                               }
                                               det.setDataList(sortedDataList);

                                               det.refreshDataList(table.getSelectedRow(), 0, false);
                                               det.setVisible(true);
                                               det.addWindowListener(new WindowListener() {
                                                                         public void windowOpened(WindowEvent e) {
                                                                         }

                                                                         public void windowClosing(WindowEvent e) {

                                                                         }

                                                                         public void windowClosed(WindowEvent e) {
                                                                             meReference.refreshData();
                                                                         }

                                                                         public void windowIconified(WindowEvent e) {
                                                                         }

                                                                         public void windowDeiconified(WindowEvent e) {
                                                                         }

                                                                         public void windowActivated(WindowEvent e) {
                                                                         }

                                                                         public void windowDeactivated(WindowEvent e) {
                                                                         }
                                                                     }
                                               );
                                           } catch (HeadlessException | IOException ex) {
                                               Logger.getLogger(StudyList.class
                                                       .getName()).log(Level.SEVERE, null, ex);
                                           }
                                       }
                                   }

                                   @Override

                                   public void mousePressed(MouseEvent e) {
                                   }

                                   @Override
                                   public void mouseReleased(MouseEvent e) {
                                   }

                                   @Override
                                   public void mouseEntered(MouseEvent e) {
                                   }

                                   @Override
                                   public void mouseExited(MouseEvent e) {
                                   }
                               }
        );

        table.setAutoCreateRowSorter(true);
        table.removeColumn(table.getColumnModel().getColumn(0));
        table.getColumnModel().getColumn(0).setPreferredWidth(600);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(350);
        table.getColumnModel().getColumn(3).setPreferredWidth(350);

        if (actionType == ActionType.STUDY) {
            table.removeColumn(table.getColumnModel().getColumn(3));
        }
        if (actionType == ActionType.INTERROGATION) {
            table.removeColumn(table.getColumnModel().getColumn(1));
        }

        final StudyList meReference = this;
        table.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    meReference.refreshData();
                }
            }
        });

        return table;
    }

}
