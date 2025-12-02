package com.w_wins.numbers.enumerating;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public record CantorPair(long x, long y) implements Comparable<CantorPair> {
    public static final Comparator<CantorPair> COMPARATOR = Comparator.<CantorPair>comparingLong(pair -> pair.x() + pair.y()).thenComparingLong(CantorPair::y);

    public CantorPair {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Cantor pair is of non-negative numbers, but got " + x + ", " + y);
        }
        if (x > Long.MAX_VALUE / 4 || y > Long.MAX_VALUE / 4) {
            throw new IllegalArgumentException("Too large values, " + x + ", " + y);
        }
    }

    public static CantorPair fromPi(final long pi) {
        final BigInteger w = BigInteger.valueOf(pi).multiply(BigInteger.valueOf(8)).add(BigInteger.ONE).sqrt().subtract(BigInteger.ONE).divide(BigInteger.TWO);
        final long t = w.pow(2).add(w).divide(BigInteger.TWO).longValueExact();
        final long y = pi - t;
        final long x = w.subtract(BigInteger.valueOf(y)).longValueExact();
        return new CantorPair(x, y);
    }

    public static Stream<CantorPair> stream() {
        return StreamSupport.stream(new CantorPairSpliterator(),true);
    }

    public long pi() {
        if ((x + y) % 2 == 1) {
            return Math.addExact(Math.multiplyExact(x + y, (x + y + 1) / 2), y);
        } else {
            return Math.addExact(Math.multiplyExact((x + y) / 2, x + y + 1), y);
        }
    }

    @Override
    public int compareTo(final CantorPair other) {
        return COMPARATOR.compare(this, other);
    }
}
