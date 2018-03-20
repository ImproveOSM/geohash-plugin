/*
 * The code is licensed under the GPL Version 3 license https://www.gnu.org/licenses/quick-guide-gplv3.html.
 *
 * Copyright (c)2017, Telenav, Inc. All Rights Reserved
 */
package org.openstreetmap.josm.plugins.geohash.util;

import org.openstreetmap.josm.Main;

/**
 *
 * @author laurad
 * @version $Revision$
 */
public final class PreferenceManager {

    private static final String LAYER_OPENED = "geohash.layer.opened";
    private static final PreferenceManager INSTANCE = new PreferenceManager();

    private PreferenceManager() {}

    public static PreferenceManager getInstance() {
        return INSTANCE;
    }

    public boolean loadLayerOpenedFlag() {
        final String layerOpened = Main.pref.get(LAYER_OPENED);
        return layerOpened.isEmpty() ? false : Boolean.valueOf(layerOpened);
    }

    public void setLayerOpenedFlag(final Boolean isOpened) {
        Main.pref.put(LAYER_OPENED, isOpened.toString());
    }
}