package com.w_wins.advent.twentytwentyfour.daysixteen;

import com.w_wins.adventcommon.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record DijkstraPath(
        DijkstraNode current,
        Optional<DijkstraPath> next,
        int count) {
    public DijkstraPath append(final DijkstraScore candidate) {
        return new DijkstraPath(candidate.node(), Optional.of(this), count() + 1);
    }

    public Set<Coordinate> asSet() {
        throw new UnsupportedOperationException();
    }

    private void update(final Set<Coordinate> set) {
        next.ifPresent(path -> {
            set.addAll(current().asListTo(path.current));
            path.update(set);
        });
    }

    public List<Coordinate> asList() {
        final List<Coordinate> returnValue = new ArrayList<>();
        update(returnValue);
        return returnValue;
    }

    private void update(final List<Coordinate> list) {
        next.ifPresent(path -> {
            list.addAll(current().asListTo(path.current));
            path.update(list);
        });
    }
}
