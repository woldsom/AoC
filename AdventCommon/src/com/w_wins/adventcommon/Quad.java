package com.w_wins.adventcommon;

import com.w_wins.common.Functions;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.ListOfListsGrid;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Quad<E>(
        E upperLeft,
        E upperRight,
        E lowerLeft,
        E lowerRight) {

    public static <S, D> Grid<D> invertGrid(final Grid<S> source, Function<Quad<S>, D> mapping, Function<Coordinate, S> fill) {
        return new ListOfListsGrid<>(IntStream.rangeClosed(0, source.getRowCount()).mapToObj(rowIndex -> IntStream.rangeClosed(0, source.getColumnCount()).mapToObj(columnIndex -> new Coordinate(columnIndex, rowIndex)).map(Functions.bindRight(Quad::extract, Functions.fromPredicate(Coordinate.boundsOf(source), Coordinate.gridGetter(source), fill))).map(mapping).toList()).toList());
    }

    public static <T> Quad<T> extract(final Coordinate target, final Function<Coordinate, T> getter) {
        return Quad.ofList(Arrays.stream(COORDINATES.values()).map(COORDINATES::coordinate).map(target::plus).map(getter).toList());
    }

    private static <T> Quad<T> ofList(final List<T> list) {
        return new Quad<>(list.get(0), list.get(1), list.get(2), list.get(3));
    }

    public boolean kittyCorner() {
        return upperLeft().equals(lowerRight()) || upperRight().equals(lowerLeft());
    }

    public Grid<E> asGrid() {
        return new ListOfListsGrid<>(List.of(List.of(upperLeft(), upperRight()), List.of(lowerLeft(), lowerRight())));
    }

    public Stream<E> stream() {
        return Stream.of(upperLeft(), upperRight(), lowerLeft(), lowerRight());
    }

    public Stream<List<E>> neighbours() {
        return Stream.of(List.of(upperLeft(), upperRight()), List.of(upperRight(), lowerRight()), List.of(lowerRight(), lowerLeft()), List.of(lowerLeft(), upperLeft()));
    }

    public enum COORDINATES {
        UPPER_LEFT(new Coordinate(-1, -1)), UPPER_RIGHT(new Coordinate(0, -1)), LOWER_LEFT(new Coordinate(-1, 0)), LOWER_RIGHT(new Coordinate(0, 0));

        private final Coordinate coordinate;

        COORDINATES(final Coordinate setCoordinate) {
            coordinate = setCoordinate;
        }

        public Coordinate coordinate() {
            return coordinate;
        }
    }
}
