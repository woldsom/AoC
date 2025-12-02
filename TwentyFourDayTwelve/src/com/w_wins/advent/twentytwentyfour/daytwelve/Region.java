package com.w_wins.advent.twentytwentyfour.daytwelve;

import com.w_wins.adventcommon.Coordinate;

import java.util.Set;

public record Region(
        String what,
        Set<Coordinate> area) {

}
