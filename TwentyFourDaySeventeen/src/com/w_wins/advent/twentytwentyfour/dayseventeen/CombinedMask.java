package com.w_wins.advent.twentytwentyfour.dayseventeen;

import java.util.Comparator;
import java.util.SortedSet;

public record CombinedMask(
        AMask a,
        BMask b) implements Comparable<CombinedMask> {
    public static CombinedMask of(final AMask a, final BMask b) {
        return new CombinedMask(a, b);
    }

    public static Comparator<CombinedMask> smallFirst() {
        return Comparator.comparing(CombinedMask::b, Comparator.comparingInt(BMask::shift));
    }

    @Override
    public int compareTo(final CombinedMask other) {
        return smallFirst().compare(this, other);
    }
}
