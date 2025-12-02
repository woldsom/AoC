package com.w_wins.adventcommon;

public record Beam<T>(
        Coordinate coordinate,
        Coordinate direction,
        T value) {
    public Beam<T> flip() {
        return new Beam<>(coordinate(),Coordinate.ORIGO.minus(direction()),value());
    }
}
