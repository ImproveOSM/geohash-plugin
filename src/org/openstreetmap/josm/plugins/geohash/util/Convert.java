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
public class Convert {


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

}
