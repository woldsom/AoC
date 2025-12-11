package com.w_wins.advent.twentytwentyfive.dayeleven;

import java.util.function.UnaryOperator;

public record Paths(long noTrouble, long onlyDac, long onlyFft, long both) {
    public static Paths getStart() {
        return new Paths(1, 0, 0, 0);
    }

    public static UnaryOperator<Paths> howPathsAreAffectedAt(final String deviceLabel) {
        return switch (deviceLabel) {
            case "dac" -> Paths::butDac;
            case "fft" -> Paths::butFft;
            default -> x -> x;
        };
    }

    public Paths add(Paths other) {
        return new Paths(noTrouble() + other.noTrouble(), onlyDac() + other.onlyDac(), onlyFft() + other.onlyFft(), both() + other.both());
    }

    public Paths butDac() {
        return new Paths(0, noTrouble(), 0, onlyFft());
    }

    public Paths butFft() {
        return new Paths(0, 0, noTrouble(), onlyDac());
    }

    public long sum() {
        return noTrouble() + onlyDac() + onlyFft() + both();
    }
}
