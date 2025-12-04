package com.w_wins.advent.twentytwentyfive.dayfour;

public enum WarehousePosition {
    EMPTY,ROLL;
    public static WarehousePosition parse(String character) {
        if(character.equals("@")){
            return ROLL;
        }
        return EMPTY;
    }
}
