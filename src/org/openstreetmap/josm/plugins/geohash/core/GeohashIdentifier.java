/*
 * Copyright 2019 Grabtaxi Holdings PTE LTE (GRAB), All rights reserved.
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file.
 *
 */
package org.openstreetmap.josm.plugins.geohash.core;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import net.exfidefortis.map.BoundingBox;


/**
 * A class used to get all the geohashes for a desired geographic area
 *
 * The size of the geohashes returned for the target area depends on a ratio between the length of the geohash side (in
 * degrees) and the length of the target area side (also in degrees). The geohashes are selected in such a way that the
 * (actual) ratio between their side length and the side length of the area does not exceed the (desired) defined ratio.
 *
 * This class doesn't provide a way for the client to set a custom ratio but offers the ability to increase and decrease
 * the ratio in order to obtain sparser or more concentrated geohashes for the same area. If this ratio is large the
 * geohashes will be large (a large ratio means than one geohash may cover a larger portion of the target area). If the
 * ratio is small the geohashes will be small (a small ratio means that one geohash is allowed to cover only a small
 * portion of the target area).
 *
 * Here is an example of how the ratio is used to identify the correct geohashes for a given area with side length A.
 * Let's say the algorithm progresses until it identifies geohashes with side length G and the (actual) ratio G/A is
 * 0.5 (50%) but the (desired) defined ratio is 0.3 (30%). Since 0.5 > 0.3 the algorithm will continue by examining the
 * children of the original geohashes. The children have shorter side lengths (G') than the parents so G'/A < G/A.
 * Let's say that G'/A is 0.28 (28%). Since 0.28 < 0.3 the algorithm stops looking and returns the geohashes with side
 * lengths G'.
 *
 * @author Mihai Chintoanu
 */
public final class GeohashIdentifier {

    /**
     * The depth (i.e. geohash length) after which no geohash children are explored, even though there might be only one
     * geohash returned which fully encompasses the target area and is consequently indistinguishable (because in this
     * case no geohash borders are visible)
     */
    public static final int CUTOFF_DEPTH = 10;

    /*
     * "Side ratio" refers to the ratio between the geohash side length in degrees and the target area side length in
     * degrees.
     * The maximum and minimum values define thresholds beyond which it isn't allowed to increase or decrease the ratio.
     * The default value is the ratio starting point.
     * The step defines the amount by which each increase and decrease is accomplished.
     * The maximum, minimum, default, and step values should be defined in such a way that no increase or decrease of
     * the ratio would lead to the thresholds (minimum and maximum) being exceeded.
     * The leeway is a margin used for avoiding to switch geohash sizes when the actual ratio is very close to the
     * defined ratio but still exceeds it. It is interpreted as a percent of the defined ratio. So if the defined ratio
     * is 40% and the leeway is 10%, the actual ratio is compared to 44% (since 10% of 40 is 4 and 40+4=44).
     */

    private static final Percent MAXIMUM_SIDE_RATIO = new Percent(85);
    private static final Percent DEFAULT_SIDE_RATIO = new Percent(55);
    private static final Percent MINIMUM_SIDE_RATIO = new Percent(55);
    private static final Percent SIDE_RATIO_STEP = new Percent(30);
    private static final Percent SIDE_RATIO_LEEWAY = new Percent(10);

    private boolean isZoomFrozen = false;

    private Collection<Geohash> geohashesBeforeFreeze;


    /** The ratio between the geohash side length and the area side length which must not be exceeded */
    private Percent sideRatio = DEFAULT_SIDE_RATIO;

    /**
     * Checks if the side ratio can be increased.
     * @return true if the side ratio can be increased and false otherwise
     */
    public boolean canIncreaseSideRatio() {
        return sideRatio.isLessThan(MAXIMUM_SIDE_RATIO);
    }

    /**
     * Checks if increasing the side ratio by one step would produce a different result for the given area. If not, the
     * user would not notice the increase (which might be misleading).
     * @param bounds a bounding box for which to return geohashes
     * @return true if the side ratio increase would produce different geohashes for the given area
     */
    public boolean wouldNoticeSideRatioIncrease(final BoundingBox bounds) {
        final Collection<Geohash> geohashes = get(bounds);
        final Collection<Geohash> increasedGeohashes = get(bounds, sideRatio.add(SIDE_RATIO_STEP));
        return !geohashes.equals(increasedGeohashes);
    }

