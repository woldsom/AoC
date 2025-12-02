package com.w_wins.fixedwidth;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public final class GridParser<T> implements Function<Stream<String>, LabelledGrid<T>> {
    private final StringGridParser stringParser;
    private final GridConfig config;
    private final Function<String, T> cellParser;
    private final Function<String, String> headerParser;
    private final Map<HeaderType, Function<List<List<String>>, List<String>>> headerExtractors;
    private final Map<HeaderType, Consumer<List<List<String>>>> headerRemovers;

    public GridParser(final GridConfig setConfig, final Function<String, T> setCellParser, final Function<String, String> setHeaderParser) {
        config = setConfig;
        cellParser = setCellParser;
        headerParser = setHeaderParser;
        stringParser = new StringGridParser(setConfig);
        if (!config.headersPresent().isEmpty() && headerParser == null) {
            throw new NullPointerException("Missing headerParser when parsing " + config.headersPresent());
        }
        headerExtractors = new EnumMap<>(HeaderType.class);
        headerExtractors.put(HeaderType.LEFT, grid -> grid.stream().map(list -> list.get(0)).map(headerParser).toList());
        headerExtractors.put(HeaderType.RIGHT, grid -> grid.stream().map(list -> list.get(list.size() - 1)).map(headerParser).toList());
        headerExtractors.put(HeaderType.TOP, grid -> grid.get(0).stream().map(headerParser).toList());
        headerExtractors.put(HeaderType.BOTTOM, grid -> grid.get(grid.size() - 1).stream().map(headerParser).toList());
        headerRemovers = new EnumMap<>(HeaderType.class);
        headerRemovers.put(HeaderType.TOP, grid -> grid.remove(0));
        headerRemovers.put(HeaderType.BOTTOM, grid -> grid.remove(grid.size() - 1));
        headerRemovers.put(HeaderType.LEFT, grid -> grid.forEach(list -> list.remove(0)));
        headerRemovers.put(HeaderType.RIGHT, grid -> grid.forEach(list -> list.remove(list.size() - 1)));
    }

    @Override
    public LabelledGrid<T> apply(final Stream<String> lines) {
        final List<List<String>> stringGrid = stringParser.apply(lines);
        final Map<HeaderType, List<String>> headers = new EnumMap<>(HeaderType.class);
        config.headersPresent().forEach(header -> headers.put(header, headerExtractors.get(header).apply(stringGrid)));
        config.headersPresent().forEach(header -> headerRemovers.get(header).accept(stringGrid));
        final List<List<T>> convertedGrid = stringGrid.stream().map(line -> line.stream().map(cellParser).toList()).toList();
        return new DelegateLabelledGrid<>(new ListOfListsGrid<>(convertedGrid), headers);
    }
}
