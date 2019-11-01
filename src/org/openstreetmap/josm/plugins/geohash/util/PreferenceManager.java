/*
 * Copyright 2019 Grabtaxi Holdings PTE LTE (GRAB), All rights reserved.
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file.
 *
 */
package org.openstreetmap.josm.plugins.geohash.util;

import org.openstreetmap.josm.spi.preferences.Config;


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
        final String layerOpened = Config.getPref().get(LAYER_OPENED);
        return layerOpened.isEmpty() ? false : Boolean.valueOf(layerOpened);
    }

    public void setLayerOpenedFlag(final Boolean isOpened) {
        Config.getPref().put(LAYER_OPENED, isOpened.toString());
    }
}