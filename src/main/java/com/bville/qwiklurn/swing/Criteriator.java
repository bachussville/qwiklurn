/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.swing;

import com.bville.qwiklurn.repository.flora.CodeDescrEnum;
import com.bville.qwiklurn.repository.flora.DbManager;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.SoilType;
import com.bville.qwiklurn.repository.flora.SolarType;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import javax.lang.model.util.Elements;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableModel;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

/**
 *
 * @author Bart
 */
public class Criteriator extends JFrame {

    private class StringWrapper {

        private String src;
        private Criterium owner;
        public boolean isCriteriumStartZone;

        public StringWrapper(String s, Criterium owner, boolean isCriteriumStartZone) {
            src = s;
            this.owner = owner;
            this.isCriteriumStartZone = isCriteriumStartZone;
        }

        @Override
        public String toString() {
            return src;
        }

    }

    public enum CriteriumAttribute {
        LATIN("Latijnse name", "latinName", String.class),
        SOIL("Grondeigenschap", "soilTypes", SoilType.class),
        SOLAR("Belichting", "solarTypes", SolarType.class);

        public String name;
        public String attr;
        public Class valueType;

        CriteriumAttribute(String name, String attr, Class valueType) {
            this.name = name;
            this.attr = attr;
            this.valueType = valueType;
        }

        @Override
        public String toString() {
            return name;
        }

        Object[] getArrayOfAllowedInputValues() {
            if (valueType == SoilType.class) {
                return SoilType.values();
            }
            if (valueType == SolarType.class) {
                return SolarType.values();
            }

            return null;
        }
    }

    public enum CriteriumOperator {
        EQ("=", "$eq", false),
        LIKE("like", "$regex", false),
        NE("<>", "$ne", false),
        IN("in lijst", "$in", true);

        public String descr, dbFunction;
        public boolean supportsMultiValues;

        private CriteriumOperator(String descr, String dbFunc, boolean supportsMultiValues) {
            this.descr = descr;
            dbFunction = dbFunc;
            this.supportsMultiValues = supportsMultiValues;
        }

        @Override
        public String toString() {
            return descr;
        }

    }

    public class Criterium {

        public CriteriumGroup parentGroup;
        public CriteriumAttribute attr;
        public CriteriumOperator o;
        public List<Object> listValues = new ArrayList<>();
        public boolean isGroup = false;

        public Criterium(CriteriumGroup parentGroup) {
            this.parentGroup = parentGroup;
        }

        @Override
        public String toString() {
            if (isGroup) {
                return null;
            }
            if (o.supportsMultiValues) {
                StringBuffer result = new StringBuffer(attr.name + " " + o.descr + "[");
                listValues.forEach(v -> {
                    result.append(translateToUserValue(v) + ",");
                });
                result.delete(result.length() - 1, result.length());
                result.append("]");

                return result.toString();
            } else {
                return attr.name + " " + o.descr
                        + (o == CriteriumOperator.LIKE ? "/.*" : "")
                        + translateToUserValue(listValues.get(0))
                        + (o == CriteriumOperator.LIKE ? ".*/i" : "");
            }
        }

        public String getJsonForBasicCriteria(Criteriator.Criterium e) {
            if (e.o.supportsMultiValues) {
                StringBuffer jsonStr = new StringBuffer();
                if (e.attr.attr.equalsIgnoreCase("soilTypes")
                        || e.attr.attr.equalsIgnoreCase("solarTypes")) {
                    jsonStr.append("{" + e.attr.attr + " : {" + e.o.dbFunction + " : [");
                    e.listValues.forEach(val -> {
                        jsonStr.append("\"" + ((CodeDescrEnum) val).getCode() + "\",");
                    });
                    jsonStr.delete(jsonStr.length() - 1, jsonStr.length());
                    jsonStr.append("]}}");
                    return jsonStr.toString();
                }

            } else {
                if (e.attr.attr.equalsIgnoreCase("soilTypes")
                        || e.attr.attr.equalsIgnoreCase("solarTypes")) {
                    return "{" + e.attr.attr + " : {" + e.o.dbFunction + " : \"" + ((CodeDescrEnum) e.listValues.get(0)).getCode() + "\"}}";

                } else {
                    return "{" + e.attr.attr + " : {" + e.o.dbFunction + " : \"" + e.listValues.get(0) + "\"}}";
                }
            }
            throw new RuntimeException("Unable to build Json for criteria: " + e);
        }
    };

