package org.openstreetmap.josm.plugins.geohash.core;

import java.util.Collection;
import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


/**
 *
 *
 * @author Mihai Chintoanu
 */
class Example {

    public static void main(String[] args) {
        final BoundingBox boundingBox = new BoundingBox.Builder().south(Latitude.forDegrees(65.56641)).
                west(Longitude.forDegrees(-151.17187)).north(Latitude.forDegrees(65.74218)).
                east(Longitude.forDegrees(-150.82032)).build();
        final Collection<Geohash> geohashes = new GeohashIdentifier().get(boundingBox);
        System.out.println(geohashes);
    }
}
