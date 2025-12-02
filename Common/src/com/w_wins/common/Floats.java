package com.w_wins.common;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public final class Floats {
    public static final double DELTA = 1e-5;
    public static final double SMALL = 1e52;

    public static int closeInt(final double value) {
        if (ceil(value - DELTA) == floor(value + DELTA) &&
                abs(value) < SMALL
        ) {
            return (int) floor(value + DELTA);
        } else {
            throw new ArithmeticException("Value is not close to being integer; "+value);
        }
    }
}
