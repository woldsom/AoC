package com.w_wins.advent.twentytwentyfive.daytwelve;

import com.w_wins.adventcommon.Grids;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.SimpleGridParser;

import java.util.List;
import java.util.Optional;

public record Shape(Grid<String> grid) {
    public static Optional<Shape> tryParse(final List<String> lineGroup) {
        final String firstLine = lineGroup.getFirst();
        if (firstLine.charAt(firstLine.length() - 1) == ':') {
            return Optional.of(new Shape(new SimpleGridParser(GridConfig.CHAR_GRID).apply(lineGroup.subList(1, lineGroup.size()).stream())));
        } else {
            return Optional.empty();
        }
    }

    public int weight() {
        return Math.toIntExact(Grids.stream(grid()).filter("#"::equals).count());
    }
}
