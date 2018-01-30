package org.openstreetmap.josm.plugins.geohash.util.config;

import com.telenav.josm.common.cnf.BaseConfig;


/**
 *
 *
 * @author laurad
 */
public class Configurer extends BaseConfig {

    private static String CONFIG_FILE = "geohash.properties";
    private static Configurer INSTANCE = new Configurer();
    private final String pluginName;
    private final String pluginText;
    private final String layerIcon;
    private final String dialogShortcutIcon;
    private final String dialogShortcutName;
    private final String dialogButtonName;
    private final String dialogLabelNotFound;

    private Configurer() {
        super(CONFIG_FILE);
        pluginName = readProperty("plugin.name");
        pluginText = readProperty("plugin.txt");
        layerIcon = readProperty("layer.icon");
        dialogShortcutIcon = readProperty("dialog.shortcut.icon");
        dialogShortcutName = readProperty("dialog.shortcut.name");
        dialogButtonName = readProperty("dialog.button.name");
        dialogLabelNotFound = readProperty("dialog.label.text");
    }


    public static Configurer getINSTANCE() {
        return INSTANCE;
    }


    public String getPluginName() {
        return pluginName;
    }


    public String getPluginText() {
        return pluginText;
    }


    public String getLayerIcon() {
        return layerIcon;
    }


    public String getDialogShortcutIcon() {
        return dialogShortcutIcon;
    }


    public String getDialogShortcutName() {
        return dialogShortcutName;
    }


    public String getDialogButtonName() {
        return dialogButtonName;
    }

    public String getDialogLabelNotFound() {
        return dialogLabelNotFound;
    }


}
