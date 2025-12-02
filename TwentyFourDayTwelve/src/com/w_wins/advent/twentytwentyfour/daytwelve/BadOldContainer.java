package com.w_wins.advent.twentytwentyfour.daytwelve;

import com.w_wins.adventcommon.Coordinate;

import java.util.concurrent.atomic.AtomicReference;

public record BadOldContainer(
        Coordinate at,
        String what,
        AtomicReference<RegionOld> state) {
}
