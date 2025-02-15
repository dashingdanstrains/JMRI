package jmri.jmrix.can.cbus.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import jmri.InstanceManager;
import jmri.jmrix.can.CanSystemConnectionMemo;
import jmri.jmrix.can.TrafficControllerScaffold;
import jmri.jmrix.can.cbus.eventtable.CbusEventTableDataModel;
import jmri.jmrix.can.cbus.swing.eventtable.CbusEventTablePane;
import jmri.util.JUnitUtil;
import jmri.util.JmriJFrame;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;

/**
 * Test simple functioning of CbusNewEventPane
 *
 * @author Paul Bender Copyright (C) 2016
 * @author Steve Young Copyright (C) 2020
*/
@DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
public class CbusNewEventPaneTest  {

    @Test
    public void testInitComponents() throws Exception{
        // for now, just makes sure there isn't an exception.
        CbusNewEventPane t = new CbusNewEventPane(mainPanel);
        assertThat(t).isNotNull();
    }

    @Test
    public void testPersistenceEvNumNodeNum() throws Exception{
        
        CbusNewEventPane t = new CbusNewEventPane(mainPanel);
        JmriJFrame f = new JmriJFrame();
        f.add(t);
        f.setTitle(t.getClass().getName());
        f.pack();
        f.setVisible(true);
        JFrameOperator jfo = new JFrameOperator( t.getClass().getName() );
        
        assertThat(new JTextFieldOperator(jfo,0).getText()).isEqualTo("0");
        assertThat(new JTextFieldOperator(jfo,1).getText()).isEqualTo("1");
        
        new JTextFieldOperator(jfo,0).setText("123"); // nn
        new JTextFieldOperator(jfo,1).setText("456"); // en
        f.dispose();
        jfo.dispose();
        
        CbusNewEventPane tt = new CbusNewEventPane(mainPanel);
        JmriJFrame ff = new JmriJFrame();
        ff.add(tt);
        ff.setTitle(tt.getClass().getName());
        ff.pack();
        ff.setVisible(true);
        JFrameOperator jffo = new JFrameOperator( tt.getClass().getName() );
        
        assertThat(new JTextFieldOperator(jffo,0).getText()).isEqualTo("123");
        assertThat(new JTextFieldOperator(jffo,1).getText()).isEqualTo("456");
        
        // new JButtonOperator(jfo, "Not a Button").doClick();  // NOI18N
        ff.dispose();
        jffo.dispose();
    }

    private CanSystemConnectionMemo memo = null;
    private TrafficControllerScaffold tc = null;
    private CbusEventTablePane mainPanel = null;

    @BeforeEach
    public void setUp(@TempDir File tempDir) {
        JUnitUtil.setUp();
        try {
            JUnitUtil.resetProfileManager( new jmri.profile.NullProfile( tempDir));
        } catch ( java.io.IOException e) {
            fail("Exception creating temp. user folder");
        }

        memo = new CanSystemConnectionMemo();
        tc = new TrafficControllerScaffold();
        memo.setTrafficController(tc);
        memo.setProtocol(jmri.jmrix.can.CanConfigurationManager.MERGCBUS);
        memo.configureManagers();
        mainPanel = new CbusEventTablePane();
        mainPanel.initComponents(memo);
        mainPanel.getMenus();

    }

    @AfterEach
    public void tearDown() {
        CbusEventTableDataModel dm = InstanceManager.getNullableDefault(CbusEventTableDataModel.class);
        if ( dm !=null ){
            dm.skipSaveOnDispose();
            dm.dispose();
        }

        Assertions.assertNotNull(mainPanel);
        mainPanel.dispose();
        Assertions.assertNotNull(tc);
        tc.terminateThreads();
        Assertions.assertNotNull(memo);
        memo.dispose();
        tc = null;
        memo = null;

        JUnitUtil.tearDown();
    }

}
