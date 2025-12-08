package com.w_wins.advent.twentytwentyfive.dayeight;

import java.util.Comparator;
import java.util.Set;

public record Edge(Set<Coordinate> points, long squareLength) implements Comparable<Edge>{

    public static final Comparator<Edge> COMPARATOR = Comparator.comparing(Edge::squareLength);

    public Edge {
        if (points.size() != 2) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int compareTo(final Edge o) {
        return COMPARATOR.compare(this,o);
    }
}
