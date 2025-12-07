package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayseven.Seven;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.adventcommon.Grids;
import com.w_wins.common.CollectionUtil;
import com.w_wins.common.Functions;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.SimpleGridParser;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.w_wins.advent.twentytwentyfive.dayseven.Seven.BEGIN;
import static com.w_wins.advent.twentytwentyfive.dayseven.Seven.SPLITTER;

public class DaySevenMain {
    static void main() {
        final Grid<Seven> grid = new Utf8ResourceLines(Seven.class, "/input.txt").evaluate(lines -> Grids.map(new SimpleGridParser(GridConfig.CHAR_GRID).apply(lines), Functions.extraLeft(Seven::parse)));
        final SortedSet<Coordinate> splitters = new TreeSet<>(Grids.findAll(grid, SPLITTER));
        final Coordinate begin = Grids.find(grid, BEGIN);
        final Map<Integer,Long> beams = new HashMap<>(Map.of(begin.x(),1L));
        final int totalSplits = IntStream.range(begin.y(), grid.getRowCount()).map(y -> {
            final Set<Integer> lineSplitters = splitters.tailSet(new Coordinate(0, y)).headSet(new Coordinate(-1, y + 1)).stream().map(Coordinate::x).collect(Collectors.toSet());
            final Set<Integer> splits = CollectionUtil.intersection(lineSplitters, beams.keySet());
            splits.stream().forEach(x-> {
                beams.merge(x + 1, beams.get(x), Math::addExact);
                beams.merge(x - 1, beams.get(x), Math::addExact);
            });
            beams.keySet().removeAll(splits);
            return splits.size();
        }).sum();
        IO.println(totalSplits);
        IO.println(beams.values().stream().mapToLong(x->x).sum());
    }
}
