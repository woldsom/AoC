package com.w_wins.common;

import java.util.OptionalLong;

public final class LongNumberBuilder {
    private final int radix;
    private OptionalLong progress = OptionalLong.empty();

    public LongNumberBuilder() {
        this(10);
    }

    public LongNumberBuilder(final int setRadix) {
        radix = setRadix;
    }

    public void addDigit(char d) {
        if (Character.isDigit(d)) {
            progress = OptionalLong.of(progress.orElse(0) * radix + Character.digit(d, radix));
        } else {
            throw new IllegalArgumentException("Expected digit");
        }
    }

    public OptionalLong getAndZero() {
        final OptionalLong returnValue = progress;
        progress = OptionalLong.empty();
        return returnValue;
    }
}
