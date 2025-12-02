package com.w_wins.advent.twentytwentyfour.daysix;

import com.w_wins.common.CollectorUtil;

import java.util.Arrays;

public enum MapElement {
    EMPTY("."), START("^"), OBSTRUCTION("#");

    private final String letter;

    MapElement(final String setLetter) {
        letter = setLetter;
    }

    public static MapElement parse(String s) {
        return Arrays.stream(MapElement.values()).filter(element -> element.letter.equals(s)).collect(CollectorUtil.singleton());
    }
}
