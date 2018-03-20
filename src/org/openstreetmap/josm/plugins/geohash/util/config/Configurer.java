/*
 * The code is licensed under the GPL Version 3 license https://www.gnu.org/licenses/quick-guide-gplv3.html.
 *
 * Copyright (c)2017, Telenav, Inc. All Rights Reserved
 */
package org.openstreetmap.josm.plugins.geohash.util.config;

import com.telenav.josm.common.cnf.BaseConfig;
import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


/**
 *
 *
 * @author laurad
 */
public final class Configurer extends BaseConfig {

    private final static String CONFIG_FILE = "geohash.properties";
    private final static Configurer INSTANCE = new Configurer();
    private final String pluginName;
    private final String pluginText;
    private final String layerIcon;
    private final String layerInfoComponent;
    private final String layerTooltipText;
    private final String dialogShortcutIcon;
    private final String dialogShortcutName;
    private final String dialogButtonName;
    private final String dialogLabelNotFound;

    private Configurer() {
        super(CONFIG_FILE);
        pluginName = readProperty("plugin.name");
        pluginText = readProperty("plugin.txt");
        layerIcon = readProperty("layer.icon");
        layerInfoComponent = readProperty("layer.info.component");
        layerTooltipText = readProperty("layer.tooltip.text");
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

    public String getLayerInfoComponent() {
        return layerInfoComponent;
    }

    public String getLayerTooltipText() {
        return layerTooltipText;
    }

    /**
     * Returns a bounding box corresponding to the JOSM world map.
     *
     * @return BoundingBox
     */
    public static BoundingBox getWorldBorder() {

        return new BoundingBox.Builder().south(Latitude.forDegrees(Latitude.MINIMUM_DEGREE_VALUE))
                .west(Longitude.forDegrees(Longitude.MINIMUM_DEGREE_VALUE))
                .north(Latitude.forDegrees(Latitude.MAXIMUM_DEGREE_VALUE))
                .east(Longitude.forDegrees(Longitude.MAXIMUM_DEGREE_VALUE)).build();
    }
}