package jmri.jmrix.dccpp.swing;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import jmri.JmriException;
import jmri.script.ScriptEngineSelector;

/**
 * Menu action to Export all JMRI Roster Entries as ROSTER() macro calls
 *  This adds a menu item to the "DCC++" menu. Taking this option opens a 
 *  window where the ROSTER() lines can be copied into your myAutomation.h 
 *  Syntax: ROSTER(999,"Loco Name","F0/F1/*F2/F3/F4/F5/F6/F7/F8")
 * NOTE: calls existing script for compatibility
 * @author MSteveTodd Copyright (C) 2023
 */
public class DCCppRosterExportAction extends AbstractAction {

    private final ScriptEngineSelector _scriptEngineSelector = new ScriptEngineSelector();
    private final String scriptFilename = "program:jython"+File.separator+"DCC-EX"+File.separator+"RosterExportToDCC-EX.py";
    private static final Logger log = LoggerFactory.getLogger(DCCppRosterExportAction.class);
    
    public DCCppRosterExportAction(String s) {
        super(s);
    }

    public DCCppRosterExportAction() {
        this("Roster Export to DCC-EX");
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings( value="SLF4J_FORMAT_SHOULD_BE_CONST",
            justification="Same message is used in dialog")
    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("in actionPerformed");
        ScriptEngineSelector.Engine engine =
                    _scriptEngineSelector.getSelectedEngine();
        if (engine == null) {
            String em = "Script engine is null";
            log.error(em);
            JOptionPane.showMessageDialog(null,
                    em, "Script Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(jmri.util.FileUtil.getExternalFilename(scriptFilename)),
                    StandardCharsets.UTF_8)) {
            engine.getScriptEngine().eval(reader);
        } catch (IOException | ScriptException ex) {
            String em = "Cannot execute script: " + ex;
            log.error(em);
            JOptionPane.showMessageDialog(null,
                    em, "Script Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

}
