package net.exfidefortis.map;

import java.util.Objects;


/**
 *
 *
 * @author Mihai Chintoanu
 * @param <T>
 */
public class Range<T> {

    private final T minimum;
    private final T maximum;

    public Range(final T minimum, final T maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public T minimum() {
        return minimum;
    }

    public T maximum() {
        return maximum;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Range<?> other = (Range<?>) obj;
        return Objects.equals(minimum, other.minimum) && Objects.equals(maximum, other.maximum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimum, maximum);
    }
}