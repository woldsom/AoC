package com.w_wins.advent.twentytwentyfour.daytwelve;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.adventcommon.Grids;
import com.w_wins.adventcommon.MapGrid;
import com.w_wins.adventcommon.Quad;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Predicates;
import com.w_wins.fixedwidth.Grid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

public record Plots(
        Grid<Region> grid) {
    public static final Region OUTSIDE_REGION = new Region(".", Set.of());// 0 area solves all out-of-grid excursions

    public static Plots fromCharGrid(final Grid<String> grid) {
        return new Plots(Coordinate.coordinatesOf(grid).collect(Collectors.toCollection(HashSet::new)).stream().gather(Gatherer.<Coordinate, Map<Coordinate, Region>, MapGrid<Region>>of(HashMap::new, (state, element, _) -> {
            if (!state.containsKey(element)) {
                final Region region = new Region(grid.get(element.x(), element.y()), Grids.floodFill(grid, element, String::equals));
                region.area().forEach(c -> {
                    state.put(c, region);
                });
            }
            return true;
        }, Functions.createAndModifySame(HashMap::new, Map::putAll), (map2, downstream) -> downstream.push(new MapGrid<>(map2, grid.getColumnCount(), grid.getRowCount(),null)))).collect(CollectorUtil.singleton()));
    }

    public long noDiscount() { // Iterate quad for convenience, then only consider top and left walls, to not double-count.
        return Grids.stream(Quad.invertGrid(grid, quad -> {
            long sum = 0;
            if (!quad.upperLeft().equals(quad.upperRight())) {
                sum += quad.upperLeft().area().size() + quad.upperRight().area().size();
            }
            if (!quad.upperLeft().equals(quad.lowerLeft())) {
                sum += quad.upperLeft().area().size() + quad.lowerLeft().area().size();
            }
            return sum;
        }, _ -> OUTSIDE_REGION)).mapToLong(x -> x).sum();
    }

    public long discount() { // Counting fence corners instead of lengths
        return Grids.stream(Quad.invertGrid(grid, quad -> {
            final Map<Region, Long> counts = quad.stream().collect(CollectorUtil.frequency());
            final ToLongFunction<Stream<Region>> sumAreas = regionsWithCorners -> regionsWithCorners.map(Region::area).mapToLong(Set::size).sum();
            // In the examples in these comments, the different letters indicate *regions*, which might not necessarily be different letters in the input, if they are otherwise disconnected. E.g. the center quad of the last example in the problem would be:
            // AB
            // CA
            // Where B and C are different regions even though they share the input letter B!
            if (counts.size() < 2) { // no fences, no corners
                // AA
                // AA
                return 0L;
            } else if (counts.size() == 2) {
                if (quad.kittyCorner()) { // given standard topology, two regions cannot cross each other, so there's a corner each
                    // AA (not possible: AB
                    // AB                BA)
                    return sumAreas.applyAsLong(counts.keySet().stream());
                } else { // Straight fence between two regions
                    // AA
                    // BB
                    return 0L;
                }
            } else {
                if (quad.kittyCorner()) { // One region kitty-corner, just count each quad member as the kitty-corner region is then counted twice.
                    // A B
                    // C A
                    return sumAreas.applyAsLong(quad.stream());
                } else { // since regions that now are present more than once has no corners, filter on that and count every other region
                    // AA AB
                    // BC CD
                    return sumAreas.applyAsLong(counts.entrySet().stream().filter(Predicates.testing(Map.Entry::getValue, frequency -> frequency < 2)).map(Map.Entry::getKey));
                }
            }
        }, _ -> OUTSIDE_REGION)).mapToLong(x -> x).sum();
    }
}
