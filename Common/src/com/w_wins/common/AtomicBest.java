package com.w_wins.common;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class AtomicBest<V> {
    private final AtomicReference<BestStore<V>> store;

    public AtomicBest() {
        store = new AtomicReference<>();
    }

    public AtomicBest(final long setScore, final V setValue) {
        final BestStore<V> newStore = new BestStore<>(setScore, setValue, false);
        store = new AtomicReference<>(newStore);
    }

    public boolean isBest(long score, V value) {
        Objects.requireNonNull(value);
        BestStore<V> previous;
        final BestStore<V> newStore = new BestStore<>(score, value, false);
        do {
            previous = store.get();
            if (previous != null && score <= previous.score()) {
                return false;
            } else if (previous != null && previous.consumed()) {
                throw new IllegalStateException("Update concurrent with consume");
            }
        } while (!store.compareAndSet(previous, newStore));
        return true;
    }

    public OptionalLong consumeAndReturnScore() {
        final OptionalLong[] r = new OptionalLong[1];
        consume((a, b) -> r[0] = a);
        return r[0];
    }

    public void consume(final BiConsumer<OptionalLong, Optional<V>> consumer) {
        BestStore<V> previous;
        final BestStore<V> newStore = new BestStore<>(0, null, true);
        do {
            previous = store.get();
            if (previous != null && previous.consumed()) {
                throw new IllegalStateException("Multiple calls to consume");
            }
        } while (!store.compareAndSet(previous, newStore));
        if (previous == null) {
            consumer.accept(OptionalLong.empty(), Optional.empty());
        } else {
            consumer.accept(OptionalLong.of(previous.score()), Optional.of(previous.value()));
        }
    }

    public Optional<V> consumeAndReturnValue() {
        final AtomicReference<Optional<V>> r = new AtomicReference<>();
        consume((a, b) -> r.set(b));
        return r.get();
    }

    public boolean volatileCheckBest(final long score) {
        final BestStore<V> value = store.get();
        return value == null || value.score() < score;
    }

    private record BestStore<T>(long score, T value, boolean consumed) {
    }
}
