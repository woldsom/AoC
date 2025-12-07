package com.w_wins.advent.twentytwentyfive.dayseven;

public enum Seven {
    WALL, FLOOR, PILL;

    public static Seven parse(String s) {
        return switch (s) {
            case "#" -> WALL;
            case "." -> FLOOR;
            case "*" -> PILL;
            default -> throw new IllegalArgumentException("Unknown grid string " + s);
        };
    }

    public String symbol() {
        return switch(this){
            case WALL->"X";
            case FLOOR -> "_";
            case PILL -> "O";
        };
    }
}
