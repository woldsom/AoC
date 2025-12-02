package com.w_wins.advent.twentytwentyfour.daytwentyone;

import com.w_wins.common.Streams;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public record TwentyOne(
        String joined) {
    public static TwentyOne parse(Stream<String> lines) {
        final String joined = lines.flatMap(line -> Streams.split(line, " ")).collect(Collectors.joining());
        return new TwentyOne(joined);
    }
}
