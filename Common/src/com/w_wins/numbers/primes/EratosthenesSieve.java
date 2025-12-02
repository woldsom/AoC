package com.w_wins.numbers.primes;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.w_wins.common.Predicates.not;

public final class EratosthenesSieve implements PrimeGenerator {
    private static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);
    private final BitSet composite;

    public EratosthenesSieve() {
        this(Integer.MAX_VALUE);
    }

    public EratosthenesSieve(final int max) {
        composite = new BitSet();
        IntStream.rangeClosed(2, (int) Math.sqrt(max)).forEach(index -> {
            if (!composite.get(index)) {
                IntStream.iterate(index * index, x -> x <= max , x -> x + index).forEach(composite::set);
            }
        });
        System.err.println("Iterating to "+((int) Math.sqrt(max)));
    }

    @Override
    public boolean isPrime(final BigInteger possiblePrime) {
        if (possiblePrime.compareTo(MAX_INT) <= 0) {
            return !composite.get(possiblePrime.intValueExact());
        } else {
            throw new UnsupportedOperationException("Not implemented that high yet");
        }
    }

    @Override
    public Stream<BigInteger> primes() {
        return Stream.concat(IntStream.range(2, Integer.MAX_VALUE).filter(not(composite::get)).mapToObj(BigInteger::valueOf), Stream.generate(() -> {
            throw new UnsupportedOperationException("End of integers, primes not supported past");
        }));
    }

    public IntStream primesUpTo(final int limitInclusive) {
        return IntStream.rangeClosed(2, limitInclusive).filter(not(composite::get));
    }
}
