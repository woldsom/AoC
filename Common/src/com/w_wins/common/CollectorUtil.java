package com.w_wins.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class CollectorUtil {
    public static <T> FunctionalCollector<T, List<T>, List<T>> toList() {
        return FunctionalCollector.of(ArrayList::new, List::add, Functions.createAndModifySame(ArrayList::new, List::addAll));
    }

    public static <T> FunctionalCollector<T, SortedSet<T>, SortedSet<T>> toSortedSet() {
        return FunctionalCollector.of(TreeSet::new, SortedSet::add, Functions.createAndModifySame(TreeSet::new, SortedSet::addAll));
    }

    public static <K, V> FunctionalCollector<Map.Entry<K, V>, ?, Map<K, V>> toMap() {
        return FunctionalCollector.wrap(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V> FunctionalCollector<Map.Entry<K, V>, ?, SortedMap<K, V>> toSortedMap() {
        return FunctionalCollector.wrap(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Unsupported.binaryOperation(), TreeMap::new));
    }

    public static <K, V> FunctionalCollector<Map.Entry<K, V>, ?, Map<V, List<K>>> toReverseMap() {
        return FunctionalCollector.wrap(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
    }

    public static <T> FunctionalCollector<T, ?, T> groupByReduce(BinaryOperator<T> op) {
        return FunctionalCollector.wrap(Collectors.collectingAndThen(Collectors.reducing(op), Optional::orElseThrow));
    }

    public static <T extends Comparable<? super T>> FunctionalCollector<Set<T>, Set<T>, Set<T>> coalescingSet() {
        return FunctionalCollector.of(TreeSet::new, Set::addAll, Functions.createAndModifySame(TreeSet::new, Set::addAll));
    }

    public static <T> FunctionalCollector<Set<T>, Set<T>, Set<T>> coalescingHashSet() {
        return FunctionalCollector.of(HashSet::new, Set::addAll, Functions.createAndModifySame(HashSet::new, Set::addAll));
    }

    public static <T> BinaryOperator<List<T>> concatList() {
        return concatList(ArrayList::new);
    }

    public static <T> BinaryOperator<List<T>> concatList(final Supplier<List<T>> supplier) {
        return Functions.createAndModifySame(Functions.asFunction(supplier, List::addAll), List::addAll);
    }

    public static <T> BinaryOperator<Set<T>> intersection() {
        return intersection(HashSet::new);
    }

    public static <T> BinaryOperator<Set<T>> intersection(final Supplier<Set<T>> supplier) {
        return Functions.createAndModifySame(Functions.asFunction(supplier, Set::addAll), Set::retainAll);
    }

    public static <K, V> FunctionalCollector<Map.Entry<K, V>, ?, Map<K, Set<V>>> groupingByKey() {
        return groupingByKey(Collectors.toSet());
    }

    public static <K, V, C> FunctionalCollector<Map.Entry<K, V>, ?, Map<K, C>> groupingByKey(Collector<V, ?, C> c) {
        return FunctionalCollector.wrap(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, c)));
    }

    public static <T> FunctionalCollector<T, ?, T> singleton() {
        final Optional<Optional<T>> sentinel = Optional.empty();
        final AtomicReference<Optional<Optional<T>>> singleton = new AtomicReference<>(sentinel);
        return FunctionalCollector.of(
                () -> singleton,
                (a, b) -> a.compareAndExchange(sentinel, Optional.of(Optional.ofNullable(b))).map(c -> {
                    throw new IllegalStateException("Expected single result in stream, got [" + b + "] and [" + c.orElse(null) + "]");
                }),
                (a, b) -> a,
                a -> a.getAcquire().orElseThrow(() -> new IllegalStateException("Expected single result in stream, got none")).orElse(null),
                Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED
        );
    }

    public static <K, V, U> Collector<Map.Entry<K, V>, ?, Map<K, U>> toMap(final Collector<V, ?, U> downstream) {
        return FunctionalCollector.wrap(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, downstream)));
    }

    public static <T> Collector<T, ?, Map<T, Long>> frequency() {
        return Collectors.groupingBy(x -> x, Collectors.counting());
    }

    public static <T,V> Collector<T, ?, Map<T, V>> frequency(final Function<Long,V> alter) {
        return Collectors.groupingBy(x -> x, Collectors.collectingAndThen(Collectors.counting(),alter));
    }
}
