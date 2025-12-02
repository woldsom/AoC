package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daytwentyone.CacheKey;
import com.w_wins.advent.twentytwentyfour.daytwentyone.Pad;
import com.w_wins.iostream.Utf8ResourceLines;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class DayTwentyOneMain {
    public static final Pattern SPLITTER = Pattern.compile(Pattern.quote("A"));
    private final Map<CacheKey, BigInteger> memo = new HashMap<>();

    void main() {
        System.out.println(new Utf8ResourceLines(Pad.class, "/input.txt").<BigInteger>evaluate(lines -> lines.map(line -> {
            final Set<String> set = Pad.sequences(line, Pad.NUMERIC); // reuse previous implementation for numeric pad
            final BigInteger local = set.stream().map(firstStep -> new BigInteger(line.substring(0, 3)).multiply(shortestExpansion(25, firstStep))).min(BigInteger::compareTo).orElseThrow();
            System.err.println(line+": *"+local);
            return local;
        }).reduce(BigInteger::add).orElseThrow()));
    }

    private BigInteger shortestExpansion(final int depth, final String sequence) {
        final CacheKey key = new CacheKey(depth, sequence);
        final BigInteger cache = memo.get(key);
        if (cache == null) {
            final BigInteger returnValue;
            if (depth == 0) {
                returnValue = BigInteger.valueOf(sequence.length());
            } else if (sequence.length() > 6) {
                final List<String> list = Arrays.stream(SPLITTER.split(sequence)).map(s -> s + "A").toList();
                returnValue = list.stream().map(s -> shortestExpansion(depth, s)).reduce(BigInteger::add).orElseThrow();
            } else {
                final Set<String> subResult = Pad.sequences(sequence, Pad.DIRECTIONAL); // reuse previous implementation for short strings
                returnValue = subResult.stream().map(s -> shortestExpansion(depth - 1, s)).min(BigInteger::compareTo).orElseThrow();
            }
            memo.put(key, returnValue);
            return returnValue;
        } else {
            return cache;
        }
    }
}
