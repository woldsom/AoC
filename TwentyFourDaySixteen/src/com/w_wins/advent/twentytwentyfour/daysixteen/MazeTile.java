package com.w_wins.advent.twentytwentyfour.daysixteen;

import com.w_wins.common.CollectorUtil;

import java.util.Arrays;

public enum MazeTile {
    FLOOR('.'), WALL('#'), START('S'), END('E');

    private final char character;

    MazeTile(final char setCharacter) {
        character = setCharacter;
    }

    public static MazeTile getTile(char characterToTest) {
        if(characterToTest==' ')return FLOOR;
        return Arrays.stream(values()).filter(e -> e.character == characterToTest).collect(CollectorUtil.singleton());
    }

    public char getCharacter() {
        return character;
    }

    public boolean walkable() {
        return !WALL.equals(this);
    }
}
