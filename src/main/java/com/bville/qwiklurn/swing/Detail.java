/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.swing;

import com.bville.qwiklurn.repository.flora.*;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;

import java.awt.*;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * @author Bart
 */
public class Detail extends JFrame {

    public static Detail meReference;

    private static DbManager dbMgr;
    private ImageZoom zoom;
    private JScrollPane scrollPane;
    private JPanel photoPanel;
    private JPanel customPanel;
    private JPanel soilPanel, functionPanel;
    private ImagePanel image;
    private JComboBox<FloraSubTypeEnum> FloraSubTypeList;
    private JComboBox<Species> speciesCombo;
    private JComboBox<Project> projectCombo;
    private OptionalBooleanJCombobox winterLeafCombo;
    private ImmutableTableModel specialPropsModel;
    private JTable specialProps;
    private JButton newButton, saveButton, saveAsButton, validateButton, nextFloraButton, previousFloraButton, previousMediaButton, nextMediaButton;
    private JTextField latinNameText, commonNameText, maxHeightText, maxWidthText;
    private JLabel refName;
    private final Map<CodeDescrEnum, JCheckBox> functionTypeCheckboxes = new TreeMap();
    private final Map<GroupedCodeDescrEnum, JCheckBox> soilTypeCheckboxes = new TreeMap();
    private final Map<CodeDescrEnum, JCheckBox> solarTypeCheckboxes = new TreeMap();
    private final Map<Integer, JCheckBox> blossomMonthsCheckboxes = new TreeMap();
    private final Map<Integer, JCheckBox> harvestMonthsCheckboxes = new TreeMap();
    private JTextArea maintenanceJTextArea;
    private JComboBox<SnoeiWijzeEnum> treePruneComboBox;

    private List<JComponent> validatableComponents;

    private IFloraSubType floraElement;
    private IFloraSubType prevFloraElement;
    private final ActionType actionType;
    private final InterrogationSetup interrogationSetup;
    private int listIdx = 0;
    private int activeMediaIdx = -1;
    private int maxMediaIdx = 0;

    List<IFloraSubType> dataList;

    public Detail(DbManager dbM, String title, ActionType actionT, InterrogationSetup iSetup) throws HeadlessException, IOException {
        super(title);

        dbMgr = dbM;
        actionType = actionT;
        interrogationSetup = iSetup;

        init();
    }

    private void init() throws IOException {

        validatableComponents = new ArrayList();
        meReference = this;
        setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel prevNextPanel = new JPanel();
        previousFloraButton = new JButton("<<<<");
        previousFloraButton.setPreferredSize(new Dimension(200, 20));
        previousFloraButton.addActionListener((ActionEvent e) -> {
            try {
                refreshDataList(--listIdx, maxMediaIdx, false);
            } catch (Exception exc) {
            }
        });

        refName = new JLabel();
        refName.setPreferredSize(new Dimension(250, 50));

        nextFloraButton = new JButton(">>>>");
        nextFloraButton.setPreferredSize(previousFloraButton.getPreferredSize());
        nextFloraButton.addActionListener((ActionEvent e) -> {
            try {
                refreshDataList(++listIdx, maxMediaIdx, false);
            } catch (Exception exc) {
            }
        });
        prevNextPanel.add(previousFloraButton);
        prevNextPanel.add(refName);
        prevNextPanel.add(nextFloraButton);
        previousFloraButton.setVisible(actionType.isListProcessingAction());
        nextFloraButton.setVisible(actionType.isListProcessingAction());

        c.anchor = c.CENTER;
        c.gridwidth = c.REMAINDER;
        c.fill = c.HORIZONTAL;
        getContentPane().add(prevNextPanel, c);

        c = new DefaultGridBagConstraints();
        c.gridwidth = 1;
        c.gridheight = 2;
        c.gridy++;
        getContentPane().add(getMediaPanel(dbMgr), c);

        c.gridheight = 1;
        c.gridx++;
        c.ipadx = 15;
        getContentPane().add(getPropertiesPanel(), c);
        c.ipadx = 0;

        customPanel = new JPanel();
        customPanel.add(new JLabel("ReplacableCustomPanel"));
        customPanel.setName("ReplacableCustomPanel");

        c.gridy++;
        getContentPane().add(customPanel, c);

        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++;
        c.anchor = c.CENTER;
        getContentPane().add(getButtonPanel(dbMgr), c);

        JFrameUtils.centerComponent(this);
    }

