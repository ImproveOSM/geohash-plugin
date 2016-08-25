package org.openstreetmap.josm.plugins.geohash.util;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MapView;
import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


public final class Util {

    private static final int MIN_ZOOM = 0;
    private static final int MAX_ZOOM = 18;
    private static final int TILE_SIZE = 1024;

    private Util() {}


    /**
     * Returns the zoom level based on the given bounds.
     *
     * @param bounds the map bounds
     * @return an integer
     */
    public static int zoom(final Bounds bounds) {
        return ((int) Math.min(MAX_ZOOM, Math.max(MIN_ZOOM,
                Math.round(Math.floor(Math.log(TILE_SIZE / bounds.asRect().height) / Math.log(2))))));
    }

    public static BoundingBox buildBoundingBox(final MapView mapView) {
        final Bounds bounds = mapView.getRealBounds();
        // new Bounds(mapView.getLatLon(0, mapView.getHeight()), mapView.getLatLon(mapView.getWidth(), 0));
        final BoundingBox.Builder builder = new BoundingBox.Builder().north(Latitude.forDegrees(bounds.getMax().lat()));
        builder.south(Latitude.forDegrees(bounds.getMin().lat()));
        builder.east(Longitude.forDegrees(bounds.getMax().lon()));
        builder.west(Longitude.forDegrees(bounds.getMin().lon()));
        return builder.build();
    }
}