/*
 * The code is licensed under the GPL Version 3 license https://www.gnu.org/licenses/quick-guide-gplv3.html.
 *
 * Copyright (c)2017, Telenav, Inc. All Rights Reserved
 */
package org.openstreetmap.josm.plugins.geohash.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import net.exfidefortis.map.BoundingBox;


/**
 *
 *
 * @author Mihai Chintoanu
 */
public final class GeohashIdentifier {

    public static final int CUTOFF_DEPTH = 10;


    private double coverageRatio;
    private double coverageRatioLeeway;

    public GeohashIdentifier(final double coverageRatio, final double coverageRatioLeeway) {
        this.coverageRatio = coverageRatio;
        this.coverageRatioLeeway = coverageRatioLeeway;
    }

    public boolean coverageRatioIncreasePossible(final BoundingBox bounds) {
        return !Objects.equals(computeIncreasedCoverageRatio(bounds), coverageRatio);
    }

    public void increaseCoverageRatio(final BoundingBox bounds) {
        final double newCoverageRatio = computeIncreasedCoverageRatio(bounds);
        if (!Objects.equals(newCoverageRatio, coverageRatio)) {
            coverageRatio = newCoverageRatio;
        }
    }

    private double computeIncreasedCoverageRatio(final BoundingBox bounds) {
        final Collection<Geohash> geohashes = get(bounds);
        final Collection<Geohash> parents = geohashes.stream().map(Geohash::parent).collect(Collectors.toSet());
        final Geohash oneParent = parents.stream().findAny().get();
        double newCoverageRatio = coverageRatio;
        if (oneParent != Geohash.WORLD) {
            final double geohashArea = oneParent.bounds().areaAsSquareDegrees();
            final double boundsArea = bounds.areaAsSquareDegrees();
            final double proposedMaximumCoveragePercent = geohashArea / boundsArea;
            if (proposedMaximumCoveragePercent < 1) {
                newCoverageRatio = proposedMaximumCoveragePercent;
            }
        }
        return newCoverageRatio;
    }

    public boolean coverageRatioDecreasePossible(final BoundingBox bounds) {
        return !Objects.equals(computeDecreasedCoverageRatio(bounds), coverageRatio);
    }

    public void decreaseCoverageRatio(final BoundingBox bounds) {
        final double newCoverageRatio = computeDecreasedCoverageRatio(bounds);
        if (!Objects.equals(newCoverageRatio, coverageRatio)) {
            coverageRatio = newCoverageRatio;
        }
    }

    private double computeDecreasedCoverageRatio(final BoundingBox bounds) {
        Collection<Geohash> geohashes = get(bounds);
        final Collection<Geohash> children = relevantChildren(geohashes, bounds);
        double newCoverageRatio = coverageRatio;
        if (children.size() < 400 && !atCutOffDepth(children)) {
            final Geohash oneChild = children.stream().findAny().get();
            final double geohashArea = oneChild.bounds().areaAsSquareDegrees();
            final double boundsArea = bounds.areaAsSquareDegrees();
            newCoverageRatio = geohashArea / boundsArea;
        }
        return newCoverageRatio;
    }

    public Collection<Geohash> get(final BoundingBox bounds) {
        System.out.println("coverageRatio is " + coverageRatio);
        Collection<Geohash> geohashes = Collections.singleton(Geohash.WORLD);
        while (!acceptableCoverage(geohashes, bounds)) {
            geohashes = relevantChildren(geohashes, bounds);
        }
        System.out.println("actual coverage ratio is " + coverageRatio(geohashes, bounds));
        return geohashes;
    }

    private boolean acceptableCoverage(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        final boolean acceptableCoverage;
        if (atCutOffDepth(geohashes)) {
            acceptableCoverage = true;
        } else if (geohashes.isEmpty() || singleEncompassingGeohash(geohashes, bounds)) {
            acceptableCoverage = false;
        } else {
            final double acceptableError = coverageRatio * coverageRatioLeeway;
            acceptableCoverage = coverageRatio(geohashes, bounds) <= coverageRatio + acceptableError;
        }
        return acceptableCoverage;
    }

    private boolean atCutOffDepth(final Collection<Geohash> geohashes) {
        return geohashes.stream().anyMatch(geohash -> geohash.code().length() == CUTOFF_DEPTH);
    }

    private boolean singleEncompassingGeohash(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        return geohashes.size() == 1 && geohashes.stream().findAny().get().bounds().contains(bounds);
    }

    private double coverageRatio(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        final double geohashArea = geohashes.stream().findAny().get().bounds().areaAsSquareDegrees();
        return geohashArea / bounds.areaAsSquareDegrees();
    }

    private Collection<Geohash> relevantChildren(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        return geohashes.stream().flatMap(geohash -> geohash.children().stream())
                .filter(geohash -> geohash.bounds().sharesAreaWith(bounds))
                .collect(Collectors.toSet());
    }
}