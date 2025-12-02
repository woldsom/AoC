package com.w_wins.advent.twentytwentyfour.daytwentyfour;

import com.w_wins.common.Either;

import java.util.function.BiPredicate;

public enum BooleanOperator {
    OR((a, b) -> a | b), AND((a, b) -> a & b), XOR((a, b) -> a ^ b);

    private final BiPredicate<Boolean, Boolean> operation;

    BooleanOperator(BiPredicate<Boolean, Boolean> setOperation) {
        operation = setOperation;
    }

    public boolean evaluate(final boolean first, final boolean second) {
        return operation.test(first, second);
    }

    public String pretty() {
        return switch(this){
            case OR ->
                    "|";
            case AND ->
                    "&";
            case XOR ->
                    "^";
        };
    }
}
