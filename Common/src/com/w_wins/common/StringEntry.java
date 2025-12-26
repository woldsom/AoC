package com.w_wins.common;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class StringEntry {
    public static Map.Entry<String, String> split(final String original, final String divider) {
        final String[] parts = original.split(Pattern.quote(divider), 2);
        return Map.entry(parts[0], parts[1]);
    }

    public static <K> Map.Entry<K, String> split(final String original, final String divider, final Function<String, K> processKey) {
        final String[] parts = original.split(Pattern.quote(divider), 2);
        return Map.entry(processKey.apply(parts[0]), parts[1]);
    }

    public static <K, V> Map.Entry<K, V> split(final String original, final String divider, final Function<String, K> processKey, final Function<String, V> processValue) {
        final String[] parts = original.split(Pattern.quote(divider), 2);
        return Map.entry(processKey.apply(parts[0]), processValue.apply(parts[1]));
    }

    public static <K, V> Map.Entry<K, V> splitReverse(final String original, final String divider, final Function<String, K> processKey, final Function<String, V> processValue) {
        final String[] parts = original.split(Pattern.quote(divider), 2);
        return Map.entry(processKey.apply(parts[1]), processValue.apply(parts[0]));
    }
}
