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
 * A bounding box defines an area on the map. Its shape depends on the map
 * projection. On a Mercator projection the bounding box is rectangular.
 * @author Mihai Chintoanu
 */
public class BoundingBox {

    public static class Builder {

        private Latitude north;
        private Latitude south;
        private Longitude east;
        private Longitude west;

        public Builder() { }

        public Builder(BoundingBox reference) {
            north = reference.north();
            south = reference.south();
            east = reference.east();
            west = reference.west();
        }

        public Builder north(Latitude north) {
            this.north = north;
            return this;
        }

        public Builder south(Latitude south) {
            this.south = south;
            return this;
        }

        public Builder east(Longitude east) {
            this.east = east;
            return this;
        }

        public Builder west(Longitude west) {
            this.west = west;
            return this;
        }

        public Latitude north() {
            return north;
        }

        public Latitude south() {
            return south;
        }

        public Longitude east() {
            return east;
        }

        public Longitude west() {
            return west;
        }

        public BoundingBox build() {
            return new BoundingBox(north, south, east, west);
        }
    }


    /** A bounding box which encompasses the whole world */
    public static final BoundingBox WORLD = new BoundingBox(Latitude.MAXIMUM,
            Latitude.MINIMUM, Longitude.MAXIMUM, Longitude.MINIMUM);

    private final Latitude north;
    private final Latitude south;
    private final Longitude east;
    private final Longitude west;

    private BoundingBox(Latitude north, Latitude south, Longitude east,
            Longitude west) {
        if (north.compareTo(south) < 0) {
            throw new IllegalArgumentException("north < south");
        }
        if (east.compareTo(west) < 0) {
            throw new IllegalArgumentException("east < west");
        }
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }

    /**
     * Returns a new bounding box based on this one, but with a different north.
     * @param north the north boundary for the new bounding box
     * @return a new bounding box with the given north and all other boundaries
     * of this bounding box
     */
    public BoundingBox withNorth(Latitude north) {
        return new BoundingBox.Builder(this).north(north).build();
    }

    /**
     * Returns a new bounding box based on this one, but with a different south.
     * @param south the south boundary for the new bounding box
     * @return a new bounding box with the given south and all other boundaries
     * of this bounding box
     */
    public BoundingBox withSouth(Latitude south) {
        return new BoundingBox.Builder(this).south(south).build();
    }

    /**
     * Returns a new bounding box based on this one, but with a different east.
     * @param east the east boundary for the new bounding box
     * @return a new bounding box with the given east and all other boundaries
     * of this bounding box
     */
    public BoundingBox withEast(Longitude east) {
        return new BoundingBox.Builder(this).east(east).build();
    }

    /**
     * Returns a new bounding box based on this one, but with a different west.
     * @param west the west boundary for the new bounding box
     * @return a new bounding box with the given west and all other boundaries
     * of this bounding box
     */
    public BoundingBox withWest(Longitude west) {
        return new BoundingBox.Builder(this).west(west).build();
    }

    /**
     * Returns the latitude of the northern edge of this bounding box.
     * @return this bounding box's north latitude
     */
    public Latitude north() {
        return north;
    }

    /**
     * Returns the latitude of the southern edge of this bounding box.
     * @return this bounding box's south latitude
     */
    public Latitude south() {
        return south;
    }

    /**
     * Returns the longitude of the eastern edge of this bounding box.
     * @return this bounding box's east longitude
     */
    public Longitude east() {
        return east;
    }

    /**
     * Returns the longitude of the western edge of this bounding box.
     * @return this bounding box's west longitude
     */
    public Longitude west() {
        return west;
    }

    /**
     * Returns the coordinates of the north western corner of this bounding box.
     * @return this bounding box's north western coordinate
     */
    public Point northWest() {
        return new Point(west, north);
    }

    /**
     * Returns the coordinates of the north eastern corner of this bounding box.
     * @return this bounding box's north eastern coordinate
     */
    public Point northEast() {
        return new Point(east, north);
    }

    /**
     * Returns the coordinates of the south western corner of this bounding box.
     * @return this bounding box's south western coordinate
     */
    public Point southWest() {
        return new Point(west, south);
    }

    /**
     * Returns the coordinates of the south eastern corner of this bounding box.
     * @return this bounding box's south eastern coordinate
     */
    public Point southEast() {
        return new Point(east, south);
    }

    public Point center() {
        Latitude latitude = Latitude.forDegrees((north.asDegrees() + south.asDegrees()) / 2);
        Longitude longitude = Longitude.forDegrees((east.asDegrees() + west.asDegrees()) / 2);
        return new Point(longitude, latitude);
    }

    public boolean contains(final Point point) {
        if (point == null) {
            throw new IllegalArgumentException("received null point");
        }
        return point.latitude().asDegrees() >= south.asDegrees()
                && point.latitude().asDegrees() <= north.asDegrees()
                && point.longitude().asDegrees() >= west.asDegrees()
                && point.longitude().asDegrees() <= east.asDegrees();
    }

    public boolean contains(final BoundingBox other) {
        if (other == null) {
            throw new IllegalArgumentException("received null bounding box");
        }
        return contains(other.northEast()) && contains(other.northWest()) && contains(other.southEast())
                && contains(other.southWest());
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.north);
        hash = 37 * hash + Objects.hashCode(this.south);
        hash = 37 * hash + Objects.hashCode(this.east);
        hash = 37 * hash + Objects.hashCode(this.west);
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
        final BoundingBox other = (BoundingBox) obj;
        if (!Objects.equals(this.north, other.north)) {
            return false;
        }
        if (!Objects.equals(this.south, other.south)) {
            return false;
        }
        if (!Objects.equals(this.east, other.east)) {
            return false;
        }
        return Objects.equals(this.west, other.west);
    }

    @Override
    public String toString() {
        return "W:" + west.asDegrees() + ", E:" + east.asDegrees()
                + ", S:" + south.asDegrees() + ", N:" + north.asDegrees();
    }
}
