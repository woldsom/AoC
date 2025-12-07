package com.w_wins.advent.twentytwentyfive.dayseven;

public enum GridElement {
    SPLITTER, FLOOR, BEGIN;

    public static GridElement parse(String s) {
        return switch (s) {
            case "^" -> SPLITTER;
            case "." -> FLOOR;
            case "S" -> BEGIN;
            default -> throw new IllegalArgumentException("Unknown grid string " + s);
        };
    }
}
