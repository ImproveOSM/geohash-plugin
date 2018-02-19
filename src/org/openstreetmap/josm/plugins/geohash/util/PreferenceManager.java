/*
 *  Copyright 2018 Telenav, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.openstreetmap.josm.plugins.geohash.util;

import org.openstreetmap.josm.Main;

/**
 *
 * @author laurad
 * @version $Revision$
 */
public class PreferenceManager {

    private static String LAYER_OPENED = "geohash.layer.opened";
    private static PreferenceManager INSTANCE = new PreferenceManager();

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
