package com.w_wins.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

public final class Functions {
    public static <T, U, R> BiFunction<T, U, R> reverse(final BiFunction<U, T, R> function) {
        return (a, b) -> function.apply(b, a);
    }

    public static <K, V> Function<K, Map.Entry<K, V>> entry(final Function<K, V> valueFunction) {
        return k -> Functions.<K, V>entry().apply(k, valueFunction.apply(k));
    }


    public static <K, V> BiFunction<K, V, Map.Entry<K, V>> entry() {
        return (key, value) -> Map.entry(key, value);
    }

    public static <V> IntFunction<Map.Entry<Integer, V>> intEntry(final IntFunction<V> valueFunction) {
        return k -> Functions.<Integer, V>entry().apply(k, valueFunction.apply(k));
    }

    public static <K, V, U> Function<Map.Entry<K, V>, Map.Entry<K, U>> newValue(final Function<Map.Entry<K, V>, U> valueFunction) {
        return e -> Collections.singletonMap(e.getKey(), valueFunction.apply(e)).entrySet().stream().findAny().orElseThrow();
    }

    public static <K, V, U> Function<Map.Entry<K, V>, Map.Entry<K, U>> newValue(final BiFunction<K, V, U> valueFunction) {
        return e -> Collections.singletonMap(e.getKey(), valueFunction.apply(e.getKey(), e.getValue())).entrySet().stream().findAny().orElseThrow();
    }

    public static <K, V, U> Function<Map.Entry<K, V>, Map.Entry<K, U>> onValue(final Function<V, U> valueFunction) {
        return e -> Collections.singletonMap(e.getKey(), valueFunction.apply(e.getValue())).entrySet().stream().findAny().orElseThrow();
    }

    public static <T, U, R> BiFunction<T, U, R> createAndModify(final Function<T, R> creator, final BiConsumer<R, U> modify) {
        return (a, b) -> {
            final R returnValue = creator.apply(a);
            modify.accept(returnValue, b);
            return returnValue;
        };
    }

    public static <T, R> BiFunction<T, T, R> asBiFunction(final Supplier<R> creator, final BiConsumer<R, T> modify) {
        return (a, b) -> {
            final R returnValue = creator.get();
            modify.accept(returnValue, a);
            modify.accept(returnValue, b);
            return returnValue;
        };
    }

    public static <T, R> Function<T, R> asFunction(final Supplier<R> creator, final BiConsumer<R, T> modify) {
        return a -> {
            final R returnValue = creator.get();
            modify.accept(returnValue, a);
            return returnValue;
        };
    }

    public static <T> BinaryOperator<T> createAndModifySame(final Function<T, T> creator, final BiConsumer<T, T> modify) {
        return createAndModify(creator, modify)::apply;
    }

    public static <K, V> Predicate<Map.Entry<K, V>> apply(final BiPredicate<K, V> usualPredicate) {
        return e -> usualPredicate.test(e.getKey(), e.getValue());
    }

    public static <K, V> Function<K, Optional<V>> get(final Map<K, V> map) {
        return key -> Optional.ofNullable(map.get(key));
    }

    public static <T, U, R> Function<U, R> sequence(Function<U, T> first, Function<U, Function<T, R>> second) {
        return c -> first.andThen(second.apply(c)).apply(c);
    }

    public static <T, U> Function<Optional<T>, Optional<U>> onOptionals(final Function<T, U> original) {
        return bindRight(Optional::map, original);
    }

    public static <T, U, R> Function<T, R> bindRight(final BiFunction<T, U, R> function, final U value) {
        return t -> function.apply(t, value);
    }

    public static <T, R> Supplier<R> bind(final Function<T, R> function, final T value) {
        return () -> function.apply(value);
    }

    public static <T, R> Function<Function<T, R>, R> binder(T value) {
        return f -> f.apply(value);
    }

    public static <T, R> Function<Function<T, R>, Supplier<R>> supplierBinder(T value) {
        return f -> () -> f.apply(value);
    }

    public static <T, U, R> Function<BiFunction<T, U, R>, Function<T, R>> rightBinder(U value) {
        return bindRight(Functions::bindRight, value);
    }

    public static <T, U, R> Function<BiFunction<T, U, R>, Function<U, R>> leftBinder(T value) {
        return bindRight(Functions::bindLeft, value);
    }

    public static <T, U, R> Function<U, R> bindLeft(final BiFunction<T, U, R> function, final T value) {
        return u -> function.apply(value, u);
    }

    public static <T, U, R> BiFunction<T, U, R> ignoreLeft(final Function<U, R> function) {
        return (_, u) -> function.apply(u);
    }

