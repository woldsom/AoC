package com.w_wins.advent.twentytwentyfive.dayeight;

import com.w_wins.common.Strings;

import java.util.Set;

public record Coordinate(int x, int y, int z) {
    public static Coordinate parse(String s) {
        final int[] values = Strings.split(s, ",").mapToInt(Integer::parseInt).toArray();
        return new Coordinate(values[0], values[1], values[2]);
    }

    public Edge edge(final Coordinate other) {
        final Coordinate delta = deltaTo(other);
        return new Edge(Set.of(this,other),delta.magnitude());
    }

    private long magnitude() {
        return Math.addExact(Math.addExact(Math.multiplyExact((long) x(), x()), Math.multiplyExact((long) y(), y())), Math.multiplyExact((long) z(), z()));
    }

    private Coordinate deltaTo(final Coordinate other) {
        return new Coordinate(x() - other.x(), y() - other.y(), z() - other.z());
    }
}
