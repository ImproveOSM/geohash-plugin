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

import java.util.Objects;


/**
 * A geographical point is a set of geographical coordinates: a latitude and a
 * longitude.
 * @author Mihai Chintoanu
 */
public class Point {

    private final Longitude longitude;
    private final Latitude latitude;

    /**
     * Builds a new geo position with the given latitude and longitude.
     * @param longitude the longitude of this geo position
     * @param latitude the latitude of this geo position
     */
    public Point(final Longitude longitude, final Latitude latitude) {
        if (longitude == null || latitude == null) {
            throw new IllegalArgumentException(
                    "The longitude and latitude must both be not null");
        }
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Returns the longitude of this geo position.
     * @return the longitude of this geo position
     */
    public Longitude longitude() {
        return longitude;
    }

    /**
     * Returns the latitude of this geo position.
     * @return the latitude of this geo position
     */
    public Latitude latitude() {
        return latitude;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point other = (Point) obj;
        if (!Objects.equals(this.longitude, other.longitude)) {
            return false;
        }
        return Objects.equals(this.latitude, other.latitude);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.longitude);
        hash = 71 * hash + Objects.hashCode(this.latitude);
        return hash;
    }

    @Override
    public String toString() {
        return longitude + "," + latitude;
    }
}