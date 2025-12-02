package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daytwentytwo.PseudoRandomGenerator;
import com.w_wins.advent.twentytwentyfour.daytwentytwo.Sequence;
import com.w_wins.iostream.Utf8ResourceLines;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class DayTwentyTwoMain {
    void main() {
        final PseudoRandomGenerator generator = new PseudoRandomGenerator(0);
        final int[] input = new Utf8ResourceLines(PseudoRandomGenerator.class, "/input.txt").evaluate(lines -> lines.mapToInt(Integer::parseInt).toArray());
        System.out.println(Arrays.stream(input).map(PseudoRandomGenerator::nextTwoThousand).mapToObj(BigInteger::valueOf).reduce(BigInteger.ZERO, BigInteger::add));
        final Map<Sequence, Integer> best = Arrays.stream(input).mapToObj(PseudoRandomGenerator::firstOccurrencePrice).flatMap(map -> map.entrySet().stream()).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.reducing(0, Math::addExact))));
        final Sequence test = new Sequence(new int[]{-2, 1, -1, 3});
        //final Sequence test2 = new Sequence(new int[]{-1, -1, 0, 2});
        System.err.println(best.get(test));
        //System.err.println(PseudoRandomGenerator.firstOccurrencePrice(123).get(test2));

        final Map.Entry<Sequence, Integer> entry = best.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow();
        System.out.println(Arrays.toString(entry.getKey().values()) + ":" + entry.getValue());
    }
}
