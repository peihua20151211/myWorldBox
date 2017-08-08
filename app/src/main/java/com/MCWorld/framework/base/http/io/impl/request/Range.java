package com.MCWorld.framework.base.http.io.impl.request;

public final class Range<T extends Comparable<? super T>> {
    private final T mLower;
    private final T mUpper;

    public Range(T lower, T upper) {
        this.mLower = lower;
        this.mUpper = upper;
        if (lower.compareTo(upper) > 0) {
            throw new IllegalArgumentException("lower must be less than or equal to upper");
        }
    }

    public static <T extends Comparable<? super T>> Range<T> create(T lower, T upper) {
        return new Range(lower, upper);
    }

    public T getLower() {
        return this.mLower;
    }

    public T getUpper() {
        return this.mUpper;
    }

    public boolean contains(T value) {
        boolean gteLower;
        if (value.compareTo(this.mLower) >= 0) {
            gteLower = true;
        } else {
            gteLower = false;
        }
        boolean lteUpper;
        if (value.compareTo(this.mUpper) <= 0) {
            lteUpper = true;
        } else {
            lteUpper = false;
        }
        if (gteLower && lteUpper) {
            return true;
        }
        return false;
    }

    public boolean contains(Range<T> range) {
        boolean gteLower;
        if (range.mLower.compareTo(this.mLower) >= 0) {
            gteLower = true;
        } else {
            gteLower = false;
        }
        boolean lteUpper;
        if (range.mUpper.compareTo(this.mUpper) <= 0) {
            lteUpper = true;
        } else {
            lteUpper = false;
        }
        if (gteLower && lteUpper) {
            return true;
        }
        return false;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Range)) {
            return false;
        }
        Range other = (Range) obj;
        if (!(this.mLower.equals(other.mLower) && this.mUpper.equals(other.mUpper))) {
            z = false;
        }
        return z;
    }

    public T clamp(T value) {
        if (value.compareTo(this.mLower) < 0) {
            return this.mLower;
        }
        if (value.compareTo(this.mUpper) > 0) {
            return this.mUpper;
        }
        return value;
    }

    public Range<T> intersect(Range<T> range) {
        int cmpLower = range.mLower.compareTo(this.mLower);
        int cmpUpper = range.mUpper.compareTo(this.mUpper);
        if (cmpLower <= 0 && cmpUpper >= 0) {
            return this;
        }
        if (cmpLower >= 0 && cmpUpper <= 0) {
            return range;
        }
        return create(cmpLower <= 0 ? this.mLower : range.mLower, cmpUpper >= 0 ? this.mUpper : range.mUpper);
    }

    public Range<T> intersect(T t, T upper) {
        int cmpLower = t.compareTo(this.mLower);
        int cmpUpper = upper.compareTo(this.mUpper);
        if (cmpLower <= 0 && cmpUpper >= 0) {
            return this;
        }
        Comparable lower;
        if (cmpLower <= 0) {
            lower = this.mLower;
        }
        if (cmpUpper >= 0) {
            upper = this.mUpper;
        }
        return create(lower, upper);
    }

    public Range<T> extend(Range<T> range) {
        int cmpLower = range.mLower.compareTo(this.mLower);
        int cmpUpper = range.mUpper.compareTo(this.mUpper);
        if (cmpLower <= 0 && cmpUpper >= 0) {
            return range;
        }
        if (cmpLower >= 0 && cmpUpper <= 0) {
            return this;
        }
        return create(cmpLower >= 0 ? this.mLower : range.mLower, cmpUpper <= 0 ? this.mUpper : range.mUpper);
    }

    public Range<T> extend(T t, T upper) {
        int cmpLower = t.compareTo(this.mLower);
        int cmpUpper = upper.compareTo(this.mUpper);
        if (cmpLower >= 0 && cmpUpper <= 0) {
            return this;
        }
        Comparable lower;
        if (cmpLower >= 0) {
            lower = this.mLower;
        }
        if (cmpUpper <= 0) {
            upper = this.mUpper;
        }
        return create(lower, upper);
    }

    public Range<T> extend(T value) {
        return extend(value, value);
    }

    public String toString() {
        return String.format("[%s, %s]", new Object[]{this.mLower, this.mUpper});
    }

    public int hashCode() {
        return hashCodeGeneric(this.mLower, this.mUpper);
    }

    public static <T> int hashCodeGeneric(T... array) {
        if (array == null) {
            return 0;
        }
        int h = 1;
        for (T o : array) {
            h = ((h << 5) - h) ^ (o == null ? 0 : o.hashCode());
        }
        return h;
    }
}
