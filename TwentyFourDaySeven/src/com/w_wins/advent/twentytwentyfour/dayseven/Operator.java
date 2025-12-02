package com.w_wins.advent.twentytwentyfour.dayseven;

import java.util.function.BinaryOperator;

public enum Operator implements BinaryOperator<Long> {
    PLUS, TIMES, PIPE;


    @Override
    public Long apply(final Long a, final Long b) {
        return switch (this) {
            case PLUS ->
                    Math.addExact(a , b);
            case TIMES ->
                    Math.multiplyExact(a,b);
            case PIPE ->
                    Long.parseLong(a+""+b);
        };
    }
}
