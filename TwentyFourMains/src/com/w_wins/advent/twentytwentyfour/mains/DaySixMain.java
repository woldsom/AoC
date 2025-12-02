package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daysix.MapElement;
import com.w_wins.advent.twentytwentyfour.daysix.PathAndResult;
import com.w_wins.advent.twentytwentyfour.daysix.PositionAndDirection;
import com.w_wins.adventcommon.Beam;
import com.w_wins.adventcommon.BeamGrid;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.CollectorUtil;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.GridParser;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.function.Predicate.not;

public final class DaySixMain {
    public static void main() {
        long startTime = System.nanoTime();
        final Grid<MapElement> map = new Utf8ResourceLines(MapElement.class, "/input.txt").evaluate(new GridParser<>(GridConfig.CHAR_GRID, MapElement::parse, null));
        final PositionAndDirection start = new PositionAndDirection(Coordinate.coordinatesOf(map).filter(coordinate -> map.get(coordinate.x(), coordinate.y()).equals(MapElement.START)).collect(CollectorUtil.singleton()),new Coordinate(0,-1));
        final PathAndResult initialResult = walk(map, null, start);
        System.out.println(initialResult.path().stream().map(PositionAndDirection::position).distinct().count());
        System.out.println(initialResult.path().stream().map(PositionAndDirection::position).distinct().parallel().filter(not(start.position()::equals)).map(obstruction -> walk(map, obstruction, start)).filter(PathAndResult::loops).count());
        System.err.println(System.nanoTime()-startTime);
        startTime = System.nanoTime();
        final PathAndResult beamResult = walkBeam(new BeamGrid<>(map, MapElement.OBSTRUCTION::equals, Optional::of),null,start);
        System.err.println(System.nanoTime()-startTime);
    }

    public static PathAndResult walk(final Grid<MapElement> map, final Coordinate newObstruction, final PositionAndDirection start) {
        final Set<PositionAndDirection> path = new HashSet<>();
        PositionAndDirection guard = start;
        while (Coordinate.boundsOf(map).test(guard.position())) {
            if (path.contains(guard)) {
                return new PathAndResult(path, true);
            } else {
                path.add(guard);
            }
            while (Coordinate.boundsOf(map).test(guard.next()) && (map.get(guard.next().x(), guard.next().y()).equals(MapElement.OBSTRUCTION) || guard.next().equals(newObstruction))) {
                guard = guard.turn();
            }
            guard = guard.walk();
        }
        return new PathAndResult(path, false);
    }
    public static PathAndResult walkBeam(final Grid<Set<Beam<MapElement>>> map, final Coordinate newObstruction, final PositionAndDirection start) {
        final Set<PositionAndDirection> path = new HashSet<>();
        PositionAndDirection guard = start;
        while (Coordinate.boundsOf(map).test(guard.position())) {
            if (path.contains(guard)) {
                return new PathAndResult(path, true);
            } else {
                path.add(guard);
            }
            while (Coordinate.boundsOf(map).test(guard.next()) && (map.get(guard.next().x(), guard.next().y()).equals(MapElement.OBSTRUCTION) || guard.next().equals(newObstruction))) {
                guard = guard.turn();
            }
            guard = guard.walk();
        }
        return new PathAndResult(path, false);
    }
}
