package com.w_wins.common;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public final class Unsupported {
    public static <T, R> Function<T, R> function() {
        return function("Function not supported");
    }

    public static <T, R> Function<T, R> function(final String message) {
        final UnsupportedOperationException cause = new UnsupportedOperationException(message);
        return a -> {
            throw new UnsupportedOperationException(message, cause);
        };
    }

    public static <R> BinaryOperator<R> binaryOperation() {
        return binaryOperation("Binary operation not supported");
    }

    public static <R> BinaryOperator<R> binaryOperation(final String message) {
        final UnsupportedOperationException cause = new UnsupportedOperationException(message);
        return (a, b) -> {
            throw new UnsupportedOperationException(message, cause);
        };
    }

    public static <T, U, R> BiFunction<T, U, R> biFunction() {
        return biFunction("Binary function not supported");
    }

    public static <T, U, R> BiFunction<T, U, R> biFunction(final String message) {
        final UnsupportedOperationException cause = new UnsupportedOperationException(message);
        return (t, u) -> {
            throw new UnsupportedOperationException(message, cause);
        };
    }
}
