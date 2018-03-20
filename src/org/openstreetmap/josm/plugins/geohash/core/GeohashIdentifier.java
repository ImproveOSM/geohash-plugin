/*
 * The code is licensed under the GPL Version 3 license https://www.gnu.org/licenses/quick-guide-gplv3.html.
 *
 * Copyright (c)2017, Telenav, Inc. All Rights Reserved
 */
package org.openstreetmap.josm.plugins.geohash.core;

import java.util.Collection;
import java.util.HashSet;
import net.exfidefortis.map.BoundingBox;


/**
 *
 *
 * @author Mihai Chintoanu
 */
public final class GeohashIdentifier {

    public final static int CUTT_OFF_DEPTH = 10;

    public static Collection<Geohash> get(final BoundingBox bounds) {
        final Collection<Geohash> finalGeohashes = new HashSet<>();
        Geohash currentParent = Geohash.WORLD;
        int depth = 0;
        while (finalGeohashes.isEmpty() && depth < CUTT_OFF_DEPTH) {
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