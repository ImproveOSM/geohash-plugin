package org.openstreetmap.josm.plugins.geohash.util.config;

import java.util.HashMap;
import java.util.Map;
import org.openstreetmap.josm.plugins.geohash.util.Convert;
import com.telenav.josm.common.cnf.BaseConfig;
import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


/**
 *
 *
 * @author laurad
 */
public class Configurer extends BaseConfig {

    private static final int MAX_VISIBILITY_LVL = 15;
    private final static int ZOOM_STEP_LVL_3 = 3;
    private final static int ZOOM_STEP_LVL_2 = 2;
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
     * Computes a map containing zoom levels as keys and code lengths as values. Only codes with length equal or smaller
     * will be written for each zoom level. This prevents the drawing of unreadable text.
     *
     * @return Map<Integer, Integer>
     */
    public Map<Integer, Integer> getCodeVizibilityLevels() {
        final Map<Integer, Integer> visibilityLevels = new HashMap<>();
        int codeLength = 1;
        for (int i = 1; i <= Convert.MAX_ZOOM; i++) {
            visibilityLevels.put(i, codeLength);
            if (i <= MAX_VISIBILITY_LVL) {
                if (i % ZOOM_STEP_LVL_3 == 0) {
                    codeLength++;
                }
            } else {
                if (i % ZOOM_STEP_LVL_2 != 0) {
                    codeLength++;
                }
            }
        }
        /** Noticed exceptions in levels */
        visibilityLevels.replace(15, 6);
        return visibilityLevels;
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