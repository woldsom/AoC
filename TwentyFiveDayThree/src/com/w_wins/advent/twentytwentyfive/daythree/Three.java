package com.w_wins.advent.twentytwentyfive.daythree;

import java.util.List;
import java.util.stream.Stream;

public record Three() {
    public static List<Three> parse(final Stream<String> lines) {
        return lines.map(Three::parse).toList();
    }

    public static Three parse(final String line) {
        return new Three();
    }
}
