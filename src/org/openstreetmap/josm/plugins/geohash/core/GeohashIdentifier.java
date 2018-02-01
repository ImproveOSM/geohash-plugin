package org.openstreetmap.josm.plugins.geohash.core;

import java.util.Collection;
import java.util.HashSet;
import net.exfidefortis.map.BoundingBox;


/**
 *
 *
 * @author Mihai Chintoanu
 */
public class GeohashIdentifier {

    private final static int cutoffDepth = 10;

    /*
     * TODO Refine this simplistic approach once we see that it works.
     */
    public static Collection<Geohash> get(final BoundingBox bounds) {
        final Collection<Geohash> finalGeohashes = new HashSet<>();
        Geohash currentParent = Geohash.WORLD;
        int depth = 0;
        while (finalGeohashes.isEmpty() && depth < cutoffDepth) {
            final Collection<Geohash> candidateGeohashes = currentParent.children();
            Geohash nextCandidate = null;
            for (final Geohash candidate : candidateGeohashes) {
                if (candidate.bounds().contains(bounds)) {
                    nextCandidate = candidate;
                    break;
                }
            }
            if (nextCandidate == null) {
                finalGeohashes.addAll(candidateGeohashes);
            } else {
                currentParent = nextCandidate;
            }
            depth++;
        }
        return finalGeohashes;
    }
}
