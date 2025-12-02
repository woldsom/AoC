package com.w_wins.adventcommon;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;

import static java.lang.Math.ceil;
import static java.lang.Math.round;

public record LongRange(long start, long end) {
    static final Function<Function<LongStream, OptionalLong>, ToLongFunction<Collection<Long>>> findExtreme = process -> list -> {
        return process.apply(list.stream().mapToLong(n -> n)).orElseThrow();
    };
    static final ToLongFunction<Collection<Long>> findMin = findExtreme.apply(LongStream::min);
    static final ToLongFunction<Collection<Long>> findMax = findExtreme.apply(LongStream::max);

    public LongRange {
        if (start > end) {
            throw new IllegalArgumentException("Start "+start +" is greater than end "+end);
        }
    }

    public static LongRange toInfinity(final List<Long> minValues) {
        return new LongRange(findMin.applyAsLong(minValues), Long.MAX_VALUE);
    }

    public static LongRange fromCollection(final Collection<Long> list) {
        return new LongRange(findMin.applyAsLong(list), findMax.applyAsLong(list));
    }

    public static Optional<? extends LongRange> fromDoubleConversion(final LongFunction<Optional<Double>> toDouble, final LongRange origin) {
        return origin.fromDoubleConversion(toDouble);
    }

    public Optional<? extends LongRange> fromDoubleConversion(final LongFunction<Optional<Double>> toDouble) {
        return toDouble.apply(start()).flatMap(da -> toDouble.apply(end()).map(b1 -> integersBetween(da, b1)).orElse(Optional.of(new LongRange((long) ceil(da), Long.MAX_VALUE))));
    }

    private static Optional<LongRange> integersBetween(final double a, final double b) {
        final double mid = (a + b) / 2;
        final long oneInt =  round(mid);
        if (a > oneInt || b < oneInt) {
            return Optional.empty();
        } else {
            return Optional.of(new LongRange((long) ceil(a), (long) b));
        }
    }

    public long size() {
        return end + 1 - start;
    }

    public LongStream stream() {
        if (end == 0) {
            return LongStream.empty();
        }
        if (isInfinite()) {
            throw new IllegalArgumentException("Cannot iterate infinite range (just in case)");
        }
        return LongStream.rangeClosed(start, end);
    }

    public boolean isInfinite() {
        return end == Long.MAX_VALUE;
    }

    public LongRange transform(final LongUnaryOperator transform) {
        final long a = transform.applyAsLong(this.start);
        final long b = transform.applyAsLong(this.end);
        if (a > b) {
            return new LongRange(b, a);
        } else {
            return new LongRange(a, b);
        }
    }

    public boolean covers(final LongRange other) {
        return start <= other.start() && end >= other.end();
    }

    public Optional<LongRange> unionRange(final LongRange other) {
        if (intersection(new LongRange(other.start() - 1, other.end() + 1)).isPresent()) {
            return Optional.of(new LongRange(Math.min(start, other.start()), Math.max(end, other.end())));
        } else {
            return Optional.empty();
        }
    }

    public Optional<LongRange> intersection(final LongRange other) {
        final long resultStart = Math.max(start, other.start());
        final long resultEnd = Math.min(end, other.end());
        if (resultEnd >= resultStart) {
            return Optional.of(new LongRange(resultStart, resultEnd));
        } else {
            return Optional.empty();
        }
    }

    public boolean contains(final long x) {
        return start <= x && end >= x;
    }
}