    public enum CriteriumGroupOperator implements CodeDescrEnum {
        AND("$and", "EN"),
        OR("$or", "OF");

        private String code;
        private String description;

        private CriteriumGroupOperator(String code, String descr) {
            this.code = code;
            this.description = descr;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

    public class CriteriumGroup extends Criterium {

        public CriteriumGroupOperator oper;
        public List<Criterium> elements = new ArrayList<>();

        public CriteriumGroup(CriteriumGroup parentGroup) {
            super(parentGroup);
            isGroup = true;
        }

        public String asJsonString() {
            List<Bson> aggregations = new ArrayList<>();
            StringBuffer jsonString = new StringBuffer("{" + oper.getCode() + ": [");
            jsonString.append("");

            elements.forEach(e -> {
                if (e.isGroup) {
                    jsonString.append(((CriteriumGroup)e).asJsonString() + ",");
                } else {
                    jsonString.append(getJsonForBasicCriteria(e) + ",");
                }
            });

            jsonString.replace(jsonString.length() - 1, jsonString.length(), "]}");

            return jsonString.toString();

        }
    }

    /*
    -
    -
    -
    -
    -
    -
    -
    -
     */
    public DbManager db = new DbManager();
    private CriteriumGroup mainCriteriumGroup;
    private final Criteriator meReference;
    private final ImmutableTableModel criteriaData;
    private final JTable criteriaTable;
    private JRadioButton defOperator_And, defOperator_Or;
    public List<IFloraSubType> queryResults;

    private final StudyList parent;

    public Criteriator(StudyList parent) throws HeadlessException {
        this.parent = parent;

        meReference = this;

        String[] cols = {"ID"};
        Object[][] data = new Object[0][1];
        criteriaData = new ImmutableTableModel(data, cols);
        criteriaTable = new JTable(criteriaData);

        init();
    }

    private void init() {

        setTitle("Criteriator");

        setLayout(new GridBagLayout());
        DefaultGridBagConstraints c = new DefaultGridBagConstraints();

        ButtonGroup defOperator = new ButtonGroup();
        defOperator_And = new JRadioButton("AND", false);
        defOperator_And.addActionListener((e) -> {
            if (defOperator_And.isSelected()) {
                mainCriteriumGroup.oper = CriteriumGroupOperator.AND;
                rebuildTable();
            }
        });
        defOperator_Or = new JRadioButton("OR", true);
        defOperator_Or.addActionListener((e) -> {
            if (defOperator_Or.isSelected()) {
                mainCriteriumGroup.oper = CriteriumGroupOperator.OR;
                rebuildTable();
            }
        });
        mainCriteriumGroup = new CriteriumGroup(null);
        mainCriteriumGroup.oper = CriteriumGroupOperator.OR;
        defOperator.add(defOperator_And);
        defOperator.add(defOperator_Or);
        JPanel radioPanel = new JPanel();
        radioPanel.add(defOperator_And);
        radioPanel.add(defOperator_Or);

        JButton basic = new JButton("Add basic criterium");
        basic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Criterium toAdd = new Criterium(findParentGroupForNewCriterium());

                CriteriumAttribute[] arrAttr = CriteriumAttribute.values();
                toAdd.attr = (CriteriumAttribute) JOptionPane.showInputDialog(meReference, "Op welk attribuut ?", "Attribuut", JOptionPane.QUESTION_MESSAGE, null, arrAttr, arrAttr[0]);

                if (toAdd.attr != null) {
                    CriteriumOperator[] arrOper = CriteriumOperator.values();
                    toAdd.o = (CriteriumOperator) JOptionPane.showInputDialog(meReference, "Welke operatie ?", "Operatie", JOptionPane.QUESTION_MESSAGE, null, arrOper, arrOper[0]);
                }

                if (toAdd.o != null) {
                    Object[] allowedValues = toAdd.attr.getArrayOfAllowedInputValues();
                    Object defaultAllowedValue = (allowedValues != null ? allowedValues[0] : null);
                    if (toAdd.o.supportsMultiValues) {
                        Object selectedVal = JOptionPane.showInputDialog(meReference, "Geef een waarde", "Waarden", JOptionPane.QUESTION_MESSAGE, null, allowedValues, defaultAllowedValue);
                        if (selectedVal != null) {
                            toAdd.listValues.add(selectedVal);
                            boolean moreValuesRequested = (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(meReference, "Nog een waarde toevoegen ?", "", JOptionPane.YES_NO_OPTION));
                            while (moreValuesRequested) {
                                selectedVal = JOptionPane.showInputDialog(meReference, "Geef een nog waarde", "Waarden", JOptionPane.QUESTION_MESSAGE, null, allowedValues, defaultAllowedValue);
                                if (selectedVal != null) {
                                    toAdd.listValues.add(selectedVal);
                                    moreValuesRequested = (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(meReference, "Nog een waarde toevoegen ?", "", JOptionPane.YES_NO_OPTION));
                                } else {
                                    moreValuesRequested = false;
                                }
                            }
                        }
                    } else {
                        Object selectedValue = JOptionPane.showInputDialog(meReference, "Geef een waarde", "Waarden", JOptionPane.QUESTION_MESSAGE, null, allowedValues, defaultAllowedValue);
                        if (selectedValue != null) {
                            toAdd.listValues.add(selectedValue);
                        }
                    }

                }

                if (toAdd.attr != null && toAdd.o != null && toAdd.listValues.size() > 0) {
                    addToCorrespondingCriteriumGroup(toAdd);
                    rebuildTable();
                }

            }

        });

