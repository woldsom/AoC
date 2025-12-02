package com.w_wins.adventcommon;

import com.w_wins.common.Math;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public record BigCoordinate(long x,
                            long y) implements Comparable<BigCoordinate> {
    public static final BigCoordinate[] MANHATTAN_COORDINATES = new BigCoordinate[]{
            new BigCoordinate(1, 0),
            new BigCoordinate(0, 1),
            new BigCoordinate(-1, 0),
            new BigCoordinate(0, -1)
    };

    public static final BigCoordinate[] CHEBYSHEV_COORDINATES = IntStream.rangeClosed(-1, 1).boxed().flatMap(y -> IntStream.rangeClosed(-1, 1).filter(x -> x != 0 || y != 0).mapToObj(x -> new BigCoordinate(x, y))).toArray(BigCoordinate[]::new);
    public static final Comparator<BigCoordinate> Y_FIRST_COMPARATOR = Comparator.comparingLong(BigCoordinate::y).thenComparing(BigCoordinate::x);
    public static final BigCoordinate ORIGO = new BigCoordinate(0, 0);

    public Set<BigCoordinate> stepLine(final BigCoordinate to, final Predicate<BigCoordinate> bounds) {
        return lineWith(minus(to), bounds);
    }

    private Set<BigCoordinate> lineWith(final BigCoordinate diff, final Predicate<BigCoordinate> bounds) {
        return Stream.concat(Stream.iterate(this, bounds, diff::plus), Stream.iterate(this, bounds, ORIGO.minus(diff)::plus)).collect(Collectors.toSet());
    }

    public Set<BigCoordinate> line(final BigCoordinate to, final Predicate<BigCoordinate> bounds) {
        return lineWith(minus(to).reduce(), bounds);
    }

    private BigCoordinate reduce() {
        final int divisor = BigInteger.valueOf(x()).gcd(BigInteger.valueOf(y())).intValue();
        return divisor == 1 ? this : new BigCoordinate(x() / divisor, y() / divisor);
    }

    public double arcTangent() {
        return java.lang.Math.atan2(y(), x());
    }

    public BigCoordinate clockwise() {
        return new BigCoordinate(-y(), x());
    }

    public BigCoordinate counterclockwise() {
        return new BigCoordinate(y(), -x());
    }

    public Stream<BigCoordinate> manhattanNeighbours(final Predicate<BigCoordinate> boundsCheck) {
        return Stream.of(MANHATTAN_COORDINATES).map(this::plus).filter(boundsCheck);
    }

    public BigCoordinate plus(final BigCoordinate other) {
        return new BigCoordinate(x + other.x, y + other.y);
    }

    public Stream<BigCoordinate> flatNeighbours(final Predicate<BigCoordinate> boundsCheck) {
        return Stream.of(CHEBYSHEV_COORDINATES).map(this::plus).filter(boundsCheck);
    }

    public BigCoordinate absDiff(final BigCoordinate other) {
        final int sign = com.w_wins.common.Math.sgn(compareTo(other));
        return switch (sign) {
            case 0 ->
                    new BigCoordinate(0, 0);
            case 1 ->
                    minus(other);
            case -1 ->
                    other.minus(this);
            default ->
                    throw new IllegalStateException("Unexpected value: " + sign);
        };
    }

    @Override
    public int compareTo(final BigCoordinate other) {
        return Y_FIRST_COMPARATOR.compare(this, other);
    }

    public BigCoordinate minus(final BigCoordinate other) {
        return new BigCoordinate(x - other.x(), y - other.y());
    }

    public BigCoordinate times(int scalar) {
        return new BigCoordinate(x() * scalar, y() * scalar);
    }

    public BigCoordinate sgn() {
        return new BigCoordinate(Math.sgn(x()), Math.sgn(y()));
    }

    public long manhattanMagnitude() {
        return abs(x()) + abs(y());
    }

    public double magnitude() {
        return sqrt(x() * x() + y() * y());
    }

    public boolean coLinearWith(final BigCoordinate other) {
        return reduce().equals(other.reduce());
    }
}
