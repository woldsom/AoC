package com.w_wins.numbers.enumerating;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public record CoprimePair(long smaller, long larger) {
    public CoprimePair {
        if (smaller >= larger) {
            throw new IllegalArgumentException("Wrong order of arguments (" + smaller + ", " + larger + ")");
        }
    }

    public static Stream<CoprimePair> stream() {
        return LongStream.rangeClosed(1, Long.MAX_VALUE).mapToObj(CoprimePair::fromLong);
    }

    public static CoprimePair fromLong(final long l) {
        return fromTernaryString(Long.toString(l, 3));
    }

    private static CoprimePair fromTernaryString(final String ternary) {
        final AtomicReference<CoprimePair> step = new AtomicReference<>(switch (ternary.substring(0, 1)) {
            case "1" -> new CoprimePair(1, 2);
            case "2" -> new CoprimePair(1, 3);
            default -> throw new IllegalArgumentException("Not ternary natural number: " + ternary);
        });
        final Function<String, UnaryOperator<CoprimePair>> findOperator = operation -> switch (operation) {
            case "0" -> old -> new CoprimePair(old.larger(), Math.multiplyExact(old.larger(), 2) - old.smaller());
            case "1" -> old -> new CoprimePair(old.larger(), Math.addExact(Math.multiplyExact(old.larger(), 2), old.smaller()));
            case "2" -> old -> new CoprimePair(old.smaller(), Math.addExact(Math.multiplyExact(old.smaller(), 2), old.larger()));
            default -> throw new IllegalArgumentException("Not ternary digit: " + operation);
        };
        IntStream.range(1, ternary.length()).mapToObj(index -> ternary.substring(index, index + 1)).map(findOperator).forEachOrdered(step::getAndUpdate);
        return step.get();
    }
}
