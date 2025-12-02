package com.w_wins.advent.twentytwentyfour.daysix;

import java.util.Set;

public record PathAndResult(
        Set<PositionAndDirection> path,
        boolean loops
) {
}