        JButton group = new JButton("Add group of criteria");
        group.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                CriteriumGroup toAdd = new CriteriumGroup(findParentGroupForNewCriterium());

                CriteriumGroupOperator[] arrOper = CriteriumGroupOperator.values();
                toAdd.oper = (CriteriumGroupOperator) JOptionPane.showInputDialog(meReference, "Welke operatie ?", "Operatie", JOptionPane.QUESTION_MESSAGE, null, arrOper, arrOper[0]);

                if (toAdd.oper != null) {
                    addToCorrespondingCriteriumGroup(toAdd);
                    rebuildTable();
                }
            }
        });
        JScrollPane scroll = new JScrollPane(criteriaTable);

        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainCriteriumGroup.elements.clear();
                rebuildTable();
            }
        });

        JButton done = new JButton("Done");
        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                saveQuery();
            }
        });

        c.gridwidth = 2;
        getContentPane().add(radioPanel, c);

        c.gridwidth = 1;
        c.gridy++;
        getContentPane().add(basic, c);
        c.gridx++;
        getContentPane().add(group, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        getContentPane().add(scroll, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        getContentPane().add(clear, c);

        c.gridx++;
        getContentPane().add(done, c);

        rebuildTable();

        JFrameUtils.centerComponent(this);

    }

    public void buildCriteria() {
        setVisible(true);
    }

    public void saveQuery() {

        parent.alertCustomQuerySaved("");

    }

    public List<IFloraSubType> runQuery() {
        queryResults = db.findFloraByFilter(BsonDocument.parse(mainCriteriumGroup.asJsonString()));

        return queryResults;
    }

    public String translateToDBValue(Object selectedVal) {
        if (selectedVal instanceof String) {
            return (String) selectedVal;
        }

        if (selectedVal instanceof SoilType) {
            return ((SoilType) selectedVal).getCode();
        }

        if (selectedVal instanceof SolarType) {
            return ((SolarType) selectedVal).getCode();
        }

        throw new RuntimeException("Unable to determine the database value vor class " + selectedVal.getClass().getName()); //To change body of generated methods, choose Tools | Templates.
    }

    public String translateToUserValue(Object selectedVal) {
        if (selectedVal instanceof String) {
            return (String) selectedVal;
        }

        if (selectedVal instanceof CodeDescrEnum) {
            return ((CodeDescrEnum) selectedVal).getDescription();
        }

        throw new RuntimeException("Unable to determine the database value vor class " + selectedVal.getClass().getName()); //To change body of generated methods, choose Tools | Templates.
    }

    private void rebuildTable() {
        for (int i = 0; i < criteriaData.getRowCount();) {
            criteriaData.removeRow(0);
        }

        addCriteriaToTable(mainCriteriumGroup, 0);
        criteriaTable.repaint();
    }

    private String getIdentation(int level) {
        StringBuffer indentStr = new StringBuffer("+  ");
        for (int i = 0; i < level; i++) {
            indentStr.append("     +  ");
        }
        //    indentStr.append("0" + level + "   + ");
        return indentStr.toString();
    }

    private void storeLineInTable(String aLine, Criterium owner) {
        storeLineInTable(aLine, owner, false);
    }

    private void storeLineInTable(String aLine, Criterium owner, boolean isStartZone) {
        StringWrapper sw = new StringWrapper(aLine, owner, isStartZone);
        criteriaData.addRow(Arrays.asList(sw).toArray());
    }

    private void addCriteriaToTable(CriteriumGroup groupCrit, int level) {

        String prefix = getIdentation(level);
        final int nextLvl = level + 1;

        storeLineInTable(prefix + "[ - " + groupCrit.oper.name() + " -", groupCrit, true);
        storeLineInTable(prefix, groupCrit, true);

        groupCrit.elements.forEach(e -> {
            if (e.isGroup) {
                addCriteriaToTable((CriteriumGroup) e, nextLvl);
            } else {
                addCriteriaToTable(e, nextLvl);
            }
        });
        storeLineInTable(prefix, groupCrit);
        storeLineInTable(prefix + "] ", groupCrit);

    }

    private void addCriteriaToTable(Criterium basicCrit, int level) {
        storeLineInTable(getIdentation(level) + basicCrit.toString(), basicCrit);
    }

    private void addToCorrespondingCriteriumGroup(Criterium newCriterium) {
        StringWrapper selectedWrapper = (StringWrapper) criteriaData.getValueAt(criteriaTable.getSelectedRow() > -1 ? criteriaTable.getSelectedRow() : 0, 0);
        Criterium selectedElement = selectedWrapper.owner;

        if (selectedElement.isGroup) {
            if (selectedWrapper.isCriteriumStartZone) {
                //Add as firstElement
                ((CriteriumGroup) selectedElement).elements.add(0, newCriterium);
            } else {
                //Add as firstElement
                ((CriteriumGroup) selectedElement).elements.add(newCriterium);
            }
        } else {
            //Find the current group
            CriteriumGroup tgtGroup = selectedWrapper.owner.parentGroup;
            int idx = tgtGroup.elements.indexOf(selectedWrapper.owner);
            tgtGroup.elements.add(idx + 1, newCriterium);

        }
    }

    private CriteriumGroup findParentGroupForNewCriterium() {
        Criterium critSelRow = ((StringWrapper) criteriaData.getValueAt(criteriaTable.getSelectedRow() > -1 ? criteriaTable.getSelectedRow() : 0, 0)).owner;
        if (critSelRow.isGroup) {
            return (CriteriumGroup) critSelRow;
        } else {
            return critSelRow.parentGroup;
        }
    }

}
