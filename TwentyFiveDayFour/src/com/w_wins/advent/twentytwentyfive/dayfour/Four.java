package com.w_wins.advent.twentytwentyfive.dayfour;

public record Four(String[] split) {
    public static Four parse(String line) {
        return new Four(line.split(","));
    }
}
