package com.w_wins.advent.twentytwentyfive.dayeight;

import java.util.HashSet;
import java.util.Set;

public record Net(Set<Edge> edges, Set<Coordinate> coordinates) {

    public void subsume(final Edge edge) {
        edges().add(edge);
        coordinates().addAll(edge.points());
    }

    public int size() {
        return coordinates().size();
    }

    public Net union(final Net other) {
        return new Net(union(edges(), other.edges()), union(coordinates(), other.coordinates()));
    }

    private <T> Set<T> union(final Set<T> a, final Set<T> b) {
        final Set<T> returnValue = new HashSet<>();
        returnValue.addAll(a);
        returnValue.addAll(b);
        return returnValue;
    }
}
