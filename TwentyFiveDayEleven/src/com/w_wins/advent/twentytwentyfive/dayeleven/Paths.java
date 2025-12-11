package com.w_wins.advent.twentytwentyfive.dayeleven;

import java.util.function.UnaryOperator;

public record Paths(int troubleCount,long paths) {
    public static Paths getStart() {
        return new Paths(0,1);
    }

    public static UnaryOperator<Paths> howPathsAreAffectedAt(final String deviceLabel) {
        return switch (deviceLabel) {
            case "dac" -> Paths::butDac;
            case "fft" -> Paths::butFft;
            default -> x -> x;
        };
    }

    public Paths add(Paths other) {
        if(troubleCount()>other.troubleCount()) {
            return this;
        } else if(troubleCount()<other.troubleCount()){
            return other;
        } else {
            return new Paths(troubleCount(),paths()+ other.paths());
        }
    }

    public Paths butDac() {
        return new Paths(troubleCount()+1,paths());
    }

    public Paths butFft() {
        return new Paths(troubleCount()+1,paths());
    }

    public long sum() {
        return paths();
    }
}
