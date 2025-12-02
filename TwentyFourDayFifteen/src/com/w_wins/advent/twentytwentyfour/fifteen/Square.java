package com.w_wins.advent.twentytwentyfour.fifteen;

public enum Square {
    WALL('#'), STONE('O'),STONE_HANGER('X'), FLOOR('.'), ROBOT('@');

    private final char character;

    Square(final char setCharacter) {
        character = setCharacter;
    }

    public static Square parse(String character) {
        return switch (character) {
            case "#" ->
                    WALL;
            case "." ->
                    FLOOR;
            case "@" ->
                    ROBOT;
            case "O" ->
                    STONE;
            default ->
                    throw new IllegalStateException("Can't parse " + character);
        };
    }

    public String character() {
        return character + "";
    }

    public boolean isStone() {
        return STONE.equals(this) || STONE_HANGER.equals(this);
    }

    public boolean isWall() {
        return WALL.equals(this);
    }
}
