package com.w_wins.symbolic;

import java.util.Set;
import java.util.function.LongBinaryOperator;

public class Symbol {
    public static LongBinaryOperator asLongOperator(final String symbol) {
        return switch (symbol) {
            case "*" -> Math::multiplyExact;
            case "+" -> Math::addExact;
            case "/" -> Math::divideExact;
            case "-" -> Math::subtractExact;
            default -> throw new IllegalArgumentException("Unrecognized symbol " + symbol);
        };
    }

    public static boolean isOperator(final String symbol) {
        return Set.of("*", "+", "-", "/").contains(symbol);
    }
}
