package com.w_wins.numbers.primes;

import java.math.BigInteger;

public final class PrimeCheckerSimple implements PrimeChecker {
    @Override
    public boolean isPrime(final BigInteger possiblePrime) {
        if(!possiblePrime.isProbablePrime(4)) {
            return false;
        }
        BigInteger factorCheck = BigInteger.valueOf(3);
        final BigInteger sqrt = possiblePrime.sqrt();
        while(factorCheck.compareTo(sqrt)<=0) {
            if(possiblePrime.remainder(factorCheck).equals(BigInteger.ZERO)) {
                return false;
            }
            factorCheck=factorCheck.add(BigInteger.TWO);
        }
        return true;
    }
}
