package com.w_wins.adventcommon;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Streams;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import static java.lang.Math.ceil;
import static java.lang.Math.round;

public record Range(int start, int end) {
    static final Function<Function<IntStream, OptionalInt>, ToIntFunction<Collection<Integer>>> findExtreme = process -> list -> {
        return process.apply(list.stream().mapToInt(n -> n)).orElseThrow();
    };
    static final ToIntFunction<Collection<Integer>> findMin = findExtreme.apply(IntStream::min);
    static final ToIntFunction<Collection<Integer>> findMax = findExtreme.apply(IntStream::max);

    public Range {
        if (start > end) {
            throw new IllegalArgumentException();
        }
    }

    public static Range toInfinity(final List<Integer> minValues) {
        return new Range(findMin.applyAsInt(minValues), Integer.MAX_VALUE);
    }

    public static Range fromCollection(final Collection<Integer> list) {
        return new Range(findMin.applyAsInt(list), findMax.applyAsInt(list));
    }

    public static Optional<Range> fromDoubleConversion(final IntFunction<Optional<Double>> toDouble, final Range origin) {
        return origin.fromDoubleConversion(toDouble);
    }

    public static Collector<Set<Range>, ?, Set<Range>> setCollector() {
        return CollectorUtil.groupByReduce(Range::mergeRangeSets);
    }

    public Optional<Range> fromDoubleConversion(final IntFunction<Optional<Double>> toDouble) {
        return toDouble.apply(start()).flatMap(da -> toDouble.apply(end()).map(b1 -> integersBetween(da, b1)).orElse(Optional.of(new Range((int) ceil(da), Integer.MAX_VALUE))));
    }

    private static Optional<Range> integersBetween(final double a, final double b) {
        final double mid = (a + b) / 2;
        final int oneInt = (int) round(mid);
        if (a > oneInt || b < oneInt) {
            return Optional.empty();
        } else {
            return Optional.of(new Range((int) ceil(a), (int) (double) b));
        }
    }

    public long size() {
        return end + 1 - start;
    }

    public IntStream stream() {
        if (end == 0) {
            return IntStream.empty();
        }
        if (isInfinite()) {
            throw new IllegalArgumentException("Cannot iterate infinite range (just in case)");
        }
        return IntStream.rangeClosed(start, end);
    }

    public boolean isInfinite() {
        return end == Integer.MAX_VALUE;
    }

    public Range transform(final IntUnaryOperator transform) {
        final int a = transform.applyAsInt(this.start);
        final int b = transform.applyAsInt(this.end);
        if (a > b) {
            return new Range(b, a);
        } else {
            return new Range(a, b);
        }
    }

    public boolean covers(final Range other) {
        return start <= other.start() && end >= other.end();
    }

    public Optional<Range> unionRange(final Range other) {
        if (intersection(new Range(other.start() - 1, other.end() + 1)).isPresent()) {
            return Optional.of(new Range(Math.min(start, other.start()), Math.max(end, other.end())));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Range> intersection(final Range other) {
        final int resultStart = Math.max(start, other.start());
        final int resultEnd = Math.min(end, other.end());
        if (resultEnd >= resultStart) {
            return Optional.of(new Range(resultStart, resultEnd));
        } else {
            return Optional.empty();
        }
    }

    public boolean contains(final int x) {
        return start <= x && end >= x;
    }

    public static Set<Range> mergeRangeSets(Set<Range> a, Set<Range> b) {
        final SortedSet<Range> sumSet = new TreeSet<>(Comparator.comparing(Range::start));
        sumSet.addAll(a);
        sumSet.addAll(b);
        boolean done = false;
        while (!done) {
            final Optional<Range> firstUnion = Streams.present(Streams.pairStream(sumSet, Range::unionRange)).findFirst();
            if (firstUnion.isEmpty()) {
                done = true;
            } else {
                final Range union = firstUnion.get();
                sumSet.removeIf(union::covers);
                sumSet.add(union);
            }
        }
        return sumSet;
    }
}
