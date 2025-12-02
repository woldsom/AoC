package com.w_wins.fixedwidth;

public interface Grid<T> {
    T get(int column, int row);

    int getColumnCount();
    int getRowCount();
}
