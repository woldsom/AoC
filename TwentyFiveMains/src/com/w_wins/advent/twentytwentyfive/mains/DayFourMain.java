package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayfour.WarehousePosition;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.GridParser;
import com.w_wins.fixedwidth.LabelledGrid;
import com.w_wins.iostream.Utf8ResourceLines;

public class DayFourMain {
    static void main() {
        final Grid<WarehousePosition> grid = new Utf8ResourceLines(WarehousePosition.class, "/test.txt").evaluate(new GridParser<>(GridConfig.CHAR_GRID, WarehousePosition::parse, null));
        IO.println(grid);
    }
}
