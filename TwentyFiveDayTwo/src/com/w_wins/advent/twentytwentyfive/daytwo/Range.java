package com.w_wins.advent.twentytwentyfive.daytwo;

import com.w_wins.numbers.primes.EratosthenesSieve;
import com.w_wins.numbers.primes.IteratingFactorizer;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public record Range(BigInteger start, BigInteger end) {

    private static final IteratingFactorizer FACTORIZER = new IteratingFactorizer(new EratosthenesSieve(10));

    public static List<Range> parse(final Stream<String> lines) {
        return lines.flatMap(line -> Arrays.stream(line.split(Pattern.quote(",")))).map(Range::parse).toList();
    }

    public static Range parse(String entry) {
        final String[] parts = entry.split(Pattern.quote("-"));
        return new Range(new BigInteger(parts[0]), new BigInteger(parts[1]));
    }

    public static boolean isMultiple(BigInteger a) {
        final String string = a.toString(10);
        return isMultiple(string);
    }

    public static boolean isMultiple(final String string) {
        if (!string.substring(1).contains(string.substring(0, 1))) {
            return false;
        }
        final Set<Long> factors = new HashSet<>(FACTORIZER.factor(string.length()));
        if (factors.contains((long) string.length())) {
            factors.clear();
        }
        if (factors.equals(Set.of(2L))) {
            if (string.length() > 4) {
                factors.add(4L);
            }
        }
        factors.add(1L);
        return factors.stream().mapToInt(Math::toIntExact).anyMatch(factor -> {
            final String part = string.substring(0, factor);
            return string.chars().mapToObj(Character::toString).gather(Gatherers.windowFixed(factor)).map(subString -> String.join("", subString)).allMatch(part::equals);
        });
    }

    private static boolean isDoubled(final BigInteger a) {
        final String string = a.toString(10);
        if (string.length() % 2 == 1) {
            return false;
        }
        return string.startsWith(string.substring(string.length() / 2));
    }

    public BigInteger sumDoubles() {
        return sumWith(Range::isDoubled);
    }

    public BigInteger sumMultiples() {
        return sumWith(Range::isMultiple);
    }

    private BigInteger sumWith(final Predicate<BigInteger> isInvalid) {
        BigInteger sum = BigInteger.ZERO;
        for (BigInteger index = start; index.compareTo(end) <= 0; index = index.add(BigInteger.ONE)) {
            if (isInvalid.test(index)) {
                sum = sum.add(index);
            }
        }
        return sum;
    }
}
