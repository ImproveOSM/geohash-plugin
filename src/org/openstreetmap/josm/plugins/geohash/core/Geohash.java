package org.openstreetmap.josm.plugins.geohash.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import org.openstreetmap.josm.data.coor.LatLon;
import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Point;


/**
 *
 *
 * @author Mihai Chintoanu
 */
public class Geohash {

    public static final String ROOT_CODE = "";
    public static final Geohash WORLD = new Geohash(ROOT_CODE);

    private final String code;
    private transient BoundingBox bounds;

    public Geohash(final String code) {
        if (code == null) {
            throw new IllegalArgumentException("The code may not be null");
        }
        if (!Alphabet.INSTANCE.isValid(code)) {
            throw new IllegalArgumentException("The code " + code + " is invalid");
        }
        this.code = code;
    }

    public Geohash(final Point point, final int resolution) {
        this(new Codec().encode(point, resolution));
    }

    public String code() {
        return code;
    }

    public BoundingBox bounds() {
        if (bounds == null) {
            bounds = new Codec().decode(code);
        }
        return bounds;
    }

    public Geohash parent() {
        return this == WORLD ? null : new Geohash(code.substring(0, code.length() - 1));
    }

    public Collection<Geohash> children() {
        final Collection<Geohash> children = new HashSet<>();
        for (final char character : Alphabet.INSTANCE.characters()) {
            children.add(new Geohash(code + character));
        }
        return children;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Geohash other = (Geohash) obj;
        return Objects.equals(code, other.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public String toString() {
        return code + " (" + bounds() + ')';
    }

    public boolean containsPoint(final LatLon point) {
        if (point.lat() <= bounds.north().asDegrees() && point.lat() >= bounds.south().asDegrees()
                && point.lon() >= bounds.west().asDegrees() && point.lon() <= bounds.east().asDegrees()) {
            return true;
        } else {
            return false;
        }
    }
}
