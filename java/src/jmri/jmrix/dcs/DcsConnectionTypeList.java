package jmri.jmrix.dcs;

import jmri.jmrix.ConnectionTypeList;
import org.openide.util.lookup.ServiceProvider;

import javax.annotation.Nonnull;

@ServiceProvider(service = ConnectionTypeList.class)
public class DcsConnectionTypeList implements ConnectionTypeList {

    public static final String MTH = "MTH DCS";
    @Nonnull
    @Override
    public String[] getAvailableProtocolClasses() {
        return new String[]{
                "jmri.jmrix.tmcc.serialdriver.ConnectionConfig",
                "jmri.jmrix.tmcc.simulator.ConnectionConfig"
        };
    }

    @Nonnull
    @Override
    public String[] getManufacturers() {
        return new String[]{MTH};
    }

}
