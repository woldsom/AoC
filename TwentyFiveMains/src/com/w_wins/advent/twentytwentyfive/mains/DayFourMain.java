package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayfour.RollAndNeighbors;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.adventcommon.Grids;
import com.w_wins.collections.BinaryHeap;
import com.w_wins.common.CollectorUtil;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.GridParser;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

public class DayFourMain {
    static void main() {
        final Grid<Boolean> grid = new Utf8ResourceLines(RollAndNeighbors.class, "/input.txt").evaluate(new GridParser<>(GridConfig.CHAR_GRID, "@"::equals, null));
        final Predicate<Coordinate> boundsCheck = c -> c.x() >= 0 && c.y() >= 0 && c.x() < grid.getColumnCount() && c.y() < grid.getRowCount();
        final Map<Coordinate, Boolean> map = Grids.toMap(grid);
        iterateRemoval(new HashMap<>(map), boundsCheck);
        IntStream.range(0, 10000).forEach(index -> {
            long time = System.nanoTime();
            heapifyAndReduce(new HashMap<>(map), boundsCheck);
            long timeElapsed = System.nanoTime() - time;
            if (index % 100 == 0) {
                IO.println(index + ": elapsed;" + timeElapsed);
            }
        });
    }

    private static void heapifyAndReduce(final Map<Coordinate, Boolean> map, final Predicate<Coordinate> boundsCheck) {
        final Map<Coordinate, RollAndNeighbors> rolls = map.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).map(coordinate -> Map.entry(coordinate, new RollAndNeighbors(coordinate, coordinate.flatNeighbours(boundsCheck).filter(k -> map.getOrDefault(k, false)).collect(Collectors.toSet())))).collect(CollectorUtil.toMap());
        final int originalCount = rolls.size();
        final Function<RollAndNeighbors, Integer> keyExtractor = roll -> roll.neighbours().size();
        final BinaryHeap<Integer, RollAndNeighbors> heap = new BinaryHeap<>(rolls.values(), keyExtractor);
        while (heap.peek().neighbours().size() < 4) {
            final RollAndNeighbors removed = heap.pop();
            removed.neighbours().stream().filter(rolls::containsKey).map(rolls::get).forEach(neighbor -> {
                neighbor.neighbours().remove(removed.position());
                heap.decrementKey(neighbor, keyExtractor.apply(neighbor));
            });
            rolls.remove(removed.position());
        }
        final long remainingCount = heap.stream().count();
        //IO.println("Out of " + originalCount + " rolls, " + remainingCount + " remains, meaning " + (originalCount - remainingCount) + " was removed");
    }

    private static void iterateRemoval(final Map<Coordinate, Boolean> map, final Predicate<Coordinate> boundsCheck) {
        final long totalCount = map.values().stream().filter(x -> x).count();
        final long movableCount = map.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).filter(c -> c.flatNeighbours(boundsCheck).filter(map::get).count() < 4).count();
        IO.println(movableCount);
        map.entrySet().removeIf(not(Map.Entry::getValue));
        while (map.entrySet().removeIf(e -> e.getKey().flatNeighbours(boundsCheck).map(key -> map.getOrDefault(key, false)).filter(x -> x).count() < 4)) {
        }
        final long remainingCount = map.values().stream().filter(x -> x).count();
        IO.println("Out of " + totalCount + " rolls, " + remainingCount + " remains, meaning " + (totalCount - remainingCount) + " was removed.");
    }
}
