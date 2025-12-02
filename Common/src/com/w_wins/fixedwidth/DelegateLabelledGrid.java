package com.w_wins.fixedwidth;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DelegateLabelledGrid<T> implements LabelledGrid<T> {
    private final Map<HeaderType,List<String>> headers;
    private final Grid<T> original;

    public DelegateLabelledGrid(final Grid<T> setOriginal, final Map<HeaderType, List<String>> setHeaders) {
        headers = setHeaders;
        original = setOriginal;
    }

    @Override
    public T get(final int column, final int row) {
        return original.get(column, row);
    }

    @Override
    public int getColumnCount() {
        return original.getColumnCount();
    }

    @Override
    public int getRowCount() {
        return original.getRowCount();
    }

    @Override
    public String toString() {
        return original.toString();
    }

    @Override
    public List<String> getHeader(final HeaderType headerType) {
        return headers.get(headerType);
    }
}
