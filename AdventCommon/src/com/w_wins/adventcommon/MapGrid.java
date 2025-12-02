package com.w_wins.adventcommon;

import com.w_wins.fixedwidth.Grid;

import java.util.Map;
import java.util.Optional;

public class MapGrid<T> implements Grid<T> {
    private final Map<Coordinate, T> map;
    private final int columnCount;
    private final int rowCount;
    private final T defaultValue;

    public MapGrid(final Map<Coordinate, T> setMap, final int setColumnCount, final int setRowCount, final T setDefaultValue) {
        map = setMap;
        columnCount = setColumnCount;
        rowCount = setRowCount;
        defaultValue = setDefaultValue;
    }

    @Override
    public T get(final int column, final int row) {
        return map.getOrDefault(new Coordinate(column, row),defaultValue);
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }
}
