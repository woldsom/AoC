package com.w_wins.adventcommon;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.ListOfListsGrid;
import com.w_wins.pathfinding.AStar;
import com.w_wins.pathfinding.ScoredPath;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public final class Grids {
    public static <T> Stream<T> stream(Grid<T> grid) {
        return IntStream.range(0, grid.getRowCount()).boxed().flatMap(row -> IntStream.range(0, grid.getColumnCount()).mapToObj(column -> grid.get(column, row)));
    }

    public static <S, D> Grid<D> map(final Grid<S> source, final BiFunction<Coordinate, S, D> transform) {
        return new ListOfListsGrid<>(IntStream.range(0, source.getRowCount()).boxed().map(row -> IntStream.range(0, source.getColumnCount()).mapToObj(column -> transform.apply(new Coordinate(column, row), source.get(column, row))).toList()).toList());
    }

    public static <T> void debug(final Grid<T> grid, final Function<T, String> stringMapping) {
        System.err.println("Grid of " + grid.getColumnCount() + " columns and " + grid.getRowCount() + " rows:\n" + IntStream.range(0, grid.getRowCount()).boxed().map(row -> IntStream.range(0, grid.getColumnCount()).mapToObj(column -> grid.get(column, row)).map(stringMapping).collect(Collectors.joining())).collect(Collectors.joining("\n")));
    }

    public static <T> Set<Coordinate> floodFill(final Grid<T> grid, final Coordinate start, final BiFunction<T, T, Boolean> test) {
        final Set<Coordinate> visit = new HashSet<>();
        final Set<Coordinate> inside = new HashSet<>();
        final Function<Coordinate, T> gridGet = Coordinate.gridGetter(grid);
        visit.add(start);
        while (!visit.isEmpty()) {
            final Coordinate current = visit.stream().findAny().orElseThrow();
            final T currentElement = gridGet.apply(current);
            current.manhattanNeighbours(Coordinate.boundsOf(grid)).filter(not(inside::contains)).filter(neighbour -> test.apply(currentElement, gridGet.apply(neighbour))).forEach(visit::add);
            inside.add(current);
            visit.remove(current);
        }
        return inside;
    }

    public static <T> Map<Coordinate, T> toMap(final Grid<T> grid) {
        return Coordinate.coordinatesOf(grid).map(Functions.entry(Coordinate.gridGetter(grid))).collect(CollectorUtil.toMap());
    }

    public static <T> Optional<ScoredPath<Integer, Coordinate>> shortestPath(final Grid<T> grid, final Coordinate start, final Coordinate end, final Predicate<T> isWall) {
        final AStar<Integer, Coordinate> aStar = new AStar<>(Math::addExact, Integer::compareTo, () -> 0);
        final Predicate<Coordinate> bounds = Coordinate.boundsOf(grid);
        final Function<Coordinate, T> getter = Coordinate.gridGetter(grid);
        return aStar.shortest(start, end, c -> c.manhattanNeighbours(bounds).filter(n -> !isWall.test(getter.apply(n))).collect(Collectors.toSet()), (_, _) -> 1, c -> c.absDiff(end).manhattanMagnitude());
    }

    public static Optional<ScoredPath<Integer, Coordinate>> shortestPath(final Grid<String> grid, final String start, final String end, final String wall) {
        return shortestPath(grid, find(grid, start), find(grid, end), wall::equals);
    }

    public static Optional<ScoredPath<Integer, Coordinate>> shortestPath(final Grid<String> grid) {
        return shortestPath(grid, find(grid, "S"), find(grid, "E"), "#"::equals);
    }

    public static <T> Coordinate find(final Grid<T> grid, final T value) {
        return Coordinate.coordinatesOf(grid).filter(c -> value.equals(Coordinate.gridGetter(grid).apply(c))).collect(CollectorUtil.singleton());
    }

    public static <T> Set<Coordinate> findAll(final Grid<T> grid, final T value) {
        return Coordinate.coordinatesOf(grid).filter(c -> value.equals(Coordinate.gridGetter(grid).apply(c))).collect(Collectors.toSet());
    }

    public static <T> Grid<T> alter(final Grid<T> grid, final int column, final int row, final T replacement) {
        return new Grid<>() {
            @Override
            public T get(final int columnRead, final int rowRead) {
                if (columnRead == column && rowRead == row) {
                    return replacement;
                } else {
                    return grid.get(columnRead, rowRead);
                }
            }

            @Override
            public int getColumnCount() {
                return grid.getColumnCount();
            }

            @Override
            public int getRowCount() {
                return grid.getRowCount();
            }
        };
    }

    public static Optional<ScoredPath<Integer, Coordinate>> getShortestPath(final Grid<String> grid) {
        return shortestPath(grid, "S", "E", "#");
    }
}
