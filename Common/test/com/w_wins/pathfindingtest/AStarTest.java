package com.w_wins.pathfindingtest;

import com.w_wins.fixedwidth.ArrayGrid;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.pathfinding.AStar;
import com.w_wins.pathfinding.ScoredPath;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class AStarTest {
    @Test
    void simplest() {
        final AStar<Integer, Integer> testObject = new AStar<>(Integer::sum, Comparator.naturalOrder(), () -> 0);
        final Optional<ScoredPath<Integer, Integer>> result = testObject.shortest(0, 1, x -> x == 0 ? Set.of(1) : Set.of(0), (a, b) -> 1, x -> 0);
        assertEquals(1, result.orElseThrow().cost());
    }

    @Test
    void simple() {
        final AStar<Integer, Integer> testObject = new AStar<>(Integer::sum, Comparator.naturalOrder(), () -> 0);
        final Optional<ScoredPath<Integer, Integer>> result = testObject.shortest(0, 10, x -> IntStream.rangeClosed(0, 10).filter(y -> y != x).boxed().collect(Collectors.toSet()), (a, b) -> (a - b) * (a - b), x -> 0);
        assertEquals(10, result.orElseThrow().cost());
    }

    @Test
    void numberTheory() {
        final AStar<Integer, Integer> testObject = new AStar<>(Integer::sum, Comparator.naturalOrder(), () -> 0);
        final Optional<ScoredPath<Integer, Integer>> result = testObject.shortest(10, 1, x -> x % 3 == 0 ? Set.of(x / 3, x + 1) : Set.of(x + 1), (a, b) -> a.equals(b) ? 0 : 1, x -> 0);
        assertTrue(result.isPresent());
        assertEquals(8, result.get().cost());
        assertIterableEquals(List.of(10,11,12,4,5,6,2,3,1),result.get().path());
    }

    @Test
    void EulerProblemEightyThreeExample() {
        final Grid<Integer> grid = new ArrayGrid<>(new Integer[][]{new Integer[]{131, 673, 234, 103, 18}, new Integer[]{201, 96, 342, 965, 150}, new Integer[]{630, 803, 746, 422, 111}, new Integer[]{537, 699, 497, 121, 956}, new Integer[]{805, 732, 524, 37, 331}});
        final AStar<Integer, Coordinate> testObject = new AStar<>(Integer::sum, Comparator.naturalOrder(), () -> grid.get(0, 0));
        final Optional<ScoredPath<Integer, Coordinate>> result = testObject.shortest(new Coordinate(0, 0), new Coordinate(4, 4), c -> {
            if (c.row() == 0) {
                if (c.column() == 0) {
                    return Set.of(new Coordinate(0, 1), new Coordinate(1, 0));
                } else if (c.column() == 4) {
                    return Set.of(new Coordinate(4, 1), new Coordinate(3, 0));
                } else {
                    return Set.of(new Coordinate(c.column() - 1, 0), new Coordinate(c.column() + 1, 0), new Coordinate(c.column(), 1));
                }
            } else if (c.row() == 4) {
                if (c.column() == 0) {
                    return Set.of(new Coordinate(0, 3), new Coordinate(1, 4));
                } else if (c.column() == 4) {
                    return Set.of(new Coordinate(4, 3), new Coordinate(3, 4));
                } else {
                    return Set.of(new Coordinate(c.column() - 1, 4), new Coordinate(c.column() + 1, 4), new Coordinate(c.column(), 3));
                }
            } else {
                if (c.column() == 0) {
                    return Set.of(new Coordinate(0, c.row() - 1), new Coordinate(0, c.row() + 1), new Coordinate(1, c.row()));
                } else if (c.column() == 4) {
                    return Set.of(new Coordinate(4, c.row() - 1), new Coordinate(4, c.row() + 1), new Coordinate(3, c.row()));
                } else {
                    return Set.of(new Coordinate(c.column() - 1, c.row()), new Coordinate(c.column() + 1, c.row()), new Coordinate(c.column(), c.row() - 1), new Coordinate(c.column(), c.row() + 1));
                }
            }
        }, (from, to) -> grid.get(to.column(), to.row()), c -> (8 - c.column() - c.row()) * 18);
        assertEquals(2297, result.orElseThrow().cost());
    }
}