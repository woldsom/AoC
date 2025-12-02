package com.w_wins.advent.twentytwentyfour.daysix;

import com.w_wins.adventcommon.Coordinate;

public record PositionAndDirection(Coordinate position,
                                   Coordinate direction) {
    public Coordinate next() {
        return position().plus(direction());
    }

    public PositionAndDirection turn() {
        return new PositionAndDirection(position(),direction().clockwise());
    }

    public PositionAndDirection walk() {
        return new PositionAndDirection(next(),direction());
    }
}
