package com.w_wins.numbers.primes;

import java.math.BigInteger;
import java.util.stream.Stream;

public interface PrimeGenerator extends PrimeChecker {
    Stream<BigInteger> primes();
}
