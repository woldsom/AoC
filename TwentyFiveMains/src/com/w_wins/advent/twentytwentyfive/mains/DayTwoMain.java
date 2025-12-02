package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daytwo.Range;
import com.w_wins.adventcommon.AdventTwoPartEvaluator;
import com.w_wins.iostream.Utf8ResourceLines;

import java.math.BigInteger;
import java.util.List;

public class DayTwoMain {
    static void main() {
        IO.println(new Utf8ResourceLines(Range.class, "/input.txt").evaluate(new AdventTwoPartEvaluator(lines->{
            final List<Range> ranges = Range.parse(lines);
            return ranges.stream().map(Range::sumDoubles).reduce(BigInteger.ZERO,BigInteger::add);
        }, lines -> {
            final List<Range> ranges = Range.parse(lines);
            return ranges.stream().map(Range::sumMultiples).reduce(BigInteger.ZERO,BigInteger::add);
        })));
    }
}
