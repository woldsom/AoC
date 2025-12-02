package com.w_wins.adventcommon;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AdventTwoPartEvaluator implements Function<Stream<String>, String> {
    private final Function<Stream<String>, String> partOne;
    private final Function<Stream<String>, String> partTwo;

    public AdventTwoPartEvaluator(Function<Stream<String>, ?> setPartOne, Function<Stream<String>, ?> setPartTwo) {
        partOne = setPartOne.andThen(Object::toString);
        partTwo = setPartTwo.andThen(Object::toString);
    }

    @Override
    public String apply(final Stream<String> stream) {
        final List<String> buffer = stream.toList();
        return "Part one: " + partOne.apply(buffer.stream()) + ", Part two:" + partTwo.apply(buffer.stream());
    }
}
