package com.w_wins.advent.twentytwentyfive.dayone;

public record Instruction() {
    public static int asInt(String s) {
        final int abs=Integer.parseInt(s.substring(1));
        if(s.charAt(0)=='R'){
            return abs;
        } else {
            return -abs;
        }
    }
}
