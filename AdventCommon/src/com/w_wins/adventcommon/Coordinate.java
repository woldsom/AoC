package com.w_wins.adventcommon;

import com.w_wins.common.Math;
import com.w_wins.fixedwidth.Grid;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.util.function.Predicate.not;

public record Coordinate(
        int x,
        int y) implements Comparable<Coordinate> {
    public static final Coordinate[] MANHATTAN_COORDINATES = new Coordinate[]{
            new Coordinate(1, 0),
            new Coordinate(0, 1),
            new Coordinate(-1, 0),
            new Coordinate(0, -1)
    };

    public static final Coordinate[] CHEBYSHEV_COORDINATES = IntStream.rangeClosed(-1, 1).boxed().flatMap(y -> IntStream.rangeClosed(-1, 1).filter(x -> x != 0 || y != 0).mapToObj(x -> new Coordinate(x, y))).toArray(Coordinate[]::new);
    public static final Comparator<Coordinate> Y_FIRST_COMPARATOR = Comparator.comparingInt(Coordinate::y).thenComparing(Coordinate::x);
    public static final Coordinate ORIGO = new Coordinate(0, 0);

    public static <D> Function<Coordinate, D> gridGetter(final Grid<D> grid) {
        return coordinate -> grid.get(coordinate.x(), coordinate.y());
    }

    public static Predicate<Coordinate> boundsOf(Grid<?> grid) {
        return coordinate -> coordinate.x() >= 0 && coordinate.y() >= 0 && coordinate.x() < grid.getColumnCount() && coordinate.y() < grid.getRowCount();
    }

    public static Stream<Coordinate> coordinatesOf(Grid<?> grid) {
        return IntStream.range(0, grid.getColumnCount()).boxed().flatMap(x -> IntStream.range(0, grid.getRowCount()).mapToObj(y -> new Coordinate(x, y)));
    }

    public Set<Coordinate> stepLine(final Coordinate to, final Predicate<Coordinate> bounds) {
        return lineWith(minus(to), bounds);
    }

    private Set<Coordinate> lineWith(final Coordinate diff, final Predicate<Coordinate> bounds) {
        return Stream.concat(Stream.iterate(this, bounds, diff::plus), Stream.iterate(this, bounds, ORIGO.minus(diff)::plus)).collect(Collectors.toSet());
    }

    public Set<Coordinate> line(final Coordinate to, final Predicate<Coordinate> bounds) {
        return lineWith(minus(to).reduce(), bounds);
    }

    public List<Coordinate> lineSegment(final Coordinate to) {
        return Stream.iterate(this, not(to::equals), to.minus(this).reduce()::plus).toList();
    }

    private Coordinate reduce() {
        final int divisor = BigInteger.valueOf(x()).gcd(BigInteger.valueOf(y())).intValue();
        return divisor == 1 ? this : new Coordinate(x() / divisor, y() / divisor);
    }

    public double arcTangent() {
        return java.lang.Math.atan2(y(), x());
    }

    public Coordinate clockwise() {
        return new Coordinate(-y(), x());
    }

    public Coordinate counterclockwise() {
        return new Coordinate(y(), -x());
    }

    public Stream<Coordinate> manhattanNeighbours(final Predicate<Coordinate> boundsCheck) {
        return Stream.of(MANHATTAN_COORDINATES).map(this::plus).filter(boundsCheck);
    }

    public Coordinate plus(final Coordinate other) {
        return new Coordinate(x + other.x, y + other.y);
    }

    public Stream<Coordinate> flatNeighbours(final Predicate<Coordinate> boundsCheck) {
        return Stream.of(CHEBYSHEV_COORDINATES).map(this::plus).filter(boundsCheck);
    }

    public Coordinate absDiff(final Coordinate other) {
        final int sign = Math.sgn(compareTo(other));
        return switch (sign) {
            case 0 ->
                    new Coordinate(0, 0);
            case 1 ->
                    minus(other);
            case -1 ->
                    other.minus(this);
            default ->
                    throw new IllegalStateException("Unexpected value: " + sign);
        };
    }

    @Override
    public int compareTo(final Coordinate other) {
        return Y_FIRST_COMPARATOR.compare(this, other);
    }

    public Coordinate minus(final Coordinate other) {
        return new Coordinate(x - other.x(), y - other.y());
    }

    public Coordinate times(int scalar) {
        return new Coordinate(x() * scalar, y() * scalar);
    }

    public Coordinate sgn() {
        return new Coordinate(Math.sgn(x()), Math.sgn(y()));
    }

    public int manhattanMagnitude() {
        return abs(x()) + abs(y());
    }

    public double magnitude() {
        return sqrt(x() * x() + y() * y());
    }

    public boolean coLinearWith(final Coordinate other) {
        return reduce().equals(other.reduce());
    }

    public Coordinate wrap(final Coordinate bounding) {
        return new Coordinate((x() % bounding.x() + bounding.x()) % bounding.x(), (y() % bounding.y() + bounding.y()) % bounding.y());
    }

    public Map<Coordinate, Integer> countComponents() {
        final Map<Coordinate, Integer> map = new HashMap<>();
        final Coordinate sign = sgn();
        map.put(new Coordinate(sign.x(), 0), abs(this.x()));
        map.put(new Coordinate( 0,sign.y()), abs(this.y()));
        map.remove(ORIGO);
        return map;
    }
}
