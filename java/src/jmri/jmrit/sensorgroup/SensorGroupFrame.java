package jmri.jmrit.sensorgroup;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import jmri.*;
import jmri.implementation.DefaultConditionalAction;
import jmri.implementation.SensorGroupConditional;
import jmri.swing.RowSorterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User interface for creating and editing sensor groups.
 * <p>
 * Sensor groups are implemented by (groups) of Routes, not by any other object.
 *
 * @author Bob Jacobsen Copyright (C) 2007
 * @author Pete Cressman Copyright (C) 2009
 */
public class SensorGroupFrame extends jmri.util.JmriJFrame {

    public SensorGroupFrame() {
        super();
    }

    private final static String namePrefix = "SENSOR GROUP:";  // should be upper case
    private final static String nameDivider = ":";
    public final static String logixSysName;
    public final static String logixUserName = "System Logix";
    public final static String ConditionalSystemPrefix;
    private final static String ConditionalUserPrefix = "Sensor Group ";
    private int rowHeight;

    static {
        String logixPrefix = InstanceManager.getDefault(jmri.LogixManager.class).getSystemNamePrefix();
        logixSysName = logixPrefix + ":SYS";
        ConditionalSystemPrefix = logixSysName + "_SGC_";
    }

    SensorTableModel _sensorModel;
    JScrollPane _sensorScrollPane;
    JTextField _nameField;
    JList<String> _sensorGroupList;

