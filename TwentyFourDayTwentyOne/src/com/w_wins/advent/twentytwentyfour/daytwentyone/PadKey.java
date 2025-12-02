package com.w_wins.advent.twentytwentyfour.daytwentyone;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.CollectorUtil;

import java.util.Arrays;
import java.util.stream.Stream;

public enum PadKey {
    LEFT(new Coordinate(-2, 1), "<", new Coordinate(-1, 0)), DOWN(new Coordinate(-1, 1), "v", new Coordinate(0, 1)), RIGHT(new Coordinate(0, 1), ">", new Coordinate(1, 0)), UP(new Coordinate(-1, 0), "^", new Coordinate(0, -1)), ACTIVATE(new Coordinate(0, 0), "A", new Coordinate(0, 0));


    private final Coordinate relativeToA;
    private final String letter;
    private final Coordinate movement;

    PadKey(final Coordinate setCoordinate, final String setLetter, final Coordinate setMovement) {
        relativeToA = setCoordinate;
        letter = setLetter;
        movement = setMovement;
    }

    public static Coordinate getMissing() {
        return new Coordinate(-2, 0);
    }

    public static Stream<PadKey> stream() {
        return Arrays.stream(values());
    }

    public static PadKey fromKeyboardPositionRelativeToA(final Coordinate relativeCoordinates) {
        return Arrays.stream(values()).filter(p -> relativeCoordinates.equals(p.movement())).collect(CollectorUtil.singleton());
    }

    public static PadKey fromMovement(final Coordinate movement) {
        return Arrays.stream(values()).filter(p -> movement.equals(p.movement())).collect(CollectorUtil.singleton());
    }

    public Coordinate relativeToA() {
        return relativeToA;
    }

    public String letter() {
        return letter;
    }

    public Coordinate movement() {
        return movement;
    }

    public static PadKey fromLetter(final String query) {
        return Arrays.stream(values()).filter(p -> query.equals(p.letter())).collect(CollectorUtil.singleton());
    }
}
