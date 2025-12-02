package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daytwelve.Plots;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.SimpleGridParser;
import com.w_wins.iostream.Utf8ResourceLines;

public final class DayTwelveMain {
    /*
        private static void first(final Grid<String> grid) {
            final Function<Coordinate, String> gridGetter = Coordinate.gridGetter(grid);
            final Map<Coordinate, BadOldContainer> map = Coordinate.coordinatesOf(grid).map(Functions.entry(coordinate -> {
                final String letter = gridGetter.apply(coordinate);
                return new BadOldContainer(coordinate, letter, new AtomicReference<>(RegionOld.simple(coordinate, letter)));
            })).collect(CollectorUtil.toMap());
            final Set<RegionOld> regions = new TreeSet<>(Comparator.comparing(RegionOld::what).thenComparing(region -> region.area().stream().findAny().orElse(Coordinate.ORIGO)));
            regions.addAll(RegionOld.coalesce(map));
            System.out.println(regions.stream().mapToLong(RegionOld::price).sum());
            System.out.println(regions.stream().mapToLong(RegionOld::discountPrice).sum());
        }
    */
    void main() {
        final Plots plots = Plots.fromCharGrid(new Utf8ResourceLines(Plots.class, "/input.txt").evaluate(new SimpleGridParser(GridConfig.CHAR_GRID)));
        System.out.println("Part 1: " + plots.noDiscount());
        System.out.println("Part 2: " + plots.discount());
    }

}
