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
import com.w_wins.pathfinding.ScoredPath;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.w_wins.advent.twentytwentyfive.dayseven.Seven.FLOOR;
import static com.w_wins.advent.twentytwentyfive.dayseven.Seven.PILL;
import static com.w_wins.advent.twentytwentyfive.dayseven.Seven.WALL;

public class DaySevenMain {
    static void main() {
        final Grid<Seven> grid = new Utf8ResourceLines(Seven.class, "/test.txt").evaluate(lines -> Grids.map(new SimpleGridParser(GridConfig.CHAR_GRID).apply(lines), Functions.extraLeft(Seven::parse)));
        Grids.debug(grid, x -> x.name().substring(0, 1));
        final List<Coordinate> pills = new ArrayList<>(Grids.findAll(grid, PILL));
        final ScoredPath<Integer, Coordinate> longestPath = CollectionUtil.selfCross(pills, (a, b) -> Grids.shortestPath(grid, a, b, WALL::equals).orElse(null)).filter(Objects::nonNull).max(Comparator.comparing(ScoredPath::cost)).orElseThrow();
        final List<Coordinate> pathCoordinates = longestPath.path();
        Grids.debug(Grids.map(grid,(coordinate,object)->{
            final int index = pathCoordinates.indexOf(coordinate);
            if(FLOOR==object && index>=0){
                return ""+index%10;
            } else {
                return object.symbol();
            }
        }),x->x);
    }
}
