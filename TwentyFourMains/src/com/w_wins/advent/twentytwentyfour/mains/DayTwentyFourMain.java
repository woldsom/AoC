package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daytwentyfour.BooleanOperator;
import com.w_wins.advent.twentytwentyfour.daytwentyfour.Equation;
import com.w_wins.advent.twentytwentyfour.daytwentyfour.TwentyFour;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Streams;
import com.w_wins.iostream.TwoLineGroupBuilder;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;

public final class DayTwentyFourMain {
    void main() {
        final TwoLineGroupBuilder<Map<String, Integer>, List<Equation>> input = new Utf8ResourceLines(TwentyFour.class, "/inputfixed.txt").evaluate(new TwoLineGroupBuilder<>(lines -> lines.map(line -> Streams.split(line, ": ").gather(Gatherers.windowSliding(2)).map(list -> Map.entry(list.getFirst(), Integer.parseInt(list.getLast()))).collect(CollectorUtil.singleton())).collect(CollectorUtil.toMap()), lines -> lines.map(line -> Streams.split(line, " -> ").gather(Gatherers.windowSliding(2)).map(list -> list.stream().flatMap(part -> Streams.split(part, " ")).toList()).map(parts -> new Equation(BooleanOperator.valueOf(parts.get(1)), parts.getFirst(), parts.get(2), parts.getLast())).collect(CollectorUtil.singleton())).toList()));
        System.out.println(Equation.evaluateSet(input.getFirstGroupResult(), input.getSecondGroupResult()));
        System.out.println(Equation.findSwaps(input.getSecondGroupResult()).stream().sorted().collect(Collectors.joining(",")));
    }

}
