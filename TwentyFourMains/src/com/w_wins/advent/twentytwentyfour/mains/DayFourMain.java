package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.dayfour.Four;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.adventcommon.Mask;
import com.w_wins.common.Functions;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.SimpleGridParser;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class DayFourMain {
    void main() {
        final Grid<String> grid = new Utf8ResourceLines(Four.class, "/input.txt").evaluate(new SimpleGridParser(GridConfig.CHAR_GRID));
        final Mask partOneMask = new Mask(IntStream.range(0, 4).mapToObj(x -> new Coordinate(x, 0)).toList());
        final Mask partTwoMask = new Mask(List.of(
                new Coordinate(-1, -1),
                new Coordinate(0, 0),
                new Coordinate(1, 1),
                new Coordinate(-1, 1),
                new Coordinate(1, -1)
        ));
        final Set<Mask> partOneMasks = new HashSet<>(Set.of(partOneMask));
        partOneMasks.addAll(partOneMasks.stream().map(mask -> mask.transform(Coordinate.ORIGO::minus)).collect(Collectors.toSet()));
        partOneMasks.addAll(partOneMasks.stream().map(mask -> mask.transform(Coordinate::clockwise)).collect(Collectors.toSet()));
        partOneMasks.addAll(partOneMasks.stream().map(mask -> mask.transform(coordinate -> coordinate.plus(coordinate.clockwise()))).collect(Collectors.toSet()));
        final Set<Mask> partTwoMasks = new HashSet<>(Set.of(partTwoMask));
        partTwoMasks.addAll(partTwoMasks.stream().map(mask -> mask.transform(Coordinate.ORIGO::minus)).collect(Collectors.toSet()));
        partTwoMasks.addAll(partTwoMasks.stream().map(mask -> mask.transform(Coordinate::clockwise)).collect(Collectors.toSet()));
        System.out.println(count(grid, partOneMasks, "XMAS"));
        System.out.println(count(grid, partTwoMasks, "MASMS"));
    }

    public long count(final Grid<String> grid, final Set<Mask> masks, final String target) {
        return masks.stream().map(Functions.bindRight(Mask::apply, grid)).map(Map::values).mapToLong(coordinateValues -> {
            return coordinateValues.stream().filter(word -> {
                return word.stream().allMatch(Optional::isPresent);
            }).map(word -> {
                return word.stream().map(Optional::orElseThrow).collect(Collectors.joining());
            }).filter(target::equals).count();
        }).sum();
    }
}
