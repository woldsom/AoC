package com.w_wins.common;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public final class Comparators {
    public static <T, E extends Comparable<? super E>> Comparator<T> byArray(final Function<T, E[]> elementMapper) {
        return (a, b) -> Arrays.compare(elementMapper.apply(a), elementMapper.apply(b));
    }
    public static <T, E extends Comparable<? super E>> Comparator<T> byInts(final Function<T, int[]> elementMapper) {
        return (a, b) -> Arrays.compare(elementMapper.apply(a), elementMapper.apply(b));
    }
    public static <T, E extends Comparable<? super E>> Comparator<T> byLongs(final Function<T, long[]> elementMapper) {
        return (a, b) -> Arrays.compare(elementMapper.apply(a), elementMapper.apply(b));
    }
}
