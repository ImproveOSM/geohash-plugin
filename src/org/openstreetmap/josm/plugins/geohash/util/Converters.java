package org.openstreetmap.josm.plugins.geohash.util;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.ProjectionBounds;
import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


/**
 *
 *
 * @author laurad
 */
public class Converters {


    public static BoundingBox convertBoundsToBoundingBox(final Bounds bounds) {
        return new BoundingBox.Builder().south(Latitude.forDegrees(bounds.getMinLat()))
                .west(Longitude.forDegrees(bounds.getMinLon())).north(Latitude.forDegrees(bounds.getMaxLat()))
                .east(Longitude.forDegrees(bounds.getMaxLon())).build();
    }

    public static BoundingBox convertProjectionBoundsToBoundingBox(final ProjectionBounds bounds) {
        return new BoundingBox.Builder().south(Latitude.forDegrees(bounds.minNorth))
                .west(Longitude.forDegrees(bounds.minEast)).north(Latitude.forDegrees(bounds.maxNorth))
                .east(Longitude.forDegrees(bounds.maxEast)).build();

    }
}
