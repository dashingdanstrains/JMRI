/*============================================================================*
 * WARNING      This class contains automatically modified code.      WARNING *
 *                                                                            *
 * The method initComponents() and the variable declarations between the      *
 * "// Variables declaration - do not modify" and                             *
 * "// End of variables declaration" comments will be overwritten if modified *
 * by hand. Using the NetBeans IDE to edit this file is strongly recommended. *
 *                                                                            *
 * See http://jmri.org/help/en/html/doc/Technical/NetBeansGUIEditor.shtml for *
 * more information.                                                          *
 *============================================================================*/
package jmri.profile;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import jmri.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Display a list of {@link Profile}s that can be selected to start a JMRI
 * application.
 * <p>
 * This dialog is designed to be displayed while an application is starting. If
 * the last profile used for the application can be found, this dialog will
 * automatically start the application with that profile after 10 seconds unless
 * the user intervenes.
 *
 * @author Randall Wood
 */
public class ProfileManagerDialog extends JDialog {

    private Timer timer;
    private int countDown;
    private boolean disableTimer;

    /**
     * Creates new form ProfileManagerDialog
     *
     * @param parent The frame containing this dialog
     * @param modal The modal parameter for parent JDialog
     */
    public ProfileManagerDialog(Frame parent, boolean modal) {
        this(parent, modal, false);
    }

