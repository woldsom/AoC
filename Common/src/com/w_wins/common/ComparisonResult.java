package com.w_wins.common;

public enum ComparisonResult {
    FIRST_LESS, SECOND_LESS, EQUAL, UNSURE;

    public int toInt(final int defaultValue) {
        return switch (this) {
            case FIRST_LESS ->
                    -1;
            case SECOND_LESS ->
                    1;
            case EQUAL ->
                    0;
            case UNSURE ->
                    defaultValue;
        };
    }
}
