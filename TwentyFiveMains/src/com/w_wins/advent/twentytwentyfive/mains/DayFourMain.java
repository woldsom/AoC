package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayfour.WarehousePosition;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.adventcommon.Grids;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.GridParser;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.Map;
import java.util.function.Predicate;

import static com.w_wins.advent.twentytwentyfive.dayfour.WarehousePosition.ROLL;

public class DayFourMain {
    static void main() {
        final Grid<WarehousePosition> grid = new Utf8ResourceLines(WarehousePosition.class, "/test.txt").evaluate(new GridParser<>(GridConfig.CHAR_GRID, WarehousePosition::parse, null));
        final Predicate<Coordinate> boundsCheck = c->c.x()>=0&&c.y()>=0 && c.x()<grid.getColumnCount() && c.y()<grid.getRowCount();
        final Map<Coordinate, WarehousePosition> map = Grids.toMap(grid);
        final long totalCount = map.values().stream().filter(ROLL::equals).count();
        final long movableCount = map.entrySet().stream().filter(e -> ROLL.equals(e.getValue())).map(Map.Entry::getKey).filter(c->c.flatNeighbours(boundsCheck).filter(n->map.get(n)== ROLL).count() < 4).count();
        IO.println(movableCount);
        map.entrySet().removeIf(e->e.getValue()!= ROLL);
        while(map.entrySet().removeIf(e->e.getKey().flatNeighbours(boundsCheck).map(key->map.get(key)).filter(ROLL::equals).count()<4)){
            IO.println("Rolling rolling rolling");
        }
        final long remainingCount = map.values().stream().filter(ROLL::equals).count();
        IO.println("Out of "+totalCount+" rolls, "+remainingCount+" remains, meaning "+(totalCount-remainingCount)+" was removed.");
    }
}
