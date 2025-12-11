package com.w_wins.common;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public final class Maps {
    @Deprecated
    public static <K, V> Map.Entry<K, V> entry(final K key, final V value) {
        return Map.entry(key, value);
    }

    public static <K, V> Stream<Map.Entry<K, V>> entrySet(final Map<K, V> map) {
        return map.entrySet().stream();
    }

    public static <K, V> BinaryOperator<Map<K, V>> valueReduction(final BinaryOperator<V> valueOperation) {
        return Functions.andReturnLeft((Map<K, V> m1, Map<K, V> m2) -> m2.forEach((k, v) -> m1.merge(k, v, valueOperation)))::apply;
    }

    public static <K> Integer incrementValue(K k, Integer v) {
        return v == null ? 1 : v + 1;
    }
}
