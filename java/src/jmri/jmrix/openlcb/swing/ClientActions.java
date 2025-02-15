package jmri.jmrix.openlcb.swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import jmri.jmrix.can.CanSystemConnectionMemo;
import jmri.util.JmriJFrame;
import org.openlcb.NodeID;
import org.openlcb.OlcbInterface;
import org.openlcb.cdi.impl.ConfigRepresentation;
import org.openlcb.cdi.swing.CdiPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared code for creating UI elements from different places in the application.
 * <p>
 * Created by bracz on 11/21/16.
 */

public class ClientActions {
    private final static Logger log = LoggerFactory.getLogger(ClientActions.class);
    private final OlcbInterface iface;
    private final CanSystemConnectionMemo memo;

    public ClientActions(OlcbInterface iface, CanSystemConnectionMemo memo) {
        this.iface = iface;
        this.memo = memo;
    }

    public void openCdiWindow(NodeID destNode, String description) {
//        final java.util.ArrayList<JButton> readList = new java.util.ArrayList<>();
        final java.util.ArrayList<JButton> sensorButtonList = new java.util.ArrayList<>();
        final java.util.ArrayList<JButton> turnoutButtonList = new java.util.ArrayList<>();

        JmriJFrame f = new JmriJFrame();
        f.setTitle(Bundle.getMessage("CdiPanelConfigure", description));
        f.setLayout(new javax.swing.BoxLayout(f.getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        CdiPanel m = new CdiPanel();
        f.add(m);
        m.setEventTable(iface.getNodeStore().getSimpleNodeIdent(destNode).getUserName(),
                iface.getEventTable());
        // create an object to add "New Sensor" buttons
        CdiPanel.GuiItemFactory factory = new CdiPanel.GuiItemFactory() {
            private boolean haveButtons = false;
            @Override
            public JButton handleReadButton(JButton button) {
//                readList.add(button);
                return button;
            }

            @Override
            public JButton handleWriteButton(JButton button) {
                return button;
            }

            @Override
            public void handleGroupPaneStart(JPanel pane) {
                this.gpane = pane;
                evt1 = null;
                evt2 = null;
                desc = null;
            }

            @Override
            public void handleGroupPaneEnd(JPanel pane) {
                if (gpane != null && evt1 != null && evt2 != null && desc != null) {
                    JPanel p = new JPanel();
                    p.setLayout(new FlowLayout());
                    p.setAlignmentX(-1.0f);
                    pane.add(p);
                    JButton button = new JButton(Bundle.getMessage("CdiPanelMakeSensor"));
                    p.add(button);
                    sensorButtonList.add(button);
                    button.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            jmri.Sensor sensor = jmri.InstanceManager.sensorManagerInstance()
                                    .provideSensor(memo.getSystemPrefix() + "S" + mevt1.getText() + ";" + mevt2.getText());
                            if (mdesc.getText().length() > 0) {
                                sensor.setUserName(mdesc.getText());
                            }
                            log.info("make sensor MS{};{} [{}]", mevt1.getText(), mevt2.getText(), mdesc.getText());
                        }

                        final JTextField mdesc = desc;
                        final JFormattedTextField mevt1 = evt1;
                        final JFormattedTextField mevt2 = evt2;
                    });
                    button = new JButton(Bundle.getMessage("CdiPanelMakeTurnout"));
                    p.add(button);
                    turnoutButtonList.add(button);
                    button.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            jmri.Turnout turnout = jmri.InstanceManager.turnoutManagerInstance()
                                    .provideTurnout(memo.getSystemPrefix() + "T" + mevt1.getText() + ";" + mevt2.getText());
                            if (mdesc.getText().length() > 0) {
                                turnout.setUserName(mdesc.getText());
                            }
                            log.info("make turnout MT{};{} [{}]", mevt1.getText(), mevt2.getText(), mdesc.getText());
                        }

                        final JTextField mdesc = desc;
                        final JFormattedTextField mevt1 = evt1;
                        final JFormattedTextField mevt2 = evt2;
                    });
                    if (!haveButtons) {
                        haveButtons = true;
                        m.addButtonToFooter(buttonForList(sensorButtonList, Bundle.getMessage("CdiPanelMakeAllSensors")));
                        m.addButtonToFooter(buttonForList(turnoutButtonList, Bundle.getMessage("CdiPanelMakeAllTurnouts")));
                    }
                    gpane = null;
                    evt1 = null;
                    evt2 = null;
                    desc = null;
                }
            }

            @Override
            public JFormattedTextField handleEventIdTextField(JFormattedTextField field) {
                if (evt1 == null) {
                    evt1 = field;
                } else if (evt2 == null) {
                    evt2 = field;
                } else {
                    gpane = null;  // flag too many
                }
                return field;
            }

            @Override
            public JTextField handleStringValue(JTextField value) {
                desc = value;
                return value;
            }

            @Override
            /**
             * Make a sensor from a single Event ID.
             * Set the user name from the CDI description (if available)
             * {@inheritDoc}
             */
            public void makeSensor(String ev, String mdesc) {
                jmri.Sensor sensor =
                        jmri.InstanceManager.sensorManagerInstance()
                                .provideSensor(memo.getSystemPrefix() + "S" + ev);
                if (mdesc.length() > 0) {
                    sensor.setUserName(mdesc);
                }
                log.debug("make sensor MS{} [{}]", ev, mdesc);
            }

            JPanel gpane = null;
            JTextField desc = null;
            JFormattedTextField evt1 = null;
            JFormattedTextField evt2 = null;
        };
        ConfigRepresentation rep = iface.getConfigForNode(destNode);

        m.initComponents(rep, factory);

        f.pack();
        f.setVisible(true);
    }

    JButton buttonForList(final java.util.ArrayList<JButton> list, String label) {
        JButton b = new JButton(label);
        b.addActionListener(e -> {
            int delay = 0; //milliseconds
            for (final JButton b1 : list) {

                ActionListener taskPerformer = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        target.doClick();
                    }
                    final JButton target = b1;
                };
                Timer t = new Timer(delay, taskPerformer);
                t.setRepeats(false);
                t.start();
                delay = delay + 150;
            }
        });
        return b;
    }

}
