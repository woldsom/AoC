package com.w_wins.common;

import java.util.OptionalInt;

public final class NumberBuilder {
    private final int radix;
    private OptionalInt progress = OptionalInt.empty();

    public NumberBuilder() {
        this(10);
    }

    public NumberBuilder(final int setRadix) {
        radix = setRadix;
    }

    public void addDigit(char d) {
        if (Character.isDigit(d)) {
            progress = OptionalInt.of(progress.orElse(0) * radix + Character.digit(d, radix));
        } else {
            throw new IllegalArgumentException("Expected digit");
        }
    }

    public OptionalInt getAndZero() {
        final OptionalInt returnValue = progress;
        progress = OptionalInt.empty();
        return returnValue;
    }
}