    /**
     * Creates new form ProfileManagerDialog
     *
     * @param parent The frame containing this dialog
     * @param modal The modal parameter for parent JDialog
     * @param disableTimer true if the timer should be disabled
     */
    public ProfileManagerDialog(Frame parent, boolean modal, boolean disableTimer) {
        super(parent, modal);
        this.disableTimer = disableTimer;
        initComponents();
        ProfileManager.getDefault().addPropertyChangeListener(ProfileManager.ACTIVE_PROFILE, (PropertyChangeEvent evt) -> {
            profiles.setSelectedValue(ProfileManager.getDefault().getActiveProfile(), true);
            profiles.ensureIndexIsVisible(profiles.getSelectedIndex());
            profiles.repaint();
        });
        ProfileManager.getDefault().addPropertyChangeListener(Profile.NAME, (PropertyChangeEvent evt) -> {
            if (evt.getSource().getClass().equals(Profile.class) && evt.getPropertyName().equals(Profile.NAME)) {
                profileNameChanged(((Profile) evt.getSource()));
            }
        });
        this.jScrollPane1.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            profilesValueChanged(null);
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
     // This uses the deprecated {@link JComponent#setNextFocusableComponent} method.
     // Because it's autogenerated code, we leave that in place for now.
    @SuppressWarnings( "deprecation" ) // JComponent#setNextFocusableComponent
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listLabel = new JLabel();
        jScrollPane1 = new JScrollPane();
        profiles = new JList<>();
        btnSelect = new JButton();
        btnCreate = new JButton();
        btnUseExisting = new JButton();
        countDownLbl = new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getMessage("ProfileManagerDialog.title")); // NOI18N
        setMinimumSize(new Dimension(310, 110));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent evt) {
                formWindowOpened(evt);
            }
            @Override
            public void windowClosed(WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        listLabel.setText(Bundle.getMessage("ProfileManagerDialog.listLabel.text")); // NOI18N

        profiles.setModel(new ProfileListModel());
        profiles.setSelectedValue(ProfileManager.getDefault().getActiveProfile(), true);
        profiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profiles.setToolTipText(Bundle.getMessage("ProfileManagerDialog.profiles.toolTipText")); // NOI18N
        profiles.setCellRenderer(new ProfileListCellRenderer());


        profiles.setNextFocusableComponent(btnSelect);


        profiles.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                profilesKeyPressed(evt);
            }
        });
        profiles.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                profilesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(profiles);
        profiles.ensureIndexIsVisible(profiles.getSelectedIndex());
        profiles.getAccessibleContext().setAccessibleName(Bundle.getMessage("ProfileManagerDialog.profiles.AccessibleContext.accessibleName")); // NOI18N
        profiles.getAccessibleContext().setAccessibleDescription(Bundle.getMessage("ProfileManagerDialog.profiles.toolTipText")); // NOI18N

        btnSelect.setText(Bundle.getMessage("ProfileManagerDialog.btnSelect.text")); // NOI18N
        btnSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        btnCreate.setText(Bundle.getMessage("ProfileManagerDialog.btnCreate.text")); // NOI18N
        btnCreate.setToolTipText(Bundle.getMessage("ProfilePreferencesPanel.btnCreateNewProfile.toolTipText")); // NOI18N
        btnCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUseExisting.setText(Bundle.getMessage("ProfileManagerDialog.btnUseExisting.text")); // NOI18N
        btnUseExisting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnUseExistingActionPerformed(evt);
            }
        });

        countDownLbl.setText(Bundle.getMessage("ProfileManagerDialog.countDownLbl.text")); // NOI18N
        countDownLbl.setToolTipText(Bundle.getMessage("ProfileManagerDialog.countDownLbl.toolTipText")); // NOI18N

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(listLabel)
                    .addComponent(countDownLbl))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnUseExisting)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCreate)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelect))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                    .addComponent(listLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelect)
                    .addComponent(btnCreate)
                    .addComponent(btnUseExisting)
                    .addComponent(countDownLbl))
                .addContainerGap())
        );

        listLabel.getAccessibleContext().setAccessibleName(Bundle.getMessage("ProfileManagerDialog.listLabel.text")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelectActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        timer.stop();
        countDown = -1;
        countDownLbl.setVisible(false);
        if (profiles.getSelectedValue() != null) {
            ProfileManager.getDefault().setActiveProfile(profiles.getSelectedValue());
            dispose();
        }
    }//GEN-LAST:event_btnSelectActionPerformed

    private void btnCreateActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        timer.stop();
        countDownLbl.setVisible(false);
        AddProfileDialog apd = new AddProfileDialog(this, true, false);
        apd.setLocationRelativeTo(this);
        apd.setVisible(true);
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUseExistingActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnUseExistingActionPerformed
        timer.stop();
        countDownLbl.setVisible(false);
        JFileChooser chooser = new jmri.util.swing.JmriJFileChooser(FileUtil.getHomePath());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new ProfileFileFilter());
        chooser.setFileView(new ProfileFileView());
        // TODO: Use NetBeans OpenDialog if its availble
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Profile p = new Profile(chooser.getSelectedFile());
                ProfileManager.getDefault().addProfile(p);
                profiles.setSelectedValue(p, true);
            } catch (IOException ex) {
                log.warn("{} is not a profile directory", chooser.getSelectedFile());
                // TODO: Display error dialog - selected file is not a profile directory
            }
        }
    }//GEN-LAST:event_btnUseExistingActionPerformed

    private void formWindowOpened(WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        countDown = ProfileManager.getDefault().getAutoStartActiveProfileTimeout();
        if (disableTimer) {
            countDownLbl.setText("");
        } else {
            countDownLbl.setText(Integer.toString(countDown));
        }
        timer = new Timer(1000, (ActionEvent e) -> {
            if (disableTimer) {
                return;
            }
            if (countDown > 0) {
                countDown--;
                countDownLbl.setText(Integer.toString(countDown));
            } else {
                setVisible(false);
                Profile profile = profiles.getSelectedValue();
                ProfileManager.getDefault().setActiveProfile(profile);
                if (profile != null) {
                    log.info("Automatically starting with profile {} after timeout.", profile.getId());
                } else {
                    log.info("Automatically starting without a profile");
                }
                timer.stop();
                countDown = -1;
                dispose();
            }
        });
        timer.setRepeats(true);
        if (profiles.getModel().getSize() > 0
                && null != ProfileManager.getDefault().getActiveProfile()
                && countDown > 0) {
            timer.start();
        } else {
            countDownLbl.setVisible(false);
            btnSelect.setEnabled(false);
        }
    }//GEN-LAST:event_formWindowOpened

    /**
     * Get the active profile or display a dialog to prompt the user for it.
     *
     * @param f  The {@link java.awt.Frame} to display the dialog over
     * @return the active or selected {@link Profile}
     * @throws java.io.IOException if unable to read or set the starting Profile
     * @see ProfileManager#getStartingProfile()
     */
    public static Profile getStartingProfile(Frame f) throws IOException {
        ProfileManager manager = ProfileManager.getDefault();
        if (ProfileManager.getStartingProfile() == null
                || (System.getProperty(ProfileManager.SYSTEM_PROPERTY) == null
                && !manager.isAutoStartActiveProfile())) {
            Profile last = manager.getActiveProfile();
            ProfileManagerDialog pmd = new ProfileManagerDialog(f, true);
            pmd.setLocationRelativeTo(f);
            pmd.setVisible(true);
            if (last == null || !last.equals(manager.getActiveProfile())) {
                manager.saveActiveProfile();
            }
        }
        return manager.getActiveProfile();
    }

    private void profileNameChanged(Profile p) {
        p.save();
        log.info("Saving profile {}", p.getId());
    }

    private void profilesValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_profilesValueChanged
        timer.stop();
        countDownLbl.setVisible(false);
        btnSelect.setEnabled(true);
    }//GEN-LAST:event_profilesValueChanged

    private void formMousePressed(MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        this.profilesValueChanged(null);
    }//GEN-LAST:event_formMousePressed

    private void profilesKeyPressed(KeyEvent evt) {//GEN-FIRST:event_profilesKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.btnSelect.doClick();
        }
    }//GEN-LAST:event_profilesKeyPressed

    @SuppressFBWarnings(value = "DM_EXIT", justification = "This exit ensures launch is aborted if a profile is not selected or autostarted")
    private void formWindowClosed(WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        if (countDown != -1) {
            // prevent an attempt to reclose this window from blocking application exit
            countDown = -1;
            // exit with an error code to indicate abnormal exit
            System.exit(255);
        }
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnCreate;
    private JButton btnSelect;
    private JButton btnUseExisting;
    private JLabel countDownLbl;
    private JScrollPane jScrollPane1;
    private JLabel listLabel;
    private JList<Profile> profiles;
    // End of variables declaration//GEN-END:variables
    private static final Logger log = LoggerFactory.getLogger(ProfileManagerDialog.class);
}
