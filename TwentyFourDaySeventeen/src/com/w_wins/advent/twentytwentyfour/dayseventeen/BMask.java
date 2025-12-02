package com.w_wins.advent.twentytwentyfour.dayseventeen;

public record BMask(
        int shift,
        int xorMask) {
    public static BMask of(final int bits) {
        return new BMask(bits ^ 1, bits ^ 5);
    }
}
