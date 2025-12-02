package com.w_wins.common;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
    void accept(final A a, final B b, final C c);
}
