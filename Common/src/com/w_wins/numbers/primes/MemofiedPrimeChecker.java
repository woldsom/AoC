package com.w_wins.numbers.primes;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public final class MemofiedPrimeChecker implements PrimeChecker {
    private final PrimeChecker delegate;
    private final BitSet cached;
    private final BitSet prime;
    private final Map<BigInteger, Boolean> bigCache;

    public MemofiedPrimeChecker(final PrimeChecker setDelegate) {
        delegate = setDelegate;
        cached = new BitSet();
        prime = new BitSet();
        bigCache = new HashMap<>();
    }

    @Override
    public boolean isPrime(final BigInteger possiblePrime) {
        try {
            final int index = possiblePrime.intValueExact();
            if (cached.get(index)) {
                return prime.get(index);
            }
            final boolean result = delegate.isPrime(possiblePrime);
            prime.set(index, result);
            cached.set(index);
            return result;
        } catch (final ArithmeticException ae) {
            final Boolean isPrime = bigCache.get(possiblePrime);
            if (isPrime != null) {
                return isPrime;
            }
            final boolean result = delegate.isPrime(possiblePrime);
            bigCache.put(possiblePrime, result);
            return result;
        }
    }
}
