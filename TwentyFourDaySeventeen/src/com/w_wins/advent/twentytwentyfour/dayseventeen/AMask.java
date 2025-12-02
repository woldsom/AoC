package com.w_wins.advent.twentytwentyfour.dayseventeen;

public record AMask(int bits) {
    public static AMask of(final int setBits) {
        return new AMask(setBits);
    }
}
