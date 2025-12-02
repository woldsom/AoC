package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daytwo.Range;
import com.w_wins.iostream.Utf8ResourceLines;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;

public class DayTwoMain {
    static void main() {
        final List<Range> ranges = new Utf8ResourceLines(Range.class, "/input.txt").evaluate(Range::parse);
        IO.println("Part 1: "+ sum(ranges, Range::sumDoubles) +", part 2: "+ sum(ranges, Range::sumMultiples));
    }

    private static BigInteger sum(final List<Range> ranges, final Function<Range, BigInteger> method) {
        return ranges.stream().map(method).reduce(BigInteger.ZERO, BigInteger::add);
    }
}
