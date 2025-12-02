package com.w_wins.fixedwidth;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ListOfListsGrid<T> implements Grid<T> {
    private final List<List<T>> list;

    public ListOfListsGrid(final List<List<T>> setList) {
        list = setList.stream().map(ArrayList::new).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public T get(final int column, final int row) {
        return list.get(row).get(column);
    }

    @Override
    public int getColumnCount() {
        return list.isEmpty() ? 0 : list.get(0).size();
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