    public static <T, U, R> BiFunction<T, U, R> ignoreRight(final Function<T, R> function) {
        return (t, _) -> function.apply(t);
    }

    public static <T, R> Function<T, R> ignoreArgument(final Supplier<R> supplier) {
        return t -> supplier.get();
    }

    public static <K, V, U> BiFunction<K, V, V> createIfMissingAndModify(final Supplier<V> creator, final BiConsumer<V, U> modification, final U value) {
        return (key, nullable) -> {
            final V notNull = Optional.ofNullable(nullable).orElseGet(creator);
            modification.accept(notNull, value);
            return notNull;
        };
    }

    public static <K, V, U> BiFunction<K, V, V> createIfMissingAndModify(final Supplier<V> creator, final Consumer<V> modification) {
        return (key, nullable) -> {
            final V notNull = Optional.ofNullable(nullable).orElseGet(creator);
            modification.accept(notNull);
            return notNull;
        };
    }

    public static <K, V, L, U> BiFunction<K, V, V> createIfMissingAndModify(final Supplier<V> creator, final TriConsumer<V, L, U> modification, final L key, final U value) {
        return (outerKey, nullable) -> {
            final V notNull = Optional.ofNullable(nullable).orElseGet(creator);
            modification.accept(notNull, key, value);
            return notNull;
        };
    }

    public static <A, B> BiFunction<A, B, A> andReturnLeft(final BiConsumer<A, B> consumer) {
        return (a, b) -> {
            consumer.accept(a, b);
            return a;
        };
    }

    public static <A, B> BiFunction<A, B, B> andReturnRight(final BiConsumer<A, B> consumer) {
        return (a, b) -> {
            consumer.accept(a, b);
            return b;
        };
    }

    public static <E, P> Function<String, P> pairSplit(final String delimiter, final Function<String, E> parser, final BiFunction<E, E, P> pairSource) {
        return string -> {
            final String[] parts = string.split(delimiter);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Expected \"" + string + "\" to be split in two by \"" + delimiter + "\", but got " + parts.length + " elements: " + Arrays.toString(parts));
            }
            return pairSource.apply(parser.apply(parts[0]), parser.apply(parts[1]));
        };
    }

    public static <T> T passConsumerAndReturnConsumed(final Function<Consumer<T>, ?> consumerFunction) {
        final AtomicReference<T> reference = new AtomicReference<>();
        consumerFunction.apply(reference::set);
        return reference.get();
    }

    public static <K, S, D> BiConsumer<Map.Entry<K, S>, Consumer<Map.Entry<K, D>>> onValueMulti(final BiConsumer<S, Consumer<D>> valueTransformation) {
        return (entry, consumer) -> valueTransformation.accept(entry.getValue(), (inner) -> consumer.accept(Map.entry(entry.getKey(), inner)));
    }

    public static <K, S, D> BiConsumer<Map.Entry<K, S>, Consumer<Map.Entry<D, S>>> onKeyMulti(final BiConsumer<K, Consumer<D>> keyTransformation) {
        return (entry, consumer) -> keyTransformation.accept(entry.getKey(), (inner) -> consumer.accept(Map.entry(inner, entry.getValue())));
    }

    public static <K, L, V> Function<Map.Entry<K, V>, Map.Entry<L, V>> onKey(Function<K, L> modify) {
        return e -> Map.entry(modify.apply(e.getKey()), e.getValue());
    }

    public static <T, R> Function<T, R> fromPredicate(Predicate<T> predicate, R trueValue, R falseValue) {
        return t -> predicate.test(t) ? trueValue : falseValue;
    }

    public static <T, R> Function<T, R> fromPredicate(Predicate<T> predicate, Function<T, R> trueFunction, Function<T, R> falseFunction) {
        return t -> (predicate.test(t) ? trueFunction : falseFunction).apply(t);
    }

    public static <T> BiConsumer<T, Gatherer.Downstream<? super T>> pushDownstream() {
        return (t, downstream) -> downstream.push(t);
    }

    public static <I, A, R> BiFunction<I, A, R> extraLeft(Function<A, R> original) {
        return (_, r) -> original.apply(r);
    }

    public static <I, A, R> BiFunction<A, I, R> extraRight(Function<A, R> original) {
        return (l, _) -> original.apply(l);
    }

    public static <T> T passAndReturn(final T argument, final Consumer<T> consumer) {
        consumer.accept(argument);
        return argument;
    }

    public static <K, V> Function<Map.Entry<K, V>, Map.Entry<V, K>> flipEntry() {
        return e -> Map.entry(e.getValue(), e.getKey());
    }
}
