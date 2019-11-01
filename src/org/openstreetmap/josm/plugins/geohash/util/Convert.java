/*
 * Copyright 2019 Grabtaxi Holdings PTE LTE (GRAB), All rights reserved.
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file.
 *
 */
package org.openstreetmap.josm.plugins.geohash.util;

import org.openstreetmap.josm.data.Bounds;
import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


/**
 * The class appeared from the need to convert between JOSM area objects and Geohash core area objects.
 *
 * @author laurad
 */
public final class Convert {

    private static final double JOSM_MAX_LATITUDE = 85.05112877980659;
    private static final double JOSM_MIN_LATITUDE = -85.05112877980659;

    private Convert() {}


    /**
     * This method takes a JOSM Bounds objects and converts it to a Geohash BoundingBox required as input to Geohash
     * creation
     *
     * @param bounds - the Bounds to be converted to BoundingBox
     * @return BoundingBox
     */
    public static BoundingBox convertBoundsToBoundingBox(final Bounds bounds) {
        return new BoundingBox.Builder().south(Latitude.forDegrees(bounds.getMinLat()))
                .west(Longitude.forDegrees(bounds.getMinLon())).north(Latitude.forDegrees(bounds.getMaxLat()))
                .east(Longitude.forDegrees(bounds.getMaxLon())).build();
    }

    /**
     * This method converts a Geohash BoundingBox to a JOSM Bounds. It is needed in order to represent the geohash on
     * map.
     *
     * @param bounds - the BoundingBox to be converted to Bounds
     * @return Bounds
     */
    public static Bounds convertBoundingBoxToBounds(final BoundingBox bounds) {
        return new Bounds(fitLatitudeInBounds(bounds.south().asDegrees()), bounds.west().asDegrees(),
                fitLatitudeInBounds(bounds.north().asDegrees()),
                bounds.east().asDegrees());
    }

    /**
     * The JOSM map latitude is between ~85 and ~-85, so everything outside the bounds must be clipped. This method
     * return a latitude value fitted to JOSM bounds.
     *
     * @param latitude - the latitude to be fit in the JOSM restriction
     * @return double - the latitude trimmed to fit in the JOSM map
     */
    public static double fitLatitudeInBounds(final double latitude) {
        if (latitude > JOSM_MAX_LATITUDE) {
            return JOSM_MAX_LATITUDE;
        }
        if (latitude < JOSM_MIN_LATITUDE) {
            return JOSM_MIN_LATITUDE;
        }
        return latitude;
    }
}