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
 * A geometric angle represented as an object in order to support
 * multiple representations (as degrees, radians, etc.) while abstracting
 * away internal representation
 * @author Mihai Chintoanu
 */
public class Angle implements Comparable<Angle> {

    /**
     * Builds an angle object with the given value.
     * @param degrees the longitude value in decimal degrees
     * @return a new angle object
     */
    public static Angle forDegrees(double degrees) {
        return new Angle(degrees);
    }

    /**
     * Builds an angle object with the given value.
     * @param radians the longitude value in radians
     * @return a new angle object
     */
    public static Angle forRadians(double radians) {
        return new Angle(Math.toDegrees(radians));
    }

    private final double degrees;
    private transient Double radians;

    /**
     * Builds an angle with the given value, while also specifying the allowed
     * value interval (for validation purposes).
     * @param degrees the value in decimal degrees
     * @param minimumDegreeValue the minimum allowed value for this kind of
     * angle
     * @param maximumDegreeValue the maximum allowed value for this kind of
     * angle
     */
    protected Angle(double degrees, double minimumDegreeValue,
            double maximumDegreeValue) {
        if (degrees < minimumDegreeValue || degrees > maximumDegreeValue) {
            throw new IllegalArgumentException(
                    "Value out of range: " + degrees
                            + "; allowed values are in the interval ["
                            + minimumDegreeValue + "," + maximumDegreeValue
                            + "]");
        }
        this.degrees = degrees;
    }

    protected Angle(double degrees) {
        this(degrees, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * Returns the value of this angle as decimal degrees.
     * @return the value of this angle as decimal degrees
     */
    public double asDegrees() {
        return degrees;
    }

    /**
     * Returns the value of this angle as radians.
     * @return the value of this angle as radians
     */
    public double asRadians() {
        if (radians == null) {
            radians = Math.toRadians(degrees);
        }
        return radians;
    }


    public Angle add(Angle other) {
        return Angle.forDegrees(degrees + other.degrees);
    }

    public Angle subtract(Angle other) {
        return Angle.forDegrees(degrees - other.degrees);
    }

    public Angle multiply(double value) {
        return Angle.forDegrees(degrees * value);
    }

    public Angle divide(double value) {
        return Angle.forDegrees(degrees / value);
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.degrees)
                ^ (Double.doubleToLongBits(this.degrees) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Angle other = (Angle) obj;
        return Double.doubleToLongBits(this.degrees) ==
                Double.doubleToLongBits(other.degrees);
    }

    @Override
    public String toString() {
        return "" + degrees;
    }

    @Override
    public int compareTo(Angle other) {
        return Double.compare(asDegrees(), other.asDegrees());
    }
}
