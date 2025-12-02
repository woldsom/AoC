package com.w_wins.advent.twentytwentyfour.daysixteen;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.fixedwidth.Grid;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record PathElement(
        Coordinate fromWhere,
        Coordinate direction,
        Direction turn,
        int steps,
        boolean isStart,
        boolean isEnd) {
    public static final PathElement END = new PathElement(Coordinate.ORIGO, Coordinate.ORIGO, Direction.FORWARD, 0, false, true);

    public static final Coordinate EAST = new Coordinate(1, 0);

    public static PathElement getLeft(final Coordinate coordinate, final Coordinate oldDirection, final int length) {
        return new PathElement(coordinate, Direction.LEFT_TURN.turn(oldDirection), Direction.LEFT_TURN, length, false, false);
    }

    public static PathElement getStart(final Coordinate coordinate) {
        return new PathElement(coordinate, PathElement.EAST, Direction.FORWARD, 0, true, false);
    }

    public static PathElement getRight(final Coordinate coordinate, final Coordinate oldDirection, final int length) {
        return new PathElement(coordinate, Direction.RIGHT_TURN.turn(oldDirection), Direction.RIGHT_TURN, length, false, false);
    }

    private static Set<Integer> getBranches(final Coordinate where, final Coordinate direction, final Grid<MazeTile> grid) {
        final Set<Integer> returnValue = new HashSet<>();
        Coordinate current = where.plus(direction);
        int step = 1;
        final Predicate<Coordinate> bounds = Coordinate.boundsOf(grid);
        final Function<Coordinate, MazeTile> get = Coordinate.gridGetter(grid);
        while(bounds.test(current) && get.apply(current).walkable()) {
            if(get.apply(current.plus(direction.counterclockwise())).walkable() || get.apply(current.plus(direction.clockwise())).walkable()) {
                returnValue.add(step);
            }
            ++step;
            current.plus(direction);
        }
        return returnValue;
    }

    public long score() {
        return Math.addExact(turn().equals(Direction.FORWARD) ? 0 : 1000L, steps());
    }

    public Set<PathElement> nextPathsOn(final Grid<MazeTile> grid) {
        final Set<PathElement> returnValue = new HashSet<>();
        if (!isEnd()) {
            returnValue.addAll(getBranches(toWhere(), Direction.LEFT_TURN.turn(direction()), grid).stream().map(steps->getLeft(toWhere(),direction(), steps)).collect(Collectors.toSet()));
        }
        if (isStart()) {
            returnValue.addAll(getBranches(toWhere(), direction(), grid).stream().map(steps->new PathElement(toWhere(),direction(),Direction.FORWARD,steps,false,false)).collect(Collectors.toSet()));
        }
        if (!isEnd() && !isStart()) {
            returnValue.addAll(getBranches(toWhere(), Direction.RIGHT_TURN.turn(direction()), grid).stream().map(steps->getRight(toWhere(),direction(), steps)).collect(Collectors.toSet()));
        }
        return returnValue;
    }

    public long heuristicOn(final Grid<MazeTile> grid) {
        if (isEnd) {
            return 0L;
        }
        return toWhere().minus(new Coordinate(grid.getColumnCount() - 1, 1)).manhattanMagnitude();
    }

    private Coordinate toWhere() {
        return fromWhere().plus(direction().times(steps()));
    }
}
