package com.w_wins.advent.twentytwentyfour.daytwentyfour;

import com.w_wins.common.Strings;

public record BitOperand(
        boolean x,
        int bit) {
    public String pretty() {
        return (x ? "x" : "y") + Strings.padLeft("" + bit(), "0", 2);
    }

    public String key() {
        return pretty();
    }
}
