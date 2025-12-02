package com.w_wins.iostream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class TwoLineGroupBuilder<V,U> implements Function<Stream<String>, TwoLineGroupBuilder<V,U>>{
    private final Function<Stream<String>, V> firstMap;
    private final Function<Stream<String>, U> secondMap;
    private V firstResult;
    private U secondResult;

    public TwoLineGroupBuilder(final Function<Stream<String>, V> setFirstMap, final Function<Stream<String>,U> setSecondMap) {
        firstMap=setFirstMap;
        secondMap=setSecondMap;
    }

    public TwoLineGroupBuilder<V,U> apply(final Stream<String> stringStream) {
        final List<String> lineAccumulator = new ArrayList<>();
        stringStream.forEach(line -> {
            if (line.isBlank()) {
                firstResult = firstMap.apply(lineAccumulator.stream());
                lineAccumulator.clear();
            } else {
                lineAccumulator.add(line);
            }
        });
        secondResult = secondMap.apply(lineAccumulator.stream());
        lineAccumulator.clear();
        return this;
    }

    public V getFirstGroupResult() {
        return firstResult;
    }

    public U getSecondGroupResult() {
        return secondResult;
    }
}
