package org.openstreetmap.josm.plugins.geohash.util.cnf;

import javax.swing.Icon;
import org.openstreetmap.josm.tools.ImageProvider;


/**
 *
 * @author Beata
 * @version $Revision$
 */
public class IconConfig extends BaseConfig {

    private static final IconConfig INSTANCE = new IconConfig();
    private static final String CONFIG_FILE = "geohash_icon.properties";

    private final String pluginIconName;
    private final Icon layerIcon;


    private IconConfig() {
        super(CONFIG_FILE);

        pluginIconName = readProperty("plugin.icon");
        layerIcon = ImageProvider.get(readProperty("layer.icon"));
    }


    public static IconConfig getInstance() {
        return INSTANCE;
    }

    public String getPluginIconName() {
        return pluginIconName;
    }

    public Icon getLayerIcon() {
        return layerIcon;
    }
}