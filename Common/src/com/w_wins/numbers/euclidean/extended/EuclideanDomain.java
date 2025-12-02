package com.w_wins.numbers.euclidean.extended;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public record EuclideanDomain<T>(BiFunction<T, T, DivisionResult<T>> division, BinaryOperator<T> multiplication, BinaryOperator<T> subtraction, T multiplicativeIdentity, T additiveIdentity) {

    public ExtendedEuclideanResult<T> extendedEuclidean(T a, T b) {
        final List<T> r = new ArrayList<>(List.of(a, b));
        final List<T> af = new ArrayList<>(List.of(multiplicativeIdentity, additiveIdentity));
        final List<T> bf = new ArrayList<>(List.of(additiveIdentity, multiplicativeIdentity));
        while (!r.getLast().equals(additiveIdentity)) {
            final DivisionResult<T> result = division.apply(r.get(r.size() - 2), r.get(r.size() - 1));
            r.add(result.remainder());
            af.add(subtraction.apply(af.get(af.size() - 2), multiplication.apply(af.get(af.size() - 1), result.quotient())));
            bf.add(subtraction.apply(bf.get(bf.size() - 2), multiplication.apply(bf.get(bf.size() - 1), result.quotient())));
        }
        return new ExtendedEuclideanResult<>(r.get(r.size() - 2), af.get(af.size() - 2), bf.get(bf.size() - 2), bf.get(bf.size() - 1), af.get(af.size() - 1));
    }

    public static EuclideanDomain<BigInteger> withBigInteger() {
        return new EuclideanDomain<>(EuclideanDivision.usingArray(BigInteger::divideAndRemainder), BigInteger::multiply, BigInteger::subtract, BigInteger.ONE, BigInteger.ZERO);
    }

    public static EuclideanDomain<Long> withLong() {
        return new EuclideanDomain<>(EuclideanDivision.fromOperators(Math::divideExact, (a, b) -> a % b), Math::multiplyExact, Math::subtractExact, 1L, 0L);
    }

    public static EuclideanDomain<Integer> withInteger() {
        return new EuclideanDomain<>(EuclideanDivision.fromOperators((a, b) -> Math.toIntExact(a.longValue() / b), (a, b) -> a % b), Math::multiplyExact, Math::subtractExact, 1, 0);
    }
}
