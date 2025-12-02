package com.w_wins.advent.twentytwentyfour.daysixteen;

import com.w_wins.adventcommon.Coordinate;

public enum Direction {
    LEFT_TURN,FORWARD,RIGHT_TURN;

    public Coordinate turn(final Coordinate newDirection) {
        return switch (this) {
            case LEFT_TURN ->
                    newDirection.counterclockwise();
            case FORWARD ->
                    newDirection;
            case RIGHT_TURN ->
                    newDirection.clockwise();
        };
    }
}
