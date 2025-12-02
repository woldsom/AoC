package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.dayeight.Eight;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.Functions;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.SimpleGridParser;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public final class DayEightMain {
    void main() {
        final Grid<String> grid = new Utf8ResourceLines(Eight.class, "/input.txt").evaluate(new SimpleGridParser(GridConfig.CHAR_GRID));
        final Map<String, Set<Coordinate>> antennas = Coordinate.coordinatesOf(grid).collect(Collectors.groupingBy(Coordinate.gridGetter(grid), Collectors.toSet()));
        antennas.remove(".");
        System.out.println(getAntiNodes(antennas, DayEightMain::partOneAntiNodes, Coordinate.boundsOf(grid)).size());
        System.out.println(getAntiNodes(antennas, DayEightMain::partTwoAntiNodes, Coordinate.boundsOf(grid)).size());
    }

    public static Set<Coordinate> getAntiNodes(final Map<String, Set<Coordinate>> antennas, final BiFunction<Set<Coordinate>, Predicate<Coordinate>, Set<Coordinate>> method, final Predicate<Coordinate> gridBounds) {
        return antennas.values().stream().map(Functions.bindRight(method, gridBounds)).reduce(Functions.createAndModifySame(HashSet::new, Set::addAll)).orElseThrow();
    }

    public static Set<Coordinate> partOneAntiNodes(final Set<Coordinate> antennas, final Predicate<Coordinate> bounds) {
        return antennas.stream().flatMap(first -> {
            return antennas.stream().filter(not(first::equals)).map(second -> second.plus(second.minus(first))).filter(bounds);
        }).collect(Collectors.toSet());
    }

    public static Set<Coordinate> partTwoAntiNodes(final Set<Coordinate> antennas, final Predicate<Coordinate> bounds) {
        return antennas.stream().flatMap(first -> {
            return antennas.stream().filter(second -> first.compareTo(second) < 0).flatMap(second -> {
                return first.stepLine(second, bounds).stream();
            });
        }).collect(Collectors.toSet());
    }
}