    @Override
    public void initComponents() {
        addHelpMenu("package.jmri.jmrit.sensorgroup.SensorGroupFrame", true);

        setTitle(Bundle.getMessage("Title"));
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // add the sensor table
        JPanel p2xs = new JPanel();

        JPanel p21s = new JPanel();
        p21s.setLayout(new BoxLayout(p21s, BoxLayout.Y_AXIS));
        p21s.add(new JLabel(Bundle.getMessage("SensorTableLabel1")));
        p21s.add(new JLabel(Bundle.getMessage("SensorTableLabel2")));
        p21s.add(new JLabel(Bundle.getMessage("SensorTableLabel3")));
        p21s.add(new JLabel(Bundle.getMessage("SensorTableLabel4")));
        p2xs.add(p21s);
        _sensorModel = new SensorTableModel();
        JTable sensorTable = new JTable(_sensorModel);

        TableRowSorter<SensorTableModel> sorter = new TableRowSorter<>(_sensorModel);
        sorter.setComparator(SensorTableModel.SNAME_COLUMN, new jmri.util.AlphanumComparator());
        sorter.setComparator(SensorTableModel.UNAME_COLUMN, new jmri.util.AlphanumComparator());
        RowSorterUtil.setSortOrder(sorter, SensorTableModel.SNAME_COLUMN, SortOrder.ASCENDING);
        sensorTable.setRowSorter(sorter);

        sensorTable.setRowSelectionAllowed(false);
        sensorTable.setPreferredScrollableViewportSize(new java.awt.Dimension(450, 200));
        TableColumnModel sensorColumnModel = sensorTable.getColumnModel();
        TableColumn includeColumnS = sensorColumnModel.
                getColumn(SensorTableModel.INCLUDE_COLUMN);
        includeColumnS.setResizable(false);
        includeColumnS.setMinWidth(50);
        includeColumnS.setMaxWidth(60);
        TableColumn sNameColumnS = sensorColumnModel.
                getColumn(SensorTableModel.SNAME_COLUMN);
        sNameColumnS.setResizable(true);
        sNameColumnS.setMinWidth(75);
        sNameColumnS.setPreferredWidth(95);
        TableColumn uNameColumnS = sensorColumnModel.
                getColumn(SensorTableModel.UNAME_COLUMN);
        uNameColumnS.setResizable(true);
        uNameColumnS.setMinWidth(210);
        uNameColumnS.setPreferredWidth(260);

        rowHeight = sensorTable.getRowHeight();
        _sensorScrollPane = new JScrollPane(sensorTable);
        p2xs.add(_sensorScrollPane, BorderLayout.CENTER);
        getContentPane().add(p2xs);
        p2xs.setVisible(true);

        // add name field
        JPanel p3 = new JPanel();
        p3.add(new JLabel(Bundle.getMessage("GroupName")));
        _nameField = new JTextField(20);
        p3.add(_nameField);
        getContentPane().add(p3);

        // button
        JPanel p4 = new JPanel();
        JButton viewButton = new JButton(Bundle.getMessage("ButtonViewGroup"));
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPressed();
            }
        });
        p4.add(viewButton);
        JButton addButton = new JButton(Bundle.getMessage("ButtonMakeGroup"));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPressed();
            }
        });
        p4.add(addButton);
        JButton undoButton = new JButton(Bundle.getMessage("ButtonUndoGroup"));
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoGroupPressed();
            }
        });
        p4.add(undoButton);
        getContentPane().add(p4);

        JPanel p5 = new JPanel();

        DefaultListModel<String> groupModel = new DefaultListModel<>();
        // Look for Sensor group in Route table
        RouteManager rm = InstanceManager.getDefault(jmri.RouteManager.class);
        // List<String> routeList = rm.getSystemNameList();
        int i = 0;
        for (NamedBean obj : rm.getNamedBeanSet()) {
            String name = obj.getSystemName();
            if (name.startsWith(namePrefix)) {
                name = name.substring(namePrefix.length());
                String group = name.substring(0, name.indexOf(nameDivider));
                String prefix = namePrefix + group + nameDivider;
                do {
                    i++;
                    if (i >= rm.getNamedBeanSet().size()) {
                        break;
                    }
                    name = obj.getSystemName();
                } while (name.startsWith(prefix));
                groupModel.addElement(group);
            }
            i++;
        }
        // Look for Sensor group in Logix
        Logix logix = getSystemLogix();
        for (i = 0; i < logix.getNumConditionals(); i++) {
            String name = logix.getConditionalByNumberOrder(i);
            Conditional c = InstanceManager.getDefault(jmri.ConditionalManager.class).getBySystemName(name);
            String uname = null;
            if (c !=null) {
                uname = c.getUserName();
            }
            if (uname != null) {
                groupModel.addElement(uname.substring(ConditionalUserPrefix.length()));
            }
        }
        _sensorGroupList = new JList<>(groupModel);
        _sensorGroupList.setPrototypeCellValue(ConditionalUserPrefix + "XXXXXXXXXX");
        _sensorGroupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _sensorGroupList.setVisibleRowCount(5);
        JScrollPane scrollPane = new JScrollPane(_sensorGroupList);
        p5.add(scrollPane);
        p5.add(Box.createHorizontalStrut(10));
        JButton doneButton = new JButton(Bundle.getMessage("ButtonDone"));
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                donePressed(e);
            }
        });
        p5.add(doneButton);
        getContentPane().add(p5);

        // pack to cause display
        pack();
    }

    void addPressed() {
        deleteGroup(false);
        String group = _nameField.getText();
        if (group == null || group.length() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    Bundle.getMessage("MessageError1"), Bundle.getMessage("ErrorTitle"),
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        Logix logix = getSystemLogix();
        logix.deActivateLogix();
        String cSystemName = ConditionalSystemPrefix + group;
        String cUserName = ConditionalUserPrefix + group;
        // add new Conditional
        ArrayList<ConditionalVariable> variableList = new ArrayList<>();
        ArrayList<ConditionalAction> actionList = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < _sensorModel.getRowCount(); i++) {
            if ((Boolean) _sensorModel.getValueAt(i, BeanTableModel.INCLUDE_COLUMN)) {
                String sensor = (String) _sensorModel.getValueAt(i, BeanTableModel.UNAME_COLUMN);
                if (sensor == null || sensor.length() == 0) {
                    sensor = (String) _sensorModel.getValueAt(i, BeanTableModel.SNAME_COLUMN);
                }
                variableList.add(new ConditionalVariable(false, Conditional.Operator.OR,
                        Conditional.Type.SENSOR_ACTIVE, sensor, true));
                actionList.add(new DefaultConditionalAction(Conditional.ACTION_OPTION_ON_CHANGE_TO_TRUE,
                        Conditional.Action.SET_SENSOR, sensor,
                        Sensor.INACTIVE, ""));
                count++;
            }
        }
        if (count < 2) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    Bundle.getMessage("MessageError2"), Bundle.getMessage("ErrorTitle"),
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        Conditional c = new SensorGroupConditional(cSystemName, cUserName);
        c.setStateVariables(variableList);
        c.setLogicType(Conditional.AntecedentOperator.ALL_OR, "");
        c.setAction(actionList);
        logix.addConditional(cSystemName, 0);       // Update the Logix Conditional names list
        logix.addConditional(cSystemName, c);       // Update the Logix Conditional hash map
        logix.setEnabled(true);
        logix.activateLogix();
        ((DefaultListModel<String>) _sensorGroupList.getModel()).addElement(
                cUserName.substring(ConditionalUserPrefix.length()));
        clear();
    }

    void viewPressed() {
        for (int i = 0; i < _sensorModel.getRowCount(); i++) {
            _sensorModel.setValueAt(Boolean.FALSE, i, BeanTableModel.INCLUDE_COLUMN);
        }
        // look for name in List panel
        String group = _sensorGroupList.getSelectedValue();
        if (group == null) { // not there, look in text field
            group = _nameField.getText();
        }
        _nameField.setText(group);
        // Look for Sensor group in Route table
        RouteManager rm = InstanceManager.getDefault(jmri.RouteManager.class);
        String prefix = (namePrefix + group + nameDivider);
        boolean isRoute = false;
        int setRow = 0;
        for (Route r : rm.getNamedBeanSet()) {
            String name = r.getSystemName();
            if (name.startsWith(prefix)) {
                isRoute = true;
                String sensor = name.substring(prefix.length());
                // find and check that sensor
                for (int j = _sensorModel.getRowCount() - 1; j >= 0; j--) {
                    if (_sensorModel.getValueAt(j, BeanTableModel.SNAME_COLUMN).equals(sensor)) {
                        _sensorModel.setValueAt(Boolean.TRUE, j, BeanTableModel.INCLUDE_COLUMN);
                        setRow = j;
                    }
                }
            }
        }

        // look for  Sensor group in SYSTEM Logix
        if (!isRoute) {
            Logix logix = getSystemLogix();
            String cSystemName = (ConditionalSystemPrefix + group);
            String cUserName = ConditionalUserPrefix + group;
            for (int i = 0; i < logix.getNumConditionals(); i++) {
                String name = logix.getConditionalByNumberOrder(i);
                if (cSystemName.equalsIgnoreCase(name) || cUserName.equals(name)) {     // Ignore case for compatibility
                    Conditional c = InstanceManager.getDefault(jmri.ConditionalManager.class).getBySystemName(name);
                    if (c == null) {
                        log.error("Conditional \"{}\" expected but NOT found in Logix {}", name, logix.getSystemName());
                    } else {
                        List<ConditionalVariable> variableList = c.getCopyOfStateVariables();
                        for (int k = 0; k < variableList.size(); k++) {
                            String sensor = variableList.get(k).getName();
                            if (sensor != null) {
                                for (int j = _sensorModel.getRowCount() - 1; j >= 0; j--) {
                                    if (sensor.equals(_sensorModel.getValueAt(j, BeanTableModel.UNAME_COLUMN))
                                            || sensor.equals(_sensorModel.getValueAt(j, BeanTableModel.SNAME_COLUMN))) {
                                        _sensorModel.setValueAt(Boolean.TRUE, j, BeanTableModel.INCLUDE_COLUMN);
                                        setRow = j;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        _sensorModel.fireTableDataChanged();
        setRow -= 9;
        if (setRow < 0) {
            setRow = 0;
        }
        _sensorScrollPane.getVerticalScrollBar().setValue(setRow * rowHeight);
    }

    Logix getSystemLogix() {
        Logix logix = InstanceManager.getDefault(jmri.LogixManager.class).getBySystemName(logixSysName);
        if (logix == null) {
            logix = InstanceManager.getDefault(jmri.LogixManager.class).createNewLogix(logixSysName, logixUserName);
        }
        return logix;
    }

    void clear() {
        _sensorGroupList.getSelectionModel().clearSelection();
        _nameField.setText("");
        for (int i = 0; i < _sensorModel.getRowCount(); i++) {
            _sensorModel.setValueAt(Boolean.FALSE, i, BeanTableModel.INCLUDE_COLUMN);
        }
        _sensorModel.fireTableDataChanged();
    }

    void donePressed(ActionEvent e) {
        _sensorModel.dispose();
        dispose();
    }

    void deleteGroup(boolean showMsg) {
        String group = _nameField.getText();

        if (group == null || group.isEmpty()) {
            if (showMsg) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        Bundle.getMessage("MessageError3"), Bundle.getMessage("ErrorTitle"),
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        // remove the old routes
        String prefix = (namePrefix + group + nameDivider);
        RouteManager rm = InstanceManager.getDefault(jmri.RouteManager.class);
        for (Route r : rm.getNamedBeanSet()) {
            String name = r.getSystemName();
            if (name.startsWith(prefix)) {
                // OK, kill this one
                r.deActivateRoute();
                rm.deleteRoute(r);
            }
        }

        // remove Logix IX:SYS conditional
        // Due to a change at 4.17.2, the system names are no longer forced to upper case.
        // Older SYS conditionals will have an upper case name, so the user name is an alternate name.
        Logix logix = getSystemLogix();
        String cSystemName = ConditionalSystemPrefix + group;
        String cUserName = ConditionalUserPrefix + group;

        for (int i = 0; i < logix.getNumConditionals(); i++) {
            String cdlName = logix.getConditionalByNumberOrder(i);
            Conditional cdl = logix.getConditional(cdlName);
            if (cdl != null) {
                if (cSystemName.equals(cdl.getSystemName()) || cUserName.equals(cdl.getUserName())) {
                    String[] msgs = logix.deleteConditional(cdlName);
                    if (msgs == null) {
                        break;
                    }

                    // Conditional delete failed or was vetoed
                    if (showMsg) {
                        javax.swing.JOptionPane.showMessageDialog(this,
                                Bundle.getMessage("MessageError41") + " " + msgs[0] + " (" + msgs[1] + ") "
                                + Bundle.getMessage("MessageError42") + " " + msgs[2] + " (" + msgs[3] + "), "
                                + Bundle.getMessage("MessageError43") + " " + msgs[4] + " (" + msgs[5] + "). "
                                + Bundle.getMessage("MessageError44"),
                                Bundle.getMessage("ErrorTitle"), javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                }
            } else {
                log.error("Conditional \"{}\" expected but NOT found in Logix {}", cdlName, logix.getSystemName());
                return;
            }
        }

        // remove from list
        DefaultListModel<String> model = (DefaultListModel<String>) _sensorGroupList.getModel();
        model.removeElement(group);
    }

    void undoGroupPressed() {
        deleteGroup(true);
        clear();
    }

    private final static Logger log = LoggerFactory.getLogger(SensorGroupFrame.class);
}
