package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayfive.CountAndRange;
import com.w_wins.advent.twentytwentyfive.dayfive.Range;
import com.w_wins.iostream.TwoLineGroupBuilder;
import com.w_wins.iostream.Utf8ResourceLines;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DayFiveMain {
    static void main() {
        final TwoLineGroupBuilder<Set<Range>, List<BigInteger>> input = new Utf8ResourceLines(Range.class, "/input.txt").evaluate(new TwoLineGroupBuilder<>(lines -> lines.map(Range::parse).collect(Collectors.toCollection(TreeSet::new)), lines -> lines.map(BigInteger::new).toList()));
        final Set<Range> ranges = input.getFirstGroupResult();
        final List<BigInteger> ingredients = input.getSecondGroupResult();
        IO.println(ingredients.stream().filter(ingredient -> ranges.stream().anyMatch(range -> range.contains(ingredient))).count());
        final CountAndRange combination = ranges.stream().map(CountAndRange::fromRange).reduce(CountAndRange::combine).orElseThrow();
        IO.println(combination.total());
    }
}
