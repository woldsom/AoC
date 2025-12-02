package com.w_wins.fixedwidth;

import java.util.Arrays;
import java.util.stream.IntStream;

public final class ArrayGrid<T> implements Grid<T> {
    private final T[][] array;

    public ArrayGrid(final T[][] setArray) {
        array = Arrays.copyOf(setArray, setArray.length);
        IntStream.range(0, setArray.length).forEach(index -> {
            array[index] = Arrays.copyOf(setArray[index], setArray[index].length);
        });
    }

    @Override
    public T get(final int column, final int row) {
        return array[row][column];
    }

    @Override
    public int getColumnCount() {
        return array[0].length;
    }

    @Override
    public int getRowCount() {
        return array.length;
    }
}
