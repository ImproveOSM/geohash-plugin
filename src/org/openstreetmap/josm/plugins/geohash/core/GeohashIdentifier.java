/*
 * The code is licensed under the GPL Version 3 license https://www.gnu.org/licenses/quick-guide-gplv3.html.
 *
 * Copyright (c)2017, Telenav, Inc. All Rights Reserved
 */
package org.openstreetmap.josm.plugins.geohash.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

import net.exfidefortis.map.BoundingBox;


/**
 *
 *
 * @author Mihai Chintoanu
 */
public final class GeohashIdentifier {

    public static final GeohashIdentifier INSTANCE = new GeohashIdentifier(10, 0.3f);

    private final int cutOffDepth;
    private float maximumCoveragePercent;

    private GeohashIdentifier(final int cutOffDepth, final float maximumCoveragePercent) {
        this.cutOffDepth = cutOffDepth;
        this.maximumCoveragePercent = maximumCoveragePercent;
    }

    public int cutOffDepth() {
        return cutOffDepth;
    }

    public Collection<Geohash> get(final BoundingBox bounds) {
        Collection<Geohash> finalGeohashes = new HashSet<>();
        Geohash currentParent = Geohash.WORLD;
        int depth = 0;
        while (finalGeohashes.isEmpty() && depth < cutOffDepth) {
            final Collection<Geohash> candidateGeohashes = currentParent.children();
            final Iterator<Geohash> candidateGeohashesIterator = currentParent.children().iterator();
            Geohash nextCandidate = null;
            while (nextCandidate == null && candidateGeohashesIterator.hasNext()) {
                final Geohash candidate = candidateGeohashesIterator.next();
                if (candidate.bounds().contains(bounds)) {
                    nextCandidate = candidate;
                }
            }
            if (nextCandidate == null) {
                final Collection<Geohash> intersectingGeohashes = candidateGeohashes.stream()
                        .filter(geohash -> bounds.intersects(geohash.bounds()) || bounds.contains(geohash.bounds()))
                        .collect(Collectors.toSet());
                finalGeohashes.addAll(intersectingGeohashes);
            } else {
                currentParent = nextCandidate;
            }
            depth++;
        }
        return refine(finalGeohashes, bounds);
    }

    private Collection<Geohash> refine(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        Collection<Geohash> refinedGeohashes = geohashes;
        while (!acceptedCoverage(refinedGeohashes, bounds) && !atCutOffDepth(refinedGeohashes)) {
            refinedGeohashes = replaceWithChildren(refinedGeohashes, bounds);
        }
        return refinedGeohashes;
    }

    private boolean atCutOffDepth(final Collection<Geohash> geohashes) {
        return geohashes.stream().anyMatch(geohash -> geohash.code().length() == cutOffDepth);
    }

    private boolean acceptedCoverage(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        final double geohashArea = geohashes.stream().findAny().get().bounds().areaAsSquareDegrees();
        return geohashArea / bounds.areaAsSquareDegrees() <= maximumCoveragePercent;
    }

    private Collection<Geohash> replaceWithChildren(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        return geohashes.stream().flatMap(geohash -> geohash.children().stream())
                .filter(geohash -> bounds.intersects(geohash.bounds()) || bounds.contains(geohash.bounds()))
                .collect(Collectors.toSet());
    }
}