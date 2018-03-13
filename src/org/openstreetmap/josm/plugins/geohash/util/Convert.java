package org.openstreetmap.josm.plugins.geohash.util;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MainApplication;
import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


/**
 * The class appeared from the need to convert between JOSM area objects and Geohash core area objects.
 *
 * @author laurad
 */
public final class Convert {

    public static final int MIN_ZOOM = 0;
    public static final int MAX_ZOOM = 25;
    private static final int TILE_SIZE = 1024;
    private static final int ZOOM1_SCALE = 78206;
    private static final int ZOOM_CONST = 2;
    private static final double JOSM_MAX_LATITUDE = 85.05112877980659;
    private static final double JOSM_MIN_LATITUDE = -85.05112877980659;

    private Convert() {}


    /**
     * This method takes a JOSM Bounds objects and converts it to a Geohash BoundingBox required as input to Geohash
     * creation
     *
     * @param Bounds
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
     * @param BoundingBox
     * @return Bounds
     */
    public static Bounds convertBoundingBoxToBounds(final BoundingBox bounds) {
        return new Bounds(bounds.south().asDegrees(), bounds.west().asDegrees(), bounds.north().asDegrees(),
                bounds.east().asDegrees());
    }

    /**
     * Computes a zoom level based on the given bounds, the closest zoom level being 25. These zoom levels are used to
     * determine which geohash codes to be displayed and which not.
     *
     * @param bounds
     * @return zoomLevel
     */
    public static int boundsToZoomLevel(final Bounds bounds) {
        return MainApplication.getMap().mapView.getScale() >= ZOOM1_SCALE ? 1 : (int) Math.min(MAX_ZOOM,
                Math.max(MIN_ZOOM, Math.round(Math.log(TILE_SIZE / bounds.asRect().height) / Math.log(ZOOM_CONST))));
    }

    /**
     * The JOSM map latitude is between ~85 and ~-85, so everything outside the bounds must be clipped. This method
     * return a latitude value fitted to JOSM bounds.
     *
     * @param latitude
     * @return
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