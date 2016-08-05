package org.openstreetmap.josm.plugins.geohash.util.cnf;

import com.telenav.josm.common.cnf.BaseConfig;


/**
 *
 * @author Beata
 * @version $Revision$
 */
public class GuiConfig extends BaseConfig {

    private static final GuiConfig INSTANCE = new GuiConfig();
    private static final String CONFIG_FILE = "geohash_gui.properties";

    private final String pluginShortName;
    private final String pluginLongName;
    private final String pluginTlt;


    private GuiConfig() {
        super(CONFIG_FILE);

        pluginShortName = readProperty("plugin.name.short");
        pluginLongName = readProperty("plugin.name.long");
        pluginTlt = readProperty("plugin.tlt");
    }


    public static GuiConfig getInstance() {
        return INSTANCE;
    }

    public String getPluginShortName() {
        return pluginShortName;
    }

    public String getPluginLongName() {
        return pluginLongName;
    }

    public String getPluginTlt() {
        return pluginTlt;
    }
}