package com.w_wins.advent.twentytwentyfive.dayeight;

import java.util.HashSet;
import java.util.Set;

public record Net(Set<Edge> edges, Set<Coordinate> coordinates) {
    public Net union(final Net other) {
        return new Net(union(edges(),other.edges()),union(coordinates(),other.coordinates()));
    }

    private <T> Set<T> union(final Set<T> a, final Set<T> b) {
        final Set<T> returnValue = new HashSet<>();
        returnValue.addAll(a);
        returnValue.addAll(b);
        return returnValue;
    }
}
