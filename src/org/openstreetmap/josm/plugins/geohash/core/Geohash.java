/*
 * The code is licensed under the GPL Version 3 license https://www.gnu.org/licenses/quick-guide-gplv3.html.
 *
 * Copyright (c)2017, Telenav, Inc. All Rights Reserved
 */
package org.openstreetmap.josm.plugins.geohash.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
    private boolean codeVisibility;

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

    public void setCodeVisibility(final boolean codeVisibility) {
        this.codeVisibility = codeVisibility;
    }

    public boolean isCodeVisible() {
        return this.codeVisibility;
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
        return (point.lat() <= bounds.north().asDegrees()) && (point.lat() >= bounds.south().asDegrees())
                && (point.lon() >= bounds.west().asDegrees()) && (point.lon() <= bounds.east().asDegrees());
    }

    /**
     * Returns true if the geohash has any children in the provided list and false otherwise.
     *
     * @param geohashes
     * @return
     */
    public boolean hasVisibleChildren(final Set<Geohash> geohashes) {
        final Optional<Geohash> child =
                geohashes.stream().filter(g -> g.code().startsWith(this.code) && !g.code().equals(this.code)
                        && g.isCodeVisible()).findAny();
        return child.isPresent() ? true : false;
    }
}