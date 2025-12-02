package com.w_wins.fixedwidth;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class SimpleGridParser implements Function<Stream<String>, Grid<String>> {
    private final GridConfig config;
    private final StringGridParser delegate;

    public SimpleGridParser(final GridConfig setConfig) {
        config = setConfig;
        delegate= new StringGridParser(setConfig);
    }

    @Override
    public Grid<String> apply(final Stream<String> lines) {
        final List<List<String>> rows = delegate.apply(lines);
        return new ListOfListsGrid<>(rows);
    }
}
