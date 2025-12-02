package com.w_wins.numbers.pythagoras;

import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.w_wins.common.Functions;
import com.w_wins.numbers.enumerating.CantorPair;
import com.w_wins.numbers.enumerating.CoprimePair;

public record PythagoreanTriple(long shortestSide, long longestSide, long hypotenuse) {
    public PythagoreanTriple {
        if (shortestSide < 3 || longestSide < 4 || hypotenuse < 5) {
            throw new IllegalArgumentException("Values too small: " + shortestSide + ", " + longestSide + ", " + hypotenuse);
        }
        if (shortestSide >= longestSide || longestSide >= hypotenuse) {
            throw new IllegalArgumentException("Values not in order: " + shortestSide + ", " + longestSide + ", " + hypotenuse);
        }
        if (Math.addExact(Math.multiplyExact(shortestSide, shortestSide), Math.multiplyExact(longestSide, longestSide)) != Math.multiplyExact(hypotenuse, hypotenuse)) {
            throw new IllegalArgumentException("Not a pythagorean triple: " + shortestSide + ", " + longestSide + ", " + hypotenuse);
        }
    }

    public static Stream<PythagoreanTriple> all() {
        return Stream.concat(Stream.of(new PythagoreanTriple(3, 4, 5)), CantorPair.stream().map(pair -> {
            try {
                final long n = pair.x() + 1;
                final long m = pair.y() + n + 1;
                final long n2 = n * n;
                final long m2 = m * m;
                final int a = Math.toIntExact(m2 - n2);
                final int b = Math.toIntExact(2 * m * n);
                final int c = Math.toIntExact(m2 + n2);
                if (a > b) {
                    return new PythagoreanTriple(b, a, c);
                } else {
                    return new PythagoreanTriple(a, b, c);
                }
            } catch (final ArithmeticException ae) {
                return new PythagoreanTriple(3, 4, 5);
            }
        }).filter(p -> p.hypotenuse() != 5));
    }

    public static Stream<PythagoreanTriple> all2() {
        return CantorPair.stream().mapMulti((p, consumer) -> {
            try {
                consumer.accept(euclid(CoprimePair.fromLong(p.x() + 1), p.y() + 1));
            } catch (final ArithmeticException ae) {
                //System.err.println(".");
            }
        });
    }

    public static Stream<PythagoreanTriple> base() {
        return LongStream.rangeClosed(1,Long.MAX_VALUE).mapToObj(CoprimePair::fromLong).map(Functions.bindRight(PythagoreanTriple::euclid,1L));
    }

    private static PythagoreanTriple euclid(final CoprimePair coprimes, final long scalar) {
        final long n2 = Math.multiplyExact(coprimes.smaller(), coprimes.smaller());
        final long m2 = Math.multiplyExact(coprimes.larger(), coprimes.larger());
        final long a = Math.multiplyExact(m2 - n2, scalar);
        final long b = Math.multiplyExact(Math.multiplyExact(2, scalar), Math.multiplyExact(coprimes.larger(), coprimes.smaller()));
        final long c = Math.multiplyExact(Math.addExact(m2, n2), scalar);
        if (a > b) {
            return new PythagoreanTriple(b, a, c);
        } else {
            return new PythagoreanTriple(a, b, c);
        }
    }

    public PythagoreanTriple t1() {
        return new PythagoreanTriple(shortestSide() - longestSide() * 2 + hypotenuse() * 2, shortestSide() * 2 - longestSide() + hypotenuse() * 2, shortestSide() * 2 - longestSide() * 2 + hypotenuse() * 3);
    }

    public PythagoreanTriple t2() {
        return new PythagoreanTriple(shortestSide() * 2 + longestSide() + hypotenuse() * 2, shortestSide() + longestSide() * 2 + hypotenuse() * 2, shortestSide() * 2 + longestSide() * 2 + hypotenuse() * 3);
    }

    public PythagoreanTriple t3() {
        return new PythagoreanTriple(-shortestSide() * 2 + longestSide() + hypotenuse() * 2, -shortestSide() + longestSide() * 2 + hypotenuse() * 2, -shortestSide() * 2 + longestSide() * 2 + hypotenuse() * 3);
    }
}
