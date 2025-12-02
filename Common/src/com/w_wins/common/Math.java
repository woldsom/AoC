package com.w_wins.common;

import java.math.BigInteger;
import java.util.stream.IntStream;

public final class Math {
    public static boolean betweenInclusive(long value, long lowerInclusive, long upperInclusive) {
        return value >= lowerInclusive && value <= upperInclusive;
    }

    public static int modulusFull(int value, int modulo) {
        return (value % modulo + modulo) % modulo;
    }

    public static long modulusFull(long value, long modulo) {
        return (value % modulo + modulo) % modulo;
    }

    public static int[] divMod(int value, int divisor) {
        final int fullMod = modulusFull(value, divisor);
        return new int[]{(value - fullMod) / divisor, fullMod};
    }

    public static int sgn(final int compareOutcome) {
        if (compareOutcome == 0) {
            return 0;
        } else if (compareOutcome < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    public static BigInteger lcm(final BigInteger a, final BigInteger b) {
        return a.multiply(b).divide(a.gcd(b));
    }

    public static int absoluteDifference(int a, int b) {
        return java.lang.Math.absExact(java.lang.Math.subtractExact(a, b));
    }

    public static long pow(int a, int b) {
        if (b == 0) {
            return 1;
        } else {
            return IntStream.range(0, b).mapToLong(_ -> a).reduce(java.lang.Math::multiplyExact).orElseThrow();
        }
    }

    public static long sgn(final long x) {
        if (x == 0) {
            return 0;
        } else if (x < 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
