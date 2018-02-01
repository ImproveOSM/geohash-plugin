/*
 * Copyright (c) 2015, Mihai Chintoanu. All rights reserved.
 *
 * This code is free software. You may use it in compliance with the
 * GNU Lesser General Public License (LGPL), Version 3, or any later version.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/lgpl.html
 *
 * This code is distributed on an "AS IS" basis, WITHOUT ANY WARRANTY.
 */
package net.exfidefortis.map;


/**
 * A geographical longitude represented as an object in order to support
 * multiple representations (as degrees, radians, etc.) while abstracting
 * away internal representation
 * @author Mihai Chintoanu
 */
public class Longitude extends Angle {

    private static final double MINIMUM_DEGREE_VALUE = -180;
    private static final double MAXIMUM_DEGREE_VALUE = 180;

    /** The most western longitude possible */
    public static final Longitude MINIMUM = new Longitude(MINIMUM_DEGREE_VALUE);

    /** The most eastern longitude possible */
    public static final Longitude MAXIMUM = new Longitude(MAXIMUM_DEGREE_VALUE);

    public static final Range<Longitude> RANGE = new Range<>(MINIMUM, MAXIMUM);

    /** Longitude 0 */
    public static final Longitude ZERO = new Longitude(0);

    /**
     * Builds a longitude object with the given value.
     * @param degrees the longitude value in decimal degrees
     * @return a new longitude object
     * @throws IllegalArgumentException if the given value is outside the
     * accepted range of [-180.0, 180.0]
     */
    public static Longitude forDegrees(double degrees) {
        return new Longitude(degrees);
    }

    /**
     * Builds a longitude object with the given value.
     * @param radians the longitude value in radians
     * @return a new longitude object
     * @throws IllegalArgumentException if the given value is outside the
     * accepted range of [-PI, PI]
     */
    public static Longitude forRadians(double radians) {
        return new Longitude(Math.toDegrees(radians));
    }

    private Longitude(double degrees) {
        super(degrees, MINIMUM_DEGREE_VALUE, MAXIMUM_DEGREE_VALUE);
    }
}
