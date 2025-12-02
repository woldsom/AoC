package com.w_wins.common;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class Accumulator<R, U> implements Consumer<R>, Supplier<U> {
    private final BiFunction<U, R, U> accumulator;
    private U accumulated;

    public Accumulator(final U setInitial, final BiFunction<U, R, U> setAccumulator) {
        accumulator = setAccumulator;
        accumulated = setInitial;
    }

    @Override
    public void accept(final R newRecord) {
        accumulated = accumulator.apply(accumulated, newRecord);
    }

    @Override
    public U get() {
        return accumulated;
    }

    public Collector<R, ?, U> asCollectorForSerial() {
        final AtomicReference<U> collectorReference = new AtomicReference(accumulated);
        return new Collector<R, AtomicReference<U>, U>() {
            @Override
            public Supplier<AtomicReference<U>> supplier() {
                return () -> collectorReference;
            }

            @Override
            public BiConsumer<AtomicReference<U>, R> accumulator() {
                return (state, record) -> {
                    accumulated = accumulator.apply(state.get(), record);
                    collectorReference.set(accumulated);
                };
            }

            @Override
            public BinaryOperator<AtomicReference<U>> combiner() {
                return Unsupported.binaryOperation("Cannot use this collector in parallel execution");
            }

            @Override
            public Function<AtomicReference<U>, U> finisher() {
                return AtomicReference::get;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.noneOf(Characteristics.class);
            }
        };
    }
}
