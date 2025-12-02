package com.w_wins.common;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Consumers {
    public static <A, B> Consumer<B> bindLeft(final BiConsumer<A, B> consumer, final A a) {
        return b -> consumer.accept(a, b);
    }

    public static <A, B> Consumer<A> bindRight(final BiConsumer<A, B> consumer, final B b) {
        return a -> consumer.accept(a, b);
    }

    public static <A, B> BiConsumer<A, B> ignoreRight(final Consumer<A> consumer) {
        return (a, b) -> consumer.accept(a);
    }

    public static <A, B> BiConsumer<A, B> ignoreLeft(final Consumer<B> consumer) {
        return (a, b) -> consumer.accept(b);
    }
}
