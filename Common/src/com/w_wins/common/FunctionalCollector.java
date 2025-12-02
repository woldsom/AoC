package com.w_wins.common;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public interface FunctionalCollector<T, A, R> extends Collector<T, A, R>, Function<Stream<T>,R> {
    static <T, A, R> FunctionalCollector<T, A, R> of(final Supplier<A> supplier, final BiConsumer<A, T> accumulator, final BinaryOperator<A> combiner, final Function<A, R> finisher, final Characteristics... characteristics) {
        return wrap(Collector.of(supplier, accumulator, combiner, finisher, characteristics));
    }

    static <T, A, R> FunctionalCollector<T, A, R> wrap(final Collector<T, A, R> collector) {
        return new FunctionalCollector<>() {
            @Override
            public R apply(final Stream<T> stream) {
                return stream.collect(collector);
            }

            @Override
            public Supplier<A> supplier() {
                return collector.supplier();
            }

            @Override
            public BiConsumer<A, T> accumulator() {
                return collector.accumulator();
            }

            @Override
            public BinaryOperator<A> combiner() {
                return collector.combiner();
            }

            @Override
            public Function<A, R> finisher() {
                return collector.finisher();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return collector.characteristics();
            }
        };
    }

    static <T, R> FunctionalCollector<T, R, R> of(final Supplier<R> supplier, final BiConsumer<R, T> accumulator, final BinaryOperator<R> combiner, final Characteristics... characteristics) {
        return wrap(Collector.of(supplier, accumulator, combiner, characteristics));
    }
}
