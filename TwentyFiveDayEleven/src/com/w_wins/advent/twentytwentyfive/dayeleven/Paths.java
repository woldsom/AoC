package com.w_wins.advent.twentytwentyfive.dayeleven;

import java.util.function.UnaryOperator;

public record Paths(int troubleCount, long paths) {
    public static Paths getStart() {
        return new Paths(0, 1);
    }

    public static UnaryOperator<Paths> howPathsAreAffectedAt(final String deviceLabel) {
        return switch (deviceLabel) {
            case "dac", "fft" -> paths -> new Paths(paths.troubleCount() + 1, paths.paths());
            default -> x -> x;
        };
    }

    public Paths add(Paths other) {
        if (troubleCount() > other.troubleCount()) {
            return this;
        } else if (troubleCount() < other.troubleCount()) {
            return other;
        } else {
            return new Paths(troubleCount(), paths() + other.paths());
        }
    }
}