    /**
     * Increases the side ratio by one (predefined) step.
     */
    public void increaseSideRatio() {
        if (canIncreaseSideRatio()) {
            sideRatio = sideRatio.add(SIDE_RATIO_STEP);
        }
    }

    /**
     * Checks if the side ratio can be decreased.
     * @return true if the side ratio can be decreased and false otherwise
     */
    public boolean canDecreaseSideRatio() {
        return sideRatio.isGreaterThan(MINIMUM_SIDE_RATIO);
    }

    /**
     * Checks if decreasing the side ratio by one step would produce a different result for the given area. If not, the
     * user would not notice the decrease (which might be misleading).
     * @param bounds a bounding box for which to return geohashes
     * @return true if the side ratio decrease would produce different geohashes for the given area
     */
    public boolean wouldNoticeSideRatioDecrease(final BoundingBox bounds) {
        final Collection<Geohash> geohashes = get(bounds);
        final Collection<Geohash> decreasedGeohashes = get(bounds, sideRatio.subtract(SIDE_RATIO_STEP));
        return !geohashes.equals(decreasedGeohashes);
    }

    /**
     * Decreases the side ratio by one (predefined) step.
     */
    public void decreaseSideRatio() {
        if (canDecreaseSideRatio()) {
            sideRatio = sideRatio.subtract(SIDE_RATIO_STEP);
        }
    }

    /**
     * Freezes or unfreezes geohash calculation on zoom
     */
    public void setZoomFreeze(boolean freeze, BoundingBox bounds) {
        isZoomFrozen = freeze;
        geohashesBeforeFreeze = get(bounds);
    }

    public boolean getZoomFreeze() {
        return isZoomFrozen;
    }

    public Collection<Geohash> getGeohashesBeforeFreeze() {
        return geohashesBeforeFreeze;
    }

    /**
     * Returns the geohashes that cover the given area given the current side ratio setting.
     * @param bounds a bounding box for which to return geohashes
     * @return a collection of geohashes
     */
    public Collection<Geohash> get(final BoundingBox bounds) {
        return get(bounds, sideRatio);
    }

    private Collection<Geohash> get(final BoundingBox bounds, final Percent customSideRatio) {
        Collection<Geohash> geohashes = Collections.singleton(Geohash.WORLD);
        while (!acceptableSideRatio(geohashes, bounds, customSideRatio)) {
            geohashes = relevantChildren(geohashes, bounds);
        }
        return geohashes;
    }

    private boolean acceptableSideRatio(final Collection<Geohash> geohashes, final BoundingBox bounds,
            final Percent customSideRatio) {
        final boolean acceptableSideRatio;
        if (atCutOffDepth(geohashes)) {
            acceptableSideRatio = true;
        } else if (geohashes.isEmpty() || singleEncompassingGeohash(geohashes, bounds)) {
            acceptableSideRatio = false;
        } else {
            final Percent upperLimit = customSideRatio.add(SIDE_RATIO_LEEWAY);
            acceptableSideRatio = computeSideRatio(geohashes, bounds).isLessThanOrEqualTo(upperLimit);
        }
        return acceptableSideRatio;
    }

    private boolean atCutOffDepth(final Collection<Geohash> geohashes) {
        return geohashes.stream().anyMatch(geohash -> geohash.code().length() == CUTOFF_DEPTH);
    }

    private boolean singleEncompassingGeohash(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        return geohashes.size() == 1 && geohashes.stream().findAny().get().bounds().contains(bounds);
    }

    private Percent computeSideRatio(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        final BoundingBox geohashBounds = geohashes.stream().findAny().get().bounds();
        final double geohashWidth = geohashBounds.east().subtract(geohashBounds.west()).asDegrees();
        final double geohashHeight = geohashBounds.north().subtract(geohashBounds.south()).asDegrees();
        final double geohashSide = Math.max(geohashWidth, geohashHeight);
        final double boundsSide = bounds.east().subtract(bounds.west()).asDegrees();
        return Percent.fromRatio(geohashSide, boundsSide);
    }

    private Collection<Geohash> relevantChildren(final Collection<Geohash> geohashes, final BoundingBox bounds) {
        return geohashes.stream().flatMap(geohash -> geohash.children().stream())
                .filter(geohash -> geohash.bounds().sharesAreaWith(bounds))
                .collect(Collectors.toSet());
    }
}