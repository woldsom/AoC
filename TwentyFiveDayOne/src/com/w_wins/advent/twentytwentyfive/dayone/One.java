package com.w_wins.advent.twentytwentyfive.dayone;

import com.w_wins.common.Streams;

import java.util.stream.Stream;

public record One() {
    public static void doStuff(){
        Streams.allDistinct(Stream.of("a","b"));
    }
}
