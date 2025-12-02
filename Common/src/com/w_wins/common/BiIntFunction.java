package com.w_wins.common;

@FunctionalInterface
public interface BiIntFunction<R> {
    R apply(final int first, final int second);
}
