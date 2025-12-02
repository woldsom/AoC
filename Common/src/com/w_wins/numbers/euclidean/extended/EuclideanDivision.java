package com.w_wins.numbers.euclidean.extended;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public interface EuclideanDivision {

    static <V> BiFunction<V,V,DivisionResult<V>> fromOperators(final BinaryOperator<V> division, final BinaryOperator<V> remainder) {
        return (numerator, denominator) -> new DivisionResult<>(division.apply(numerator, denominator), remainder.apply(numerator, denominator));
    }

    static <V> BiFunction<V,V,DivisionResult<V>> usingArray(final BiFunction<V, V, V[]> arrayOperator) {
        return (numerator, denominator) -> {
            final V[] array = arrayOperator.apply(numerator, denominator);
            return new DivisionResult<>(array[0], array[1]);
        };
    }
}
