package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.dayeighteen.Eighteen;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Predicates;
import com.w_wins.common.Streams;
import com.w_wins.iostream.Utf8ResourceLines;
import com.w_wins.pathfinding.AStar;
import com.w_wins.pathfinding.ScoredPath;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;

public final class DayEighteenMain {

    public static final Coordinate GOAL = new Coordinate(70, 70);

    void main() {
        final List<Coordinate> bytes = new Utf8ResourceLines(Eighteen.class, "/input.txt").evaluate(lines -> lines.flatMap(line -> Streams.split(line, ","))
                .map(Integer::parseInt)
                .gather(Gatherers.windowFixed(2))
                .map(list -> new Coordinate(list.getFirst(), list.getLast())).toList());
        final Set<Coordinate> kilobyte = bytes.stream().limit(1024).collect(Collectors.toSet());
        final Predicate<Coordinate> bounds = c -> c.x() >= 0 && c.y() >= 0 && c.x() <= 70 && c.y() <= 70;
        final AStar<Integer, Coordinate> astar = new AStar<>(Math::addExact, Integer::compareTo, () -> 0);
        final Predicate<Coordinate> filter = c -> !kilobyte.contains(c) || GOAL.equals(c);
        final Optional<ScoredPath<Integer, Coordinate>> result = astar.shortest(Coordinate.ORIGO, GOAL, c -> c.manhattanNeighbours(bounds).filter(filter).collect(Collectors.toSet()), (a, b) -> a.absDiff(b).manhattanMagnitude(), c -> c.absDiff(GOAL).manhattanMagnitude());
        System.out.println(result.orElseThrow().cost());
        System.out.println(bytes.stream().skip(1024).filter(Predicates.pickOne(nextByte->{
            kilobyte.add(nextByte);
            return astar.shortest(Coordinate.ORIGO, GOAL, c -> c.manhattanNeighbours(bounds).filter(filter).collect(Collectors.toSet()), (a, b) -> a.absDiff(b).manhattanMagnitude(), c -> c.absDiff(GOAL).manhattanMagnitude()).isEmpty();
        })).collect(CollectorUtil.singleton()));
    }
}
