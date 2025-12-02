package com.w_wins.numbers.primes;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

public final class IteratingFactorizer {
    private final PrimeGenerator generator;

    public IteratingFactorizer(final PrimeGenerator setGenerator) {
        generator = setGenerator;
    }

    public List<Long> factor(long composite) {
        if (generator.isPrime(BigInteger.valueOf(composite))) {
            return List.of(composite);
        } else {
            final AtomicLong remainder = new AtomicLong(composite);
            final List<Long> returnValue = new ArrayList<>();
            generator.primes().limit((long) Math.sqrt(composite)).forEach(candidate -> {
                while (remainder.get() > 1 && remainder.get() % candidate.longValueExact() == 0) {
                    remainder.set(remainder.get() / candidate.longValueExact());
                    returnValue.add(candidate.longValueExact());
                }
            });
            if (remainder.get() > 1) {
                returnValue.add(remainder.get());
            }
            return Collections.unmodifiableList(returnValue);
        }
    }

    public Set<Long> factorPower(long composite) {
        if (generator.isPrime(BigInteger.valueOf(composite))) {
            return Set.of(composite);
        } else {
            final AtomicLong remainder = new AtomicLong(composite);
            final Set<Long> returnValue = new TreeSet<>();
            generator.primes().limit((long) Math.sqrt(composite)).mapToLong(BigInteger::longValueExact).forEach(candidate -> {
                if (remainder.get() > 1 && remainder.get() > candidate && remainder.get() % candidate == 0) {
                    long factor = 1;
                    while (remainder.get() % candidate == 0) {
                        remainder.set(remainder.get() / candidate);
                        factor = Math.multiplyExact(factor, candidate);
                    }
                    returnValue.add(factor);
                }
            });
            if (remainder.get() > 1) {
                returnValue.add(remainder.get());
            }
            return Collections.unmodifiableSet(returnValue);
        }
    }
}
