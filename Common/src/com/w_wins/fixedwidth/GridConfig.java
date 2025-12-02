package com.w_wins.fixedwidth;

import java.util.EnumSet;
import java.util.Set;

public record GridConfig(int cellWidth, int cellHorizontalSeparator, int cellVerticalSeparator, int leftHeader, Set<HeaderType> headersPresent,
                         int rightHeader, int leftHeaderSeparator, int rightHeaderSeparator, int topHeaderSeparator, int bottomHeaderSeparator,
                         boolean emptyTrailing) {
    public static final GridConfig CHAR_GRID = new GridConfig(1, 0, 0, 0, EnumSet.noneOf(HeaderType.class), 0, 0, 0, 0, 0, false);

    public GridConfig {
        if (cellWidth < 0 || cellHorizontalSeparator < 0 || cellVerticalSeparator < 0 || leftHeader < 0 || rightHeader < 0 || leftHeaderSeparator < 0 || rightHeaderSeparator < 0 || topHeaderSeparator < 0 || bottomHeaderSeparator < 0) {
            throw new IllegalArgumentException("Config values cannot be negative");
        }
        if (emptyTrailing && headersPresent.contains(HeaderType.RIGHT)) {
            throw new IllegalArgumentException("Right header not compatible with empty trailing line data");
        }
        if(leftHeader>0 && !headersPresent.contains(HeaderType.LEFT) || leftHeader==0 && headersPresent.contains(HeaderType.LEFT)) {
            throw new IllegalArgumentException("Left header settings inconsistent");
        }
        if(rightHeader>0 && !headersPresent.contains(HeaderType.RIGHT) || rightHeader==0 && headersPresent.contains(HeaderType.RIGHT)) {
            throw new IllegalArgumentException("Right header settings inconsistent");
        }
    }
}
