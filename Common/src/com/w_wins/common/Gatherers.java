package com.w_wins.common;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Gatherer;

public final class Gatherers {
    public static Gatherer<Integer, ?, NavigableMap<Integer, Integer>> runs() {
        return Gatherer.<Integer, NavigableMap<Integer, Integer>, NavigableMap<Integer, Integer>>ofSequential(TreeMap::new, (state, element, downstream) -> {
            if (state.isEmpty()) {
                state.put(element, 1);
            } else {
                final int runStart = state.firstKey();
                final int runLength = state.firstEntry().getValue();
                if (element == runStart + runLength) {
                    state.clear();
                    state.put(runStart, runLength + 1);
                } else {
                    downstream.push(new TreeMap<>(state));
                    state.clear();
                    state.put(element, 1);
                }
            }
            return true;
        }, (a, b) -> b.push(a));
    }
}
