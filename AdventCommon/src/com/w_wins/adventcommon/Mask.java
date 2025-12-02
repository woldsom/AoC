package com.w_wins.adventcommon;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.fixedwidth.Grid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public record Mask(
        List<Coordinate> relativeCoordinates) {
    public Mask transform(final UnaryOperator<Coordinate> function) {
        return new Mask(relativeCoordinates().stream().map(function).toList());
    }

    public <T> Map<Coordinate, List<Optional<T>>> apply(final Grid<T> grid) {
        final Predicate<Coordinate> bounds = Coordinate.boundsOf(grid);
        return Coordinate.coordinatesOf(grid).map(Functions.entry(coordinate -> {
            return relativeCoordinates().stream().map(coordinate::plus).map(target -> bounds.test(target) ? Optional.of(grid.get(target.x(), target.y())) : Optional.<T>empty()).toList();
        })).collect(CollectorUtil.toMap());
    }
}
