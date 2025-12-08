package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayeight.Coordinate;
import com.w_wins.advent.twentytwentyfive.dayeight.Nets;
import com.w_wins.common.CollectionUtil;
import com.w_wins.common.Streams;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DayEightMain {
    static void main() {
        final List<Coordinate> coordinates = new Utf8ResourceLines(Coordinate.class, "/input.txt").evaluate(lines -> lines.map(Coordinate::parse).toList());
        final Nets nets = Nets.withTarget(coordinates.size());
        IO.println("Part 2: " + Streams.presentLong(Streams.asMap(CollectionUtil.selfCross(coordinates, Coordinate::edge).collect(Collectors.toCollection(TreeSet::new)).stream().map(nets::extendWithAndPossiblyScore))
                .peek(e -> {
                    if (e.getKey() == 999) {
                        IO.println("Part 1: " + nets.netSizes().limit(3).mapToLong(x -> x).reduce(1, Math::multiplyExact));
                    }
                })
                .map(Map.Entry::getValue)).findFirst().orElseThrow());
    }

}
