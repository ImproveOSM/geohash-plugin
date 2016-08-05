package org.openstreetmap.josm.plugins.geohash.core;

import net.exfidefortis.map.Angle;
import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;
import net.exfidefortis.map.Point;
import net.exfidefortis.map.Range;


/**
 *
 *
 * @author Mihai Chintoanu
 */
class Codec {

    /*
     * The following algorithm is taken from http://architects.dzone.com/articles/designing-spacial-index
     */
    public String encode(final Point point, final int resolution) {
        if (resolution < 0) {
            throw new IllegalArgumentException("The resolution must be > 0");
        }
        final int bitCount = Alphabet.INSTANCE.bitsPerCharacter() * resolution;
        final int latitudeBitCount = bitCount / 2;
        final int longitudeBitCount = bitCount - latitudeBitCount;
        final BitArray latitudeBits = coordinateToBits(point.latitude(), Latitude.RANGE, latitudeBitCount);
        final BitArray longitudeBits = coordinateToBits(point.longitude(), Longitude.RANGE, longitudeBitCount);
        final BitArray.Builder geohashBits = new BitArray.Builder();
        for (int i = 0; i < longitudeBits.length(); i++) {
            geohashBits.append(longitudeBits.get(i));
            if (i < latitudeBits.length()) {
                geohashBits.append(latitudeBits.get(i));
            }
        }
        return bitArrayToCode(geohashBits.build());
    }

    private BitArray coordinateToBits(final Angle coordinate, final Range<? extends Angle> coordinateRange,
            final int resolution) {
        final BitArray.Builder bits = new BitArray.Builder();
        Angle lower = coordinateRange.minimum();
        Angle upper = coordinateRange.maximum();
        for (int i = 0; i < resolution; i++) {
            final Angle middle = lower.add(upper).divide(2);
            if (coordinate.compareTo(middle) >= 0) {
                bits.append(true);
                lower = middle;
            } else {
                bits.append(false);
                upper = middle;
            }
        }
        return bits.build();
    }

    private String bitArrayToCode(final BitArray bits) {
        final StringBuilder codeBuilder = new StringBuilder();
        final Alphabet alphabet = Alphabet.INSTANCE;
        final int resolution = bits.length() / alphabet.bitsPerCharacter();
        for (int i = 0; i < resolution; i++) {
            final int start = alphabet.bitsPerCharacter() * i;
            final int end = alphabet.bitsPerCharacter() * (i + 1);
            final String indexBinary = bits.asString().substring(start, end);
            final int index = Integer.parseInt(indexBinary, 2);
            codeBuilder.append(alphabet.get(index));
        }
        return codeBuilder.toString();
    }

    /*
     * The following algorithm is taken from http://en.wikipedia.org/wiki/Geohash
     */
    public BoundingBox decode(final String code) {
        if (code.equals(Geohash.ROOT_CODE)) {
            return BoundingBox.WORLD;
        } else {
            final BitArray codeBits = codeToBitArray(code);
            final BitArray.Builder longitudeBits = new BitArray.Builder();
            final BitArray.Builder latitudeBits = new BitArray.Builder();
            int index = 0;
            for (final boolean bit : codeBits) {
                if (index % 2 == 0) {
                    longitudeBits.append(bit);
                } else {
                    latitudeBits.append(bit);
                }
                index++;
            }
            final Range<Angle> longitudeRange = bitsToCoordinateRange(longitudeBits.build(), Longitude.RANGE);
            final Range<Angle> latitudeRange = bitsToCoordinateRange(latitudeBits.build(), Latitude.RANGE);
            final Latitude north = Latitude.forDegrees(latitudeRange.maximum().asDegrees());
            final Latitude south = Latitude.forDegrees(latitudeRange.minimum().asDegrees());
            final Longitude east = Longitude.forDegrees(longitudeRange.maximum().asDegrees());
            final Longitude west = Longitude.forDegrees(longitudeRange.minimum().asDegrees());
            return new BoundingBox.Builder().north(north).south(south).east(east).west(west).build();
        }
    }

    private BitArray codeToBitArray(final String code) {
        final BitArray.Builder builder = new BitArray.Builder();
        for (final char character : code.toCharArray()) {
            final int index = Alphabet.INSTANCE.indexOf(character);
            final String indexAsString = Integer.toBinaryString(index);
            final int numberOfBits = Alphabet.INSTANCE.bitsPerCharacter();
            final String paddedIndexAsString = String.format("%" + numberOfBits + "s", indexAsString).replace(' ', '0');
            for (final char c : paddedIndexAsString.toCharArray()) {
                switch (c) {
                    case '0':
                        builder.append(false);
                        break;
                    case '1':
                        builder.append(true);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid bit value: " + c);
                }
            }
        }
        return builder.build();
    }

    private Range<Angle> bitsToCoordinateRange(final BitArray coordinateBits,
            final Range<? extends Angle> coordinateRange) {
        Angle lower = coordinateRange.minimum();
        Angle upper = coordinateRange.maximum();
        for (final boolean bitIsSet : coordinateBits){
            final Angle middle = lower.add(upper).divide(2);
            if (bitIsSet) {
                lower = middle;
            } else {
                upper = middle;
            }
        }
        return new Range<>(lower, upper);
    }
}
