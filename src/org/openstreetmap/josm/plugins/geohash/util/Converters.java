package org.openstreetmap.josm.plugins.geohash.util;

import org.openstreetmap.josm.data.Bounds;
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

    public static Bounds convertBoundingBoxToBounds(final BoundingBox bounds) {
        return new Bounds(bounds.south().asDegrees(), bounds.west().asDegrees(), bounds.north().asDegrees(),
                bounds.east().asDegrees());
    }

}
