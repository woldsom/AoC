package com.w_wins.advent.twentytwentyfive.dayeleven;

import java.util.Set;

public record Paths(int troubleCount, long paths) {
    private static final Set<String> TROUBLE = Set.of("dac", "fft");

    public static Paths getStart() {
        return new Paths(0, 1);
    }

    public Paths visit(String deviceLabel) {
        if (TROUBLE.contains(deviceLabel)) {
            return new Paths(troubleCount() + 1, paths());
        } else {
            return this;
        }
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
