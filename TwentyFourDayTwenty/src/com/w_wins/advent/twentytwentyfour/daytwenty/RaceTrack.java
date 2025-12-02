package com.w_wins.advent.twentytwentyfour.daytwenty;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.adventcommon.Grids;
import com.w_wins.common.CollectionUtil;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Streams;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.pathfinding.ScoredPath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

public record RaceTrack(
        Grid<String> grid) {
    public Set<Cheat> cheats() {
        final ScoredPath<Integer, Coordinate> normal = Grids.getShortestPath(grid()).orElseThrow();
        return Grids.findAll(grid(), "#").stream().map(removal -> {
            final ScoredPath<Integer, Coordinate> result = Grids.shortestPath(Grids.alter(grid(), removal.x(), removal.y(), ".")).orElseThrow();
            if (result.cost().equals(normal.cost())) {
                return Optional.<Cheat>empty();
            } else {
                final Coordinate cheatStart = result.path().stream().gather(Gatherers.windowSliding(2)).filter(list -> list.getLast().equals(removal)).map(List::getFirst).collect(CollectorUtil.singleton());
                final Coordinate cheatEnd = result.path().stream().gather(Gatherers.windowSliding(2)).filter(list -> list.getFirst().equals(removal)).map(List::getLast).collect(CollectorUtil.singleton());
                return Optional.of(new Cheat(cheatStart, cheatEnd, normal.cost() - result.cost()));
            }
        }).filter(Optional::isPresent).map(Optional::orElseThrow).collect(Collectors.toSet());
    }

    public int heavyCheats(final int boundary) {
        final Map<Coordinate, Integer> distanceToFinishFrom = new HashMap<>();
        final List<Coordinate> path = Grids.shortestPath(this.grid()).orElseThrow().path();
        Streams.asMap(path.stream()).forEach(e -> distanceToFinishFrom.put(e.getValue(), path.size() - 1 - e.getKey()));
        return IntStream.range(0, path.size() - 1).map(index -> {
            final Coordinate where = path.get(index);
            final Set<Coordinate> myNeighbours = path.stream().filter(n -> where.absDiff(n).manhattanMagnitude() <= 20).filter(not(where::equals)).collect(Collectors.toSet());
            final Set<Coordinate> myEndCoordinates = new HashSet<>(path.subList(index + 1, path.size()));
            final Set<Coordinate> afterMe = CollectionUtil.intersection(myNeighbours, myEndCoordinates);
            return Math.toIntExact(afterMe.stream().map(Functions.entry(candidate -> ((path.size() - 1 - index) - distanceToFinishFrom.get(candidate)) - where.absDiff(candidate).manhattanMagnitude())).filter(candidate -> candidate.getValue() >= boundary).map(Map.Entry::getKey).distinct().count());
        }).sum();
    }
}
