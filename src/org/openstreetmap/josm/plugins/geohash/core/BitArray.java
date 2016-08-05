package org.openstreetmap.josm.plugins.geohash.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 *
 *
 * @author Mihai Chintoanu
 */
class BitArray implements Iterable<Boolean> {

    public static class Builder {

        private final List<Boolean> bits = new LinkedList<>();

        public Builder append(final boolean bit) {
            bits.add(bit);
            return this;
        }

        public BitArray build() {
            return new BitArray(bits);
        }
    }


    private final List<Boolean> bits;
    private transient String stringRepresentation;

    private BitArray(final List<Boolean> bits) {
        this.bits = new ArrayList<>(bits);
    }

    public boolean get(final int index) {
        if (index < 0 || index >= bits.size()) {
            throw new IndexOutOfBoundsException("" + index);
        }
        return bits.get(index);
    }

    public int length() {
        return bits.size();
    }

    @Override
    public Iterator<Boolean> iterator() {
        return bits.iterator();
    }

    public String asString() {
        if (stringRepresentation == null) {
            final StringBuilder builder = new StringBuilder();
            for (final boolean bitIsSet : bits) {
                builder.append(bitIsSet ? "1" : "0");
            }
            stringRepresentation = builder.toString();
        }
        return stringRepresentation;
    }
}
