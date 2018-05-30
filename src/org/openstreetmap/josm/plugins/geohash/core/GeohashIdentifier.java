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

    private static final double MAXIMUM_SIDE_RATIO = 1;
    private static final double MINIMUM_SIDE_RATIO = 0.2;
    private static final double SIDE_RATIO_LEEWAY = 0.1;


    private double sideRatio;

    public GeohashIdentifier(final double sideRatio) {
        this.sideRatio = sideRatio;
    }

    public boolean canIncreaseSideRatio(final BoundingBox bounds) {
        return !Objects.equals(computeIncreasedSideRatio(bounds), sideRatio);
    }

    public void increaseSideRatio(final BoundingBox bounds) {
        final double newSideRatio = computeIncreasedSideRatio(bounds);
        if (!Objects.equals(newSideRatio, sideRatio)) {
            sideRatio = newSideRatio;
        }
    }

    private double computeIncreasedSideRatio(final BoundingBox bounds) {
        final Collection<Geohash> geohashes = get(bounds);
        final Collection<Geohash> parents = geohashes.stream().map(Geohash::parent).collect(Collectors.toSet());
        final Geohash oneParent = parents.stream().findAny().get();
        double newSideRatio = sideRatio;
        if (oneParent != Geohash.WORLD) {
            final double proposedSideRatio = computeSideRatio(Collections.singleton(oneParent), bounds);
            if (proposedSideRatio < MAXIMUM_SIDE_RATIO) {
                newSideRatio = proposedSideRatio;
            }
        }
        return newSideRatio;
    }

    public boolean canDecreaseSideRatio(final BoundingBox bounds) {
        return !Objects.equals(computeDecreasedSideRatio(bounds), sideRatio);
    }

    public void decreaseSideRatio(final BoundingBox bounds) {
        final double newSideRatio = computeDecreasedSideRatio(bounds);
        if (!Objects.equals(newSideRatio, sideRatio)) {
            sideRatio = newSideRatio;
        }
    }

    private double computeDecreasedSideRatio(final BoundingBox bounds) {
        Collection<Geohash> geohashes = get(bounds);
        final Collection<Geohash> children = relevantChildren(geohashes, bounds);
        double newSideRatio = sideRatio;
        if (!atCutOffDepth(children)) {
            final double proposedSideRatio = computeSideRatio(children, bounds);
            if (proposedSideRatio > MINIMUM_SIDE_RATIO) {
                newSideRatio = proposedSideRatio;
            }
        }
        return newSideRatio;
    }

    public Collection<Geohash> get(final BoundingBox bounds) {
        Collection<Geohash> geohashes = Collections.singleton(Geohash.WORLD);
        while (!acceptableSideRatio(geohashes, bounds)) {
            geohashes = relevantChildren(geohashes, bounds);
        }
        return geohashes;
    }

    private boolean acceptableSideRatio(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        final boolean acceptableSideRatio;
        if (atCutOffDepth(geohashes)) {
            acceptableSideRatio = true;
        } else if (geohashes.isEmpty() || singleEncompassingGeohash(geohashes, bounds)) {
            acceptableSideRatio = false;
        } else {
            final double acceptableError = sideRatio * SIDE_RATIO_LEEWAY;
            acceptableSideRatio = computeSideRatio(geohashes, bounds) <= sideRatio + acceptableError;
        }
        return acceptableSideRatio;
    }

    private boolean atCutOffDepth(final Collection<Geohash> geohashes) {
        return geohashes.stream().anyMatch(geohash -> geohash.code().length() == CUTOFF_DEPTH);
    }

    private boolean singleEncompassingGeohash(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        return geohashes.size() == 1 && geohashes.stream().findAny().get().bounds().contains(bounds);
    }

    private double computeSideRatio(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        final BoundingBox geohashBounds = geohashes.stream().findAny().get().bounds();
        final double geohashWidth = geohashBounds.east().subtract(geohashBounds.west()).asDegrees();
        final double geohashHeight = geohashBounds.north().subtract(geohashBounds.south()).asDegrees();
        final double geohashSide = Math.max(geohashWidth, geohashHeight);
        final double boundsSide = bounds.east().subtract(bounds.west()).asDegrees();
        return geohashSide / boundsSide;
    }

    private Collection<Geohash> relevantChildren(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        return geohashes.stream().flatMap(geohash -> geohash.children().stream())
                .filter(geohash -> geohash.bounds().sharesAreaWith(bounds))
                .collect(Collectors.toSet());
    }
}