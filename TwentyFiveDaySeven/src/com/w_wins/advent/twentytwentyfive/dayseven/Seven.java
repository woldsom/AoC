package com.w_wins.advent.twentytwentyfive.dayseven;

public enum Seven {
    SPLITTER, FLOOR, BEGIN;

    public static Seven parse(String s) {
        return switch (s) {
            case "^" -> SPLITTER;
            case "." -> FLOOR;
            case "S" -> BEGIN;
            default -> throw new IllegalArgumentException("Unknown grid string " + s);
        };
    }
}
