package com.w_wins.advent.twentytwentyfive.dayfour;

public enum WarehousePosition {
    EMPTY,ROLL,MOVABLE;
    public static WarehousePosition parse(String character) {
        if(character.equals("@")){
            return ROLL;
        }
        return EMPTY;
    }
}
