package org.openstreetmap.josm.plugins.geohash.core;

import java.util.Objects;


/**
 * @author Mihai Chintoanu
 */
class Percent {

    public static Percent fromRatio(final double numerator, final double denominator) {
        final double value = numerator * 100 / denominator;
        return new Percent(Math.round((float) value));
    }

    private final int value;

    public Percent(final int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Percent value must be positive");
        }
        this.value = value;
    }

    public Percent add(final Percent other) {
        return new Percent(value + other.value);
    }

    public Percent subtract(final Percent other) {
        return new Percent(value - other.value);
    }

    public boolean isGreaterThan(final Percent other) {
        return value > other.value;
    }

    public boolean isGreaterThanOrEqualTo(final Percent other) {
        return value >= other.value;
    }

    public boolean isLessThan(final Percent other) {
        return value < other.value;
    }

    public boolean isLessThanOrEqualTo(final Percent other) {
        return value <= other.value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Percent percent = (Percent) obj;
        return value == percent.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value + "%";
    }
}
