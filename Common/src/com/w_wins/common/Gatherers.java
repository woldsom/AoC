package com.w_wins.common;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Gatherer;

public final class Gatherers {
    public static Gatherer<Integer, ?, NavigableMap<Integer, Integer>> runs() {
        return Gatherer.<Integer, NavigableMap<Integer, Integer>, NavigableMap<Integer, Integer>>ofSequential(TreeMap::new, (state, element, downstream) -> {
            if (state.isEmpty()) {
                state.put(element, 1);
            } else {
                final int runStart = state.firstKey();
                final int runLength = state.firstEntry().getValue();
                if (element == runStart + runLength) {
                    state.clear();
                    state.put(runStart, runLength + 1);
                } else {
                    downstream.push(new TreeMap<>(state));
                    state.clear();
                    state.put(element, 1);
                }
            }
            return true;
        }, (a, b) -> b.push(a));
    }

    public static <T> Gatherer<T, List<T>, List<T>> splitWhen(final Predicate<T> when) {
        return Gatherer.of(ArrayList::new,
                (state, element, downstream) -> {
                    if (when.test(element)) {
                        downstream.push(List.copyOf(state));
                        state.clear();
                    } else {
                        state.add(element);
                    }
                    return true;
                },
                Functions.createAndModifySame(ArrayList::new, List::addAll),
                Functions.pushDownstream());
    }

    public static <T, R, A> Gatherer<T, ?, R> splitWhen(final Predicate<T> when, final Collector<T, A, R> collector) {
        final Supplier<A> supplier = collector.supplier();
        final Function<A, R> finisher = collector.finisher();
        final BiConsumer<A, T> accumulator = collector.accumulator();
        final BinaryOperator<A> combiner = collector.combiner();
        return Gatherer.of(
                () -> new AtomicReference<>(supplier.get()),
                (state, element, downstream) -> {
                    if (when.test(element)) {
                        final A realState = state.get();
                        downstream.push(finisher.apply(realState));
                        if (!state.compareAndSet(realState, supplier.get())) {
                            throw new IllegalStateException("Concurrent modification of state");
                        }
                    } else {
                        accumulator.accept(state.get(), element);
                    }
                    return true;
                },
                (a, b) -> new AtomicReference<>(combiner.apply(a.get(), b.get())),
                (reference, downstream) -> {
                    downstream.push(finisher.apply(reference.get()));
                }
        );
    }
}
