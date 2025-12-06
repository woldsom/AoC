package com.w_wins.iostream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LineGroupEvaluator<T, A, F> implements Function<Stream<String>, T> {
    private final Collector<F, A, T> collector;
    private final Function<Stream<String>, F> groupFunction;

    public LineGroupEvaluator(final Function<Stream<String>, F> setGroupFunction, final Collector<F, A, T> setCollector) {
        groupFunction = setGroupFunction;
        collector = setCollector;
    }

    @Override
    public T apply(final Stream<String> stringStream) {
        final List<String> lineAccumulator = new ArrayList<>();
        final A container = collector.supplier().get();
        stringStream.forEach(line -> {
            if (line.isBlank()) {
                add(lineAccumulator, container);
            } else {
                lineAccumulator.add(line);
            }
        });
        add(lineAccumulator, container);
        return collector.finisher().apply(container);
    }

    private void add(final List<String> lineAccumulator, final A container) {
        if (!lineAccumulator.isEmpty()) {
            collector.accumulator().accept(container, groupFunction.apply(lineAccumulator.stream()));
            lineAccumulator.clear();
        }
    }
}
