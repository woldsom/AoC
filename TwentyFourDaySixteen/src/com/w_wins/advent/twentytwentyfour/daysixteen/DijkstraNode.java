package com.w_wins.advent.twentytwentyfour.daysixteen;

import com.w_wins.adventcommon.Coordinate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record DijkstraNode(
        Coordinate where,boolean horizontalNext) {
    public List<Coordinate> asListTo(final DijkstraNode other) {
        return where().lineSegment(other.where());
    }
}
