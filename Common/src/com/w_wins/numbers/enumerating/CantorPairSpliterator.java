package com.w_wins.numbers.enumerating;

import java.util.Spliterator;
import java.util.function.Consumer;

public final class CantorPairSpliterator implements Spliterator<CantorPair> {
    private final long finalSum;
    private long x;
    private long y;

    public CantorPairSpliterator() {
        this(0, 0, Long.MAX_VALUE);
    }

    public CantorPairSpliterator(final long initialX, final long initialY, final long setFinalSum) {
        x = initialX;
        y = initialY;
        finalSum = setFinalSum;
        if (x < 0 || y < 0 || x + y > finalSum) {
            throw new IllegalArgumentException("Invalid arguments to CantorPairSpliterator(" + initialX + ", " + initialY + ", " + setFinalSum + ")");
        }
    }

    @Override
    public boolean tryAdvance(final Consumer<? super CantorPair> action) {
        if (y == Long.MAX_VALUE && x == Long.MAX_VALUE) {
            return false;
        }
        action.accept(new CantorPair(x, y));
        advance();
        return true;
    }

    private void advance() {
        if (x > 0) {
            --x;
            ++y;
        } else if (x + y == finalSum) {
            x = Long.MAX_VALUE;
            y = Long.MAX_VALUE;
        } else {
            x = y + 1;
            y = 0;
        }
    }

    @Override
    public Spliterator<CantorPair> trySplit() {
        if (x + y == finalSum) {
            return null;
        }
        final CantorPairSpliterator prefix = new CantorPairSpliterator(x, y, x + y);
        x = x + y + 1;
        y = 0;
        return prefix;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return IMMUTABLE + DISTINCT + NONNULL + ORDERED;
    }
}
