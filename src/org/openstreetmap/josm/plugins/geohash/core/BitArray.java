/*
 * The code is licensed under the GPL Version 3 license https://www.gnu.org/licenses/quick-guide-gplv3.html.
 *
 * Copyright (c)2017, Telenav, Inc. All Rights Reserved
 */
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

    private BitArray(final List<Boolean> bits) {
        this.bits = new ArrayList<>(bits);
    }

    public boolean get(final int index) {
        if (index < 0 || index >= bits.size()) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
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
        final StringBuilder builder = new StringBuilder();
        for (final boolean bitIsSet : bits) {
            builder.append(bitIsSet ? "1" : "0");
        }
        return builder.toString();
    }
}