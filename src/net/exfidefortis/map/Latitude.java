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
 * A geographical latitude represented as an object in order to support
 * multiple representations (as degrees, radians, etc.) while abstracting
 * away internal representation
 * @author Mihai Chintoanu
 */
public class Latitude extends Angle {

    private static final double MINIMUM_DEGREE_VALUE = -90;
    private static final double MAXIMUM_DEGREE_VALUE = 90;

    /** The most southern latitude possible */
    public static final Latitude MINIMUM = new Latitude(MINIMUM_DEGREE_VALUE);

    /** The most northern latitude possible */
    public static final Latitude MAXIMUM = new Latitude(MAXIMUM_DEGREE_VALUE);

    public static final Range<Latitude> RANGE = new Range<>(MINIMUM, MAXIMUM);

    /** Latitude 0 */
    public static final Latitude ZERO = new Latitude(0);

    /**
     * Builds a latitude object with the given value.
     * @param degrees the latitude value in decimal degrees
     * @return a new latitude object
     * @throws IllegalArgumentException if the given value is outside the
     * accepted range of [-90.0, 90.0]
     */
    public static Latitude forDegrees(double degrees) {
        return new Latitude(degrees);
    }

    /**
     * Builds a latitude object with the given value.
     * @param radians the latitude value in radians
     * @return a new latitude object
     * @throws IllegalArgumentException if the given value is outside the
     * accepted range of [-PI/2, PI/2]
     */
    public static Latitude forRadians(double radians) {
        return new Latitude(Math.toDegrees(radians));
    }

    private Latitude(double degrees) {
        super(degrees, MINIMUM_DEGREE_VALUE, MAXIMUM_DEGREE_VALUE);
    }
}
