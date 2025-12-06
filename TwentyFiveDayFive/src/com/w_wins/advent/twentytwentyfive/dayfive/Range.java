package com.w_wins.advent.twentytwentyfive.dayfive;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.regex.Pattern;

public record Range(BigInteger startInclusive, BigInteger endInclusive) implements Comparable<Range>{

    public static final Comparator<Range> COMPARATOR = Comparator.comparing(Range::startInclusive).thenComparing(Comparator.comparing(Range::endInclusive));

    public static Range parse(String line) {
        final String[] parts = line.split(Pattern.quote("-"), 2);
        return new Range(new BigInteger(parts[0]), new BigInteger(parts[1]));
    }

    public boolean contains(final BigInteger ingredient) {
        return startInclusive().compareTo(ingredient) <= 0 && ingredient.compareTo(endInclusive()) <= 0;
    }

    @Override
    public int compareTo(final Range o) {
        return COMPARATOR.compare(this,o);
    }

    public BigInteger count() {
        return BigInteger.ONE.add(endInclusive()).subtract(startInclusive());
    }
}
