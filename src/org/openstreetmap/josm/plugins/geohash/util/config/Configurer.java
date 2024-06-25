/*
 * Copyright 2019 Grabtaxi Holdings PTE LTE (GRAB), All rights reserved.
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file.
 *
 */
package org.openstreetmap.josm.plugins.geohash.util.config;

import com.grab.josm.common.cnf.BaseConfig;


/**
 * Class reading the configuration property file for the Geohash plug-in.
 *
 * @author laurad
 */
public final class Configurer extends BaseConfig {

    private final static String CONFIG_FILE = "geohash.properties";
    private final static Configurer INSTANCE = new Configurer();

    private Configurer() {
        super(CONFIG_FILE);
    }


    public static Configurer getINSTANCE() {
        return INSTANCE;
    }

    public String getPluginName() {
        return readProperty("plugin.name");
    }

    public String getLayerIcon() {
        return readProperty("layer.icon");
    }

    public String getDialogShortcutIcon() {
        return readProperty("dialog.shortcut.icon");
    }

    public String getDialogShortcutName() {
        return readProperty("dialog.shortcut.name");
    }

    public String getDialogButtonName() {
        return readProperty("dialog.button.name");
    }

    public String getDialogLabelNotFound() {
        return readProperty("dialog.label.text");
    }

    public String getLayerInfoComponent() {
        return readProperty("layer.info.component");
    }

    public String getLayerTooltipText() {
        return readProperty("layer.tooltip.text");
    }

    public String getEnableZoomFreezeText() {
        return readProperty("menu.zoomfreeze.enable.text");
    }

    public String getDisableZoomFreezeText() {
        return readProperty("menu.zoomfreeze.disable.text");
    }

    public String getDisplayLargerGeohashesText() {
        return readProperty("menu.display.larger.geohashes.text");
    }

    public String getDisplaySmallerGeohashesText() {
        return readProperty("menu.display.smaller.geohashes.text");
    }


}