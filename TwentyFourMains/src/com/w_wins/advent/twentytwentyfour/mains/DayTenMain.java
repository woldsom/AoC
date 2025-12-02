package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.dayten.Topography;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.GridParser;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class DayTenMain {
    public static void main() {
        final long start = System.nanoTime();
        final Topography map = new Topography(new Utf8ResourceLines(Topography.class, "/input.txt").evaluate(new GridParser<>(GridConfig.CHAR_GRID, string -> ".".equals(string) ? -1 : Integer.parseInt(string), null)));
        final Set<List<Coordinate>> paths = map.paths();
        System.out.println(paths.stream().map(list -> List.of(list.getFirst(), list.getLast())).distinct().count());
        System.out.println(paths.size());
        System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-start));
    }
}
