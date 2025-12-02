package com.w_wins.advent.twentytwentyfour.dayten;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.CollectorUtil;
import com.w_wins.fixedwidth.Grid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

public record Topography(
        Grid<Integer> heights) {
    public Set<List<Coordinate>> paths() {
        final List<List<Coordinate>> paths = new LinkedList<>();
        final Function<Coordinate, Integer> getHeight = Coordinate.gridGetter(heights());
        Coordinate.coordinatesOf(heights()).filter(potentialTrailHead -> getHeight.apply(potentialTrailHead) == 0).forEach(potentialTrailHead -> paths.add(new ArrayList<>(List.of(potentialTrailHead))));
        IntStream.range(1, 10).forEach(target -> {
            final List<List<Coordinate>> additionalPaths = new ArrayList<>();
            final Iterator<List<Coordinate>> iterator = paths.iterator();
            while (iterator.hasNext()) {
                final List<Coordinate> path = iterator.next();
                final List<Coordinate> expanded = path.getLast().manhattanNeighbours(Coordinate.boundsOf(heights())).filter(where -> getHeight.apply(where) == target).toList();
                if (expanded.isEmpty()) {
                    iterator.remove();
                } else if (expanded.size() == 1) {
                    path.add(expanded.stream().collect(CollectorUtil.singleton()));
                } else {
                    iterator.remove();
                    expanded.stream().map(last -> {
                        final ArrayList<Coordinate> newPath = new ArrayList<>(path);
                        newPath.add(last);
                        return newPath;
                    }).forEach(additionalPaths::add);
                }
            }
            paths.addAll(additionalPaths);
        });
        return new HashSet<>(paths);
    }
}