    private JPanel getButtonPanel(DbManager dbMgr) {
        JPanel panel = new JPanel();

        newButton = new JButton("New");
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    IFloraSubType newC = floraElement.getClass().getDeclaredConstructor(new Class[0]).newInstance();
                    setDataList(Arrays.asList(newC));
                    refreshDataList(0, activeMediaIdx, false);
                } catch (IOException
                        | NoSuchMethodException
                        | SecurityException
                        | InstantiationException
                        | IllegalAccessException
                        | IllegalArgumentException
                        | InvocationTargetException exc) {
                    Logger.getLogger(Detail.class.getName()).log(Level.SEVERE, null, exc);
                }

            }
        });
        newButton.setVisible(actionType.allowsElementCreation());

        saveButton = new JButton("Save");
        saveButton.addActionListener((ActionEvent e) -> {
                    try {
                        floraElement.setSpecies((Species) speciesCombo.getSelectedItem());
                        floraElement.setLatinName(latinNameText.getText().trim().toLowerCase());
                        floraElement.setCommonName(commonNameText.getText().trim().toLowerCase());
                        floraElement.setMaxHeight(maxHeightText.getText().trim().length() > 0 ? Integer.valueOf(maxHeightText.getText().trim()) : null);
                        floraElement.setMaxWidth(maxWidthText.getText().trim().length() > 0 ? Integer.valueOf(maxWidthText.getText().trim()) : null);
                        floraElement.setFunctionTypes(toSelectedStringsListFromCodeEnum(functionTypeCheckboxes).stream().map(v -> {
                            return FunctieEnum.parse(v);
                        }).collect(Collectors.toList()));
                        floraElement.setSoilTypes(toSelectedStringsListFromGroupedCodeEnum(soilTypeCheckboxes).stream().map(v -> {
                            return BodemEigenschapEnum.parse(v);
                        }).collect(Collectors.toList()));
                        floraElement.setSolarTypes(toSelectedStringsListFromCodeEnum(solarTypeCheckboxes).stream().map(v -> {
                            return ZonlichtEnum.parse(v);
                        }).collect(Collectors.toList()));

                        floraElement.setWinterLeaves(winterLeafCombo.getSelectedBooleanValue());

                        floraElement.setBlossomMonths(toSelectedIntegersList(blossomMonthsCheckboxes).stream().map(v -> {
                            return Integer.valueOf(v);
                        }).collect(Collectors.toList()));

                        floraElement.setHarvestMonths(toSelectedIntegersList(harvestMonthsCheckboxes).stream().map(v -> {
                            return Integer.valueOf(v);
                        }).collect(Collectors.toList()));

                        floraElement.getSpecialProperties().clear();
                        specialPropsModel.getDataVector().stream().forEach(v -> {
                            floraElement.getSpecialProperties().put((OpvallendeEigenschapEnum) v.get(0), v.get(1).toString());
                        });
                        floraElement.setMaintenance(maintenanceJTextArea.getText().trim().length() > 0 ? maintenanceJTextArea.getText().trim() : null);

                        dbMgr.saveFlora(floraElement);

                        Project selProject = (Project) projectCombo.getSelectedItem();
                        if (selProject == null) {
                            return;
                        }

                        List<Project> projects = dbMgr.listProjectsFor(floraElement);
                        if (projects.size() == 0
                                || projects.stream().noneMatch(p -> Objects.equals(p.getId(), selProject.getId()))) {
                            dbMgr.saveProject(selProject);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
        );
        saveButton.setVisible(actionType.allowsElementUpdate());

        saveAsButton = new JButton("Save as..");
        saveAsButton.addActionListener((ActionEvent e) -> {
            String newName = JOptionPane.showInputDialog("Latin name", floraElement.getLatinName());
            if(newName != null){
                floraElement = floraElement.getCopy(newName.toLowerCase());
                dbMgr.saveFlora(floraElement);
                dataList.add(floraElement);
                try {
                    refreshDataList(dataList.size(),0,true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        saveAsButton.setVisible(actionType.allowsElementUpdate());


        validateButton = new JButton("Validate");

        validateButton.addActionListener(
                (ActionEvent e) -> {
                    try {
                        validateFloraElement();
                    } catch (Exception exc) {
                        new RuntimeException(exc);
                    }
                }
        );
        validateButton.setVisible(actionType.allowsElementIntgerrogation());

        panel.add(newButton);
        panel.add(saveButton);
        panel.add(saveAsButton);
        panel.add(validateButton);

        return panel;
    }

    private void validateFloraElement() throws IOException {
        boolean validationSucces = true;

        if (!(commonNameText.getText().trim().equalsIgnoreCase(floraElement.getCommonName().trim()))) {
            validationSucces = false;
            commonNameText.setBackground(Color.YELLOW);
        }

        List<String> selectedValues = toSelectedStringsListFromCodeEnum(functionTypeCheckboxes);
        if (selectedValues.size() == floraElement.getFunctionTypes().size()) {
            selectedValues.removeIf(floraElement.getFunctionTypes().stream().map(s -> {
                return s.getCode();
            }).collect(Collectors.toList())::contains);
            if (selectedValues.size() > 0) {
                validationSucces = false;
                functionTypeCheckboxes.values().stream().forEach(s -> {
                    s.setBackground(Color.YELLOW);
                });
            }
        } else {
            validationSucces = false;
            functionTypeCheckboxes.values().stream().forEach(s -> {
                s.setBackground(Color.YELLOW);
            });

        }

        selectedValues = toSelectedStringsListFromGroupedCodeEnum(soilTypeCheckboxes);
        if (selectedValues.size() == floraElement.getSoilTypes().size()) {
            selectedValues.removeIf(floraElement.getSoilTypes().stream().map(s -> {
                return s.getCode();
            }).collect(Collectors.toList())::contains);
            if (selectedValues.size() > 0) {
                validationSucces = false;
                soilTypeCheckboxes.values().stream().forEach(s -> {
                    s.setBackground(Color.YELLOW);
                });
            }
        } else {
            validationSucces = false;
            soilTypeCheckboxes.values().stream().forEach(s -> {
                s.setBackground(Color.YELLOW);
            });

        }

        selectedValues = toSelectedStringsListFromCodeEnum(solarTypeCheckboxes);
        if (selectedValues.size() == floraElement.getSolarTypes().size()) {
            selectedValues.removeIf(floraElement.getSolarTypes().stream().map(s -> {
                return s.getCode();
            }).collect(Collectors.toList())::contains);
            if (selectedValues.size() > 0) {
                validationSucces = false;
                solarTypeCheckboxes.values().stream().forEach(s -> {
                    s.setBackground(Color.YELLOW);
                });
            }
        } else {
            validationSucces = false;
            solarTypeCheckboxes.values().stream().forEach(s -> {
                s.setBackground(Color.YELLOW);
            });

        }

        boolean customValidationSucces = floraElement.validateInterrogatedValues();

        if (validationSucces && customValidationSucces) {
            if (floraElement != prevFloraElement) {
                dbMgr.updateFloraValidationDate(floraElement);
            }
            dataList.remove(listIdx);
            prevFloraElement = floraElement;
            refreshDataList(listIdx, 0, false);
        } else {
            prevFloraElement = floraElement;
            int button = JOptionPane.showConfirmDialog(this, "Mmm... \"perfect\" isn't there just yet. \nShall I reveal the correct answer?", "Fail", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (button == JOptionPane.YES_OPTION) {
                refreshDataList(listIdx, 0, true);
            } else {
                restoreBGColorForComponents();
            }
        }

    }

    private List<String> toSelectedStringsListFromCodeEnum(Map<CodeDescrEnum, JCheckBox> map) {
        List<String> actualValues = new ArrayList();

        map.forEach((s, c) -> {
            if (c.isSelected()) {
                actualValues.add(s.getCode());
            }
        });

        return actualValues;
    }

    private List<String> toSelectedStringsListFromGroupedCodeEnum(Map<GroupedCodeDescrEnum, JCheckBox> map) {
        List<String> actualValues = new ArrayList();

        map.forEach((s, c) -> {
            if (c.isSelected()) {
                actualValues.add(s.getUniqueid());
            }
        });

        return actualValues;
    }

    private List<Integer> toSelectedIntegersList(Map<Integer, JCheckBox> map) {
        List<Integer> actualValues = new ArrayList();

        map.forEach((s, c) -> {
            if (c.isSelected()) {
                actualValues.add(s);
            }
        });

        return actualValues;
    }

    private JPanel getPropertiesPanel() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        PropertyLabel FloraSubTypeLabel = new PropertyLabel("subtype");
        FloraSubTypeList = new JComboBox<>();
        FloraSubTypeList.setEditable(false);
        validatableComponents.add(FloraSubTypeList);

        for (int i = 0; i < FloraSubTypeEnum.values().length; i++) {
            FloraSubTypeList.insertItemAt(FloraSubTypeEnum.values()[i], i);
        }
        FloraSubTypeList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FloraSubTypeEnum subT = (FloraSubTypeEnum) FloraSubTypeList.getSelectedItem();
                floraElement = subT.getInstance(floraElement);
                dataList.set(listIdx, floraElement);
                try {
                    meReference.refreshDataList(listIdx, activeMediaIdx, false);
                } catch (IOException exc) {
                    throw new RuntimeException(exc);
                }
            }
        });

        PropertyLabel speciesLabel = new PropertyLabel("Soortnaam");
        speciesCombo = new JComboBox<>();
        JButton addSpeciesButton = new JButton("New..");
        addSpeciesButton.setPreferredSize(new Dimension(80, 25));
        addSpeciesButton.addActionListener(new SpeciesActionListener());


        JPanel speciesPanel = new JPanel();
        speciesPanel.setLayout(new GridBagLayout());
        speciesPanel.add(speciesCombo);//, new DefaultGridBagConstraints());
        speciesPanel.add(addSpeciesButton);

        PropertyLabel projectsLabel = new PropertyLabel("In project(en)");
        projectCombo = new JComboBox<>();

        JButton newProject = new JButton("New..");
        newProject.setPreferredSize(new Dimension(80, 25));
        newProject.addActionListener((e) -> {
            String name = JOptionPane.showInputDialog(meReference, "Welke naam ?");
            Project newP = new Project(name);
//            newP.addMember(new ProjectElement(floraElement));
            //dbMgr.saveProject(newP);
            projectCombo.addItem(newP);
            projectCombo.setSelectedItem(newP);
        });

        JButton addToProject = new JButton("Link to project..");
        addToProject.setPreferredSize(new Dimension(140, 25));
        addToProject.addActionListener((e) -> {

            List<Project> projects = dbMgr.listProjects();
            if (projects.size() == 0) {
                JOptionPane.showMessageDialog(null, "Geen projecten gevonden");
            } else {
                Project selectedProject = (Project) JOptionPane.showInputDialog(meReference, "Welke project ?", "Kies maar", QUESTION_MESSAGE, null, projects.toArray(), projects.get(0));
                if (selectedProject != null) {
                    selectedProject.addMember(new ProjectElement(floraElement));
                    //dbMgr.saveProject(selectedProject);
                    projectCombo.addItem(selectedProject);
                    projectCombo.setSelectedItem(selectedProject);
                }
            }
        });

        JPanel projectsPanel = new JPanel();
        projectsPanel.setLayout(new GridBagLayout());
        projectsPanel.add(projectCombo);//, new DefaultGridBagConstraints());
        projectsPanel.add(newProject);
        projectsPanel.add(addToProject);

        PropertyLabel latinNameLabel = new PropertyLabel("Latijnse naam");
        latinNameText = new JTextField();
        latinNameText.setPreferredSize(new Dimension(200, 20));
        validatableComponents.add(latinNameText);

        PropertyLabel commonNameLabel = new PropertyLabel("Vlaamse naam");
        commonNameText = new JTextField();
        commonNameText.setPreferredSize(new Dimension(200, 20));
        validatableComponents.add(commonNameText);

        PropertyLabel functionLabel = new PropertyLabel("Functie");
        functionPanel = getFunctionPanel();

        PropertyLabel soilLabel = new PropertyLabel("Grondsoort");
        soilPanel = getSoilPanel();

        PropertyLabel solarLabel = new PropertyLabel("Zonlicht");
        JPanel solarPanel = getSolarPanel();

        PropertyLabel winterLeafLabel = new PropertyLabel("Bladhoudend");
        winterLeafCombo = new OptionalBooleanJCombobox();

        PropertyLabel heightLabel = new PropertyLabel("Hoogte");
        maxHeightText = new JTextField();
        maxHeightText.setPreferredSize(new Dimension(35, 20));
        validatableComponents.add(maxHeightText);

        PropertyLabel widthLabel = new PropertyLabel("Breedte");
        maxWidthText = new JTextField();
        maxWidthText.setPreferredSize(new Dimension(35, 20));
        validatableComponents.add(maxWidthText);

        PropertyLabel blossomMonthsLabel = new PropertyLabel("Bloeimaanden");
        JPanel blossomMonthsPanel = getBlossomMonthsPanel();

        PropertyLabel harvestMonthsLabel = new PropertyLabel("Oogstmaanden");
        JPanel harvestMonthsPanel = getHarvestMonthsPanel();

        PropertyLabel specialsLabel = new PropertyLabel("Specifieke kenmerken");
        String[] cols = {"Type", "Omschrijving"};
        Object[][] data = new Object[0][2];

        specialPropsModel = new ImmutableTableModel(data, cols);
        specialProps = new JTable(specialPropsModel);
        specialProps.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    specialPropsModel.removeRow(specialProps.getSelectedRow());
                }
            }
        });
        MouseListener mouseL = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    Object[] dataRowValues = new Object[2];
                    dataRowValues[0] = JOptionPane.showInputDialog(meReference, "Welk type karakteristiek ?", null, JOptionPane.QUESTION_MESSAGE, null, OpvallendeEigenschapEnum.values(), null);
                    if (null != dataRowValues[0]) {
                        dataRowValues[1] = JOptionPane.showInputDialog(meReference, "Welke waarde ?", null, JOptionPane.QUESTION_MESSAGE);
                        if (dataRowValues[1] != null) {
                            specialPropsModel.addRow(dataRowValues);
                        }
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
        };
        specialProps.getTableHeader().addMouseListener(mouseL);
        specialProps.addMouseListener(mouseL);

        validatableComponents.add(specialProps);

        JScrollPane specialsScrollpane = new JScrollPane(specialProps);
        specialsScrollpane.addMouseListener(mouseL);
        specialsScrollpane.setPreferredSize(new Dimension(350, 80));

        PropertyLabel maintenanceLabel = new PropertyLabel("Maintenance");
        maintenanceJTextArea = new JTextArea();
        maintenanceJTextArea.setPreferredSize(new Dimension(400, 100));

        treePruneComboBox = new JComboBox<>();
        treePruneComboBox.setEditable(false);
        for (int i = 0; i < SnoeiWijzeEnum.values().length; i++) {
            treePruneComboBox.insertItemAt(SnoeiWijzeEnum.values()[i], i);
        }

        treePruneComboBox.addActionListener((ActionEvent e) -> {
            maintenanceJTextArea.setText(((SnoeiWijzeEnum) treePruneComboBox.getSelectedItem()).getLongDescription());
        });
        c.gridx = 0;
        c.gridy = 0;
        panel.add(FloraSubTypeLabel, c);
        c.gridx++;
        panel.add(FloraSubTypeList, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(speciesLabel, c);
        c.gridx++;
        panel.add(speciesPanel, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(projectsLabel, c);
        c.gridx++;
        panel.add(projectsPanel, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(latinNameLabel, c);
        c.gridx++;
        panel.add(latinNameText, c);
        c.gridx++;

        c.gridx = 0;
        c.gridy++;
        panel.add(commonNameLabel, c);
        c.gridx++;
        panel.add(commonNameText, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(functionLabel, c);
        c.gridx++;
        panel.add(functionPanel, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(soilLabel, c);
        c.gridx++;
        panel.add(soilPanel, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(solarLabel, c);
        c.gridx++;
        panel.add(solarPanel, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(winterLeafLabel, c);
        c.gridx++;
        panel.add(winterLeafCombo, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(heightLabel, c);
        c.gridx++;
        panel.add(maxHeightText, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(widthLabel, c);
        c.gridx++;
        panel.add(maxWidthText, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(blossomMonthsLabel, c);
        c.gridx++;
        panel.add(blossomMonthsPanel, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(harvestMonthsLabel, c);
        c.gridx++;
        panel.add(harvestMonthsPanel, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(specialsLabel, c);
        c.gridx++;
        panel.add(specialsScrollpane, c);

        c.gridy++;
        c.gridx = 0;
        panel.add(maintenanceLabel, c);
        c.gridx++;
        panel.add(maintenanceJTextArea, c);
        c.gridx++;
        panel.add(treePruneComboBox, c);

        return panel;
    }

    public void setDataList(List<IFloraSubType> elems) {
        dataList = elems;
        listIdx = 0;
    }

    public void refreshDataList(int elementIdx, Integer mediaIdx, boolean revealAnswers) throws IOException {
        if (actionType == ActionType.INTERROGATION) {
            restoreBGColorForComponents();
        }

        if (dataList != null) {
            listIdx = elementIdx;
            if (listIdx < dataList.size()) {
            } else {
                if (dataList.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No more details available");
                    this.dispose();
                } else {
                    listIdx = dataList.size() - 1;
                }
            }
            floraElement = dataList.get(listIdx);
            previousFloraButton.setVisible(dataList.size() > 0);
            nextFloraButton.setVisible(dataList.size() > 0);

            previousFloraButton.setEnabled(elementIdx > 0);
            nextFloraButton.setEnabled(elementIdx < dataList.size() - 1);

        }

        maxMediaIdx = floraElement.getMediaReferences().size() - 1;
        activeMediaIdx = (floraElement.getMediaReferences().size() > 0 ? 0 : -1);
        if (mediaIdx != null && mediaIdx > -1 && floraElement.getMediaReferences().size() > mediaIdx.intValue()) {
            activeMediaIdx = mediaIdx.intValue();
        }

        previousMediaButton.setText("" + (activeMediaIdx + 1) + "/" + (maxMediaIdx + 1) + "   " + previousMediaButton.getText().substring(previousMediaButton.getText().length() - 2));
        nextMediaButton.setText(nextMediaButton.getText().substring(0, 2) + "   " + (activeMediaIdx + 1) + "/" + (maxMediaIdx + 1));
        previousMediaButton.setEnabled(activeMediaIdx > 0);
        nextMediaButton.setEnabled(activeMediaIdx <= maxMediaIdx);
        //refName.setText(floraElement == null ? "":floraElement.getLatinName());

        ImageInputStream tgtImage;
        if (floraElement == null || activeMediaIdx <= -1) {
            image = new ImagePanel(DbManager.noImageUrl());
        } else {
            File tgtFile;
            if (floraElement.getMediaReferences().get(activeMediaIdx) instanceof Document) {
                String tgtId = ((Document) floraElement.getMediaReferences().get(activeMediaIdx)).getObjectId("gridFsId").toHexString();
                tgtFile = dbMgr.getTempFile(tgtId);

            } else {
                tgtFile = ((File) floraElement.getMediaReferences().get(activeMediaIdx));
            }

            tgtImage = new FileImageInputStream(tgtFile);
            image = new ImagePanel(tgtImage);
        }
        zoom.setImage(image);
        zoom.reset();
        scrollPane.setViewportView(zoom.imagePanel);

        scrollPane.revalidate();

        setupValuesFromDatabase();
        if (actionType == ActionType.INTERROGATION && !revealAnswers) {
            overrideValuesForInterrogation();
        }

        pack();
        repaint();

    }

    private void setupValuesFromDatabase() {
        FloraSubTypeList.setSelectedItem(floraElement == null ? null : floraElement.getSubType());
        latinNameText.setText(floraElement == null ? "" : floraElement.getLatinName());

        speciesCombo.removeAllItems();
        List<Species> specList = dbMgr.listSpecies();
        specList.forEach(s -> {
            speciesCombo.addItem(s);

            if (floraElement.getSpecies() != null && s.getId().compareTo(floraElement.getSpecies().getId()) == 0) {
                speciesCombo.setSelectedItem(s);
            }
        });

        projectCombo.removeAllItems();
        List<Project> projList = dbMgr.listProjectsFor(floraElement);
        projList.forEach(s -> {
            projectCombo.addItem(s);
        });

        if (projList != null && projList.size() > 0) {
            projectCombo.setSelectedItem(projList.get(0));
        }

        commonNameText.setText(floraElement == null ? "" : floraElement.getCommonName());

        functionTypeCheckboxes.values().forEach(c -> {
            c.setSelected(false);
        });
        soilTypeCheckboxes.values().forEach(c -> {
            c.setSelected(false);
        });
        solarTypeCheckboxes.values().forEach(c -> {
            c.setSelected(false);
        });

        if (floraElement != null) {
            floraElement.getFunctionTypes().forEach(s -> {
                functionTypeCheckboxes.get(s).setSelected(true);
            });
            floraElement.getSoilTypes().forEach(s -> {
                soilTypeCheckboxes.get(s).setSelected(true);
            });
            floraElement.getSolarTypes().forEach(s -> {
                solarTypeCheckboxes.get(s).setSelected(true);
            });
        }

        winterLeafCombo.setSelectedBooleanValue(floraElement.getWinterLeaves());

        maxHeightText.setText(floraElement.getMaxHeight() != null ? "" + floraElement.getMaxHeight() : "");

        maxWidthText.setText(floraElement.getMaxWidth() != null ? "" + floraElement.getMaxWidth() : "");

        blossomMonthsCheckboxes.values().forEach(c -> {
            c.setSelected(false);
        });
        harvestMonthsCheckboxes.values().forEach(c -> {
            c.setSelected(false);
        });
        if (floraElement != null) {
            floraElement.getBlossomMonths().forEach(s -> {
                blossomMonthsCheckboxes.get(s).setSelected(true);
            });
            floraElement.getHarvestMonths().forEach(s -> {
                harvestMonthsCheckboxes.get(s).setSelected(true);
            });
        }

        for (int i = 0; i < specialPropsModel.getRowCount(); ) {
            specialPropsModel.removeRow(0);
        }

        floraElement.getSpecialProperties().forEach((c, v) -> {
            Object[] dataRow = new Object[2];
            int colId = 0;

            dataRow[colId] = c;
            dataRow[++colId] = v;

            specialPropsModel.addRow(dataRow);
        });
        specialProps.repaint();

        maintenanceJTextArea.setText(floraElement.getMaintenance() != null ? "" + floraElement.getMaintenance() : "");

        JPanel custom = floraElement != null ? floraElement.getSubTypePropertiesPanel() : null;

        DefaultGridBagConstraints c = new DefaultGridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 15;
        getContentPane().remove(3);
        if (custom != null) {
            getContentPane().add(custom, c, 3);
        } else {
            getContentPane().add(new JPanel(), c, 3);
        }

        floraElement.refreshSubTypeComponents();
    }

    private void overrideValuesForInterrogation() {
        FloraSubTypeList.setEnabled(false);

        latinNameText.setEnabled(false);

        if (interrogationSetup.testCommonName) {
            commonNameText.setText(
                    "???");
        } else {
            commonNameText.setEnabled(false);
        }

        if (interrogationSetup.testFunction) {
            functionTypeCheckboxes.values().forEach(c -> {
                c.setSelected(false);
            });
        } else {
            functionTypeCheckboxes.values().forEach(c -> {
                c.setEnabled(false);
            });
        }

        if (interrogationSetup.testSoilType) {
            soilTypeCheckboxes.values().forEach(c -> {
                c.setSelected(false);
            });
        } else {
            soilTypeCheckboxes.values().forEach(c -> {
                c.setEnabled(false);
            });
        }

        if (interrogationSetup.testSolarType) {
            solarTypeCheckboxes.values().forEach(c -> {
                c.setSelected(false);
            });
        } else {
            solarTypeCheckboxes.values().forEach(c -> {
                c.setEnabled(false);
            });
        }

        winterLeafCombo.setEnabled(false);
        maxHeightText.setEnabled(false);
        maxWidthText.setEnabled(false);
        treePruneComboBox.setEnabled(false);
        maintenanceJTextArea.setEnabled(false);
        floraElement.setUpPanelForInterrogation(interrogationSetup);

    }

    private JPanel getFunctionPanel() {
        JPanel fPanel = new JPanel();

        fPanel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints cbc = new DefaultGridBagConstraints();
        cbc.gridy = -1;
        cbc.fill = cbc.HORIZONTAL;
        FunctieEnum[] functieEnums = FunctieEnum.values();

        for (int i = 0; i < functieEnums.length; i++) {
            FunctieEnum solarType = functieEnums[i];

            cbc.gridx++;
            JCheckBox cb = new JCheckBox(solarType.getDescription());
            functionTypeCheckboxes.put(solarType, cb);
            cb.setName(functieEnums[i].name());
            validatableComponents.add(cb);

            fPanel.add(cb, cbc);

        }

        return fPanel;
    }

    private JPanel getSoilPanel() {
        soilPanel = new JPanel();
        List<String> groupCodes = BodemEigenschapEnum.getGroupCodes();
        soilPanel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints cbc = new DefaultGridBagConstraints();
        cbc.gridy = -1;
        cbc.fill = cbc.HORIZONTAL;
        BodemEigenschapEnum[] bodemEigenschapEnums = BodemEigenschapEnum.values();

        for (int x = 0; x < groupCodes.size(); x++) {
            cbc.gridy++;
            cbc.gridx = -1;
            for (int i = 0; i < bodemEigenschapEnums.length; i++) {
                BodemEigenschapEnum bodemEigenschapEnum = bodemEigenschapEnums[i];
                if (bodemEigenschapEnum.getGroup().equalsIgnoreCase(groupCodes.get(x))) {
                    cbc.gridx++;
                    JCheckBox cb = new JCheckBox(bodemEigenschapEnum.getDescription());
                    soilTypeCheckboxes.put(bodemEigenschapEnum, cb);
                    cb.setName(bodemEigenschapEnums[i].name());
                    validatableComponents.add(cb);
                    soilPanel.add(cb, cbc);
                }
            }

        }

        return soilPanel;
    }

    private JPanel getSolarPanel() {
        JPanel solarPanel = new JPanel();

        solarPanel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints cbc = new DefaultGridBagConstraints();
        cbc.gridy = -1;
        cbc.fill = cbc.HORIZONTAL;
        ZonlichtEnum[] zonlichtEnums = ZonlichtEnum.values();

        for (int i = 0; i < zonlichtEnums.length; i++) {
            ZonlichtEnum zonlichtEnum = zonlichtEnums[i];

            cbc.gridx++;
            JCheckBox cb = new JCheckBox(zonlichtEnum.getDescription());
            solarTypeCheckboxes.put(zonlichtEnum, cb);
            cb.setName(zonlichtEnums[i].name());
            validatableComponents.add(cb);

            solarPanel.add(cb, cbc);

        }

        return solarPanel;
    }

    private JPanel getBlossomMonthsPanel() {
        JPanel blossomMonthsPanel = new JPanel();

        blossomMonthsPanel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints cbc = new DefaultGridBagConstraints();
        cbc.fill = cbc.HORIZONTAL;
        ZonlichtEnum[] zonlichtEnums = ZonlichtEnum.values();

        for (int i = 0; i < 12; i++) {

            cbc.gridx++;
            String name = String.format("%d", i + 1);
            JCheckBox cb = new JCheckBox(getMonthName(i + 1));
            blossomMonthsCheckboxes.put(i + 1, cb);
            cb.setName(name);
            validatableComponents.add(cb);

            cbc.gridy = 0;
            blossomMonthsPanel.add(cb, cbc);

        }

        return blossomMonthsPanel;
    }

    private JPanel getHarvestMonthsPanel() {
        JPanel harvestMonthsPanel = new JPanel();

        harvestMonthsPanel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints cbc = new DefaultGridBagConstraints();
        cbc.fill = cbc.HORIZONTAL;
        ZonlichtEnum[] zonlichtEnums = ZonlichtEnum.values();

        for (int i = 0; i < 12; i++) {

            cbc.gridx++;
            String name = String.format("%d", i + 1);
            JCheckBox cb = new JCheckBox(getMonthName(i + 1));
            harvestMonthsCheckboxes.put(i + 1, cb);
            cb.setName(name);
            validatableComponents.add(cb);

            cbc.gridy = 0;
            harvestMonthsPanel.add(cb, cbc);

        }

        return harvestMonthsPanel;
    }

    private JPanel getMediaPanel(DbManager dbMgr) throws IOException {
        photoPanel = new JPanel();
        photoPanel.setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        image = new ImagePanel(DbManager.noImageUrl());
        zoom = new ImageZoom(image);

        previousMediaButton = new JButton("<<");
        previousMediaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (activeMediaIdx > 0) {
                    activeMediaIdx--;
                    try {
                        refreshDataList(listIdx, activeMediaIdx, false);
                    } catch (IOException ex) {
                        Logger.getLogger(Detail.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        JButton updateButton = new JButton("update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(new File("C:\\temp\\QwiklurnUploads"));
                fc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().endsWith(".jpg")
                                || f.getName().endsWith(".jpeg")
                                || f.getName().endsWith(".png")
                                || f.getName().endsWith(".bmp")
                                ;
                    }

                    @Override
                    public String getDescription() {
                        return null;
                    }
                });
                int returnVal = fc.showOpenDialog(Detail.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (activeMediaIdx == -1) {
                        activeMediaIdx++;
                        maxMediaIdx++;
                        floraElement.getMediaReferences().add(file);
                    } else {
                        floraElement.getMediaReferences().set(activeMediaIdx, file);
                    }
                    try {
                        refreshDataList(listIdx, activeMediaIdx, false);
                    } catch (IOException ex) {
                        Logger.getLogger(Detail.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    //Cancelled
                }

            }

        });
        nextMediaButton = new JButton(">>");
        nextMediaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (activeMediaIdx < maxMediaIdx) {
                    activeMediaIdx++;
                } else {
                    activeMediaIdx++;
                    maxMediaIdx++;
                    floraElement.getMediaReferences().add(null);
                }
                try {
                    refreshDataList(listIdx, activeMediaIdx, false);
                } catch (IOException ex) {
                    Logger.getLogger(Detail.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }

        });

        photoPanel.add(zoom.getUIPanel(), c);
        c.gridx++;
        photoPanel.add(previousMediaButton, c);
        c.gridx++;
        photoPanel.add(updateButton, c);
        c.gridx++;
        photoPanel.add(nextMediaButton, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 4;
        scrollPane = new JScrollPane(zoom.imagePanel);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setMaximumSize(new Dimension(500, 400));
        photoPanel.add(scrollPane, c);
        photoPanel.setAlignmentX(CENTER_ALIGNMENT);

        return photoPanel;
    }

    private void restoreBGColorForComponents() {
        validatableComponents.forEach(c -> c.setBackground(Color.WHITE));

        if (floraElement != null) {
            floraElement.restoreBGColorForSubTypeComponents();
        }
    }

    private String getMonthName(int i) {
        switch (i) {
            case 1:
                return "jan";
            case 2:
                return "feb";
            case 3:
                return "maa";
            case 4:
                return "apr";
            case 5:
                return "mei";
            case 6:
                return "jun";
            case 7:
                return "jul";
            case 8:
                return "aug";
            case 9:
                return "sep";
            case 10:
                return "okt";
            case 11:
                return "nov";
            case 12:
                return "dec";
        }
        throw new RuntimeException("Invalid month number: " + i);
    }

    private class SpeciesActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = JOptionPane.showInputDialog(meReference, "Welke naam ?");
            List<VoortplantingEnum> selectedTechn = new ArrayList<>();
            List<Object> options = new ArrayList<>();
            if (name != null) {
                JPanel subPanel = new JPanel();
                subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
                Arrays.stream(VoortplantingEnum.values()).forEach(t -> {
                            JCheckBox cb = new JCheckBox(t.getDescription());
                            cb.addActionListener(e1 -> {
                                if (cb.isSelected()) {
                                    selectedTechn.add(t);
                                } else {
                                    selectedTechn.remove(t);
                                }
                            });
                            subPanel.add(cb);
                        }
                );

                JPanel actionButtons = new JPanel();
                actionButtons.setLayout(new FlowLayout());
                JButton close = new JButton("Ok");
                close.addActionListener(e2 -> {

                    Window w = SwingUtilities.getWindowAncestor(close);
                    w.setVisible(false);
                    w.dispose();

                    PricingCategory price = (PricingCategory) JOptionPane.showInputDialog(meReference, "Welke richtprijs per stuk ?", "Richtprijs", QUESTION_MESSAGE, null, PricingCategory.values(), PricingCategory.A);
                    if (price != null) {
                        Species newS = new Species(null, name, new ArrayList<>(), selectedTechn, price);
                        speciesCombo.addItem(newS);
                        speciesCombo.setSelectedItem(newS);
                    }
                });
                JButton cancel = new JButton("Cancel");
                cancel.addActionListener(e3 -> {

                    Window w = SwingUtilities.getWindowAncestor(cancel);
                    w.setVisible(false);
                    w.dispose();
                });
                actionButtons.add(close);
                actionButtons.add(cancel);
                subPanel.add(actionButtons);
                options.add(subPanel);
                JOptionPane.showOptionDialog(meReference, "Welke reproductie methodes ?", "Input", JOptionPane.OK_CANCEL_OPTION, QUESTION_MESSAGE, null, options.toArray(), null);
            }
        }
    }
}
