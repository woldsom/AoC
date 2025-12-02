package com.w_wins.advent.twentytwentyfour.daysixteen;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.AtomicBest;
import com.w_wins.common.Predicates;
import com.w_wins.fixedwidth.Grid;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record HorizontalVerticalPathElement(
        Coordinate where,
        boolean vertical,
        boolean isStart,
        boolean isEnd) {
    public long score(final HorizontalVerticalPathElement b) {
        final long cost = (isStart()&&horizontal()||b.isEnd() ? 0L : 1000L) + where().absDiff(b.where()).manhattanMagnitude();
        //System.err.println("Reported cost "+cost+" for "+this+" and "+b);
        return cost;
    }

    public boolean horizontal() {
        return !vertical();
    }

    public long heuristicOn(final Grid<MazeTile> grid) {
        final long value = isEnd() ? 0 : 1000L + (isStart() ? grid.getColumnCount() + grid.getRowCount() - 4 : where().minus(new Coordinate(grid.getColumnCount() - 1, 1)).manhattanMagnitude());
        return value;
    }

    public Set<HorizontalVerticalPathElement> nextPathsOn(final Grid<MazeTile> grid) {
        if (isStart()) {
            //System.err.println("Start");
            return Set.of(new HorizontalVerticalPathElement(where(), false, false, false), new HorizontalVerticalPathElement(where(), true, false, false));
        } else if (isEnd()) {
            //System.err.println("End");
            return Set.of(new HorizontalVerticalPathElement(new Coordinate(grid.getColumnCount() - 2, 1), true, false, true));
        } else {
            //System.err.println("Normal");
            return Stream.concat(iterateBranches(grid, true), iterateBranches(grid, false)).collect(Collectors.toSet());
        }
    }

    private Stream<HorizontalVerticalPathElement> iterateBranches(final Grid<MazeTile> grid, final boolean negate) {
        final Function<Coordinate, MazeTile> get = Coordinate.gridGetter(grid);
        final int count;
        if (horizontal() && negate) {
            count = where().x() - 1;
        } else if (horizontal()) {
            count = grid.getColumnCount() - 2 - where().x();
        } else if (negate) {
            count = where().y() - 1;
        } else {
            count = grid.getRowCount() - 2 - where().y();
        }
        //System.err.println(" count: "+count+ " on "+this+" "+negate);
        final Coordinate direction = positiveDirection().times(negate ? -1 : 1);
        final Coordinate end=new Coordinate(grid.getColumnCount()-2,1);
        return IntStream.rangeClosed(1, count).mapToObj(steps -> where().plus(direction.times(steps))).filter(Predicates.allUntil(c -> !get.apply(c).walkable())).filter(to -> get.apply(to.plus(direction.clockwise())).walkable() || get.apply(to.minus(direction.clockwise())).walkable()||end.equals(to)).map(to -> new HorizontalVerticalPathElement(to, !vertical(), false, MazeTile.END.equals(get.apply(to))));
    }

    private Coordinate positiveDirection() {
        return vertical() ? new Coordinate(0, 1) : new Coordinate(1, 0);
    }
}
