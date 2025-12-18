package com.w_wins.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.powExact;

public final class CollectionUtil {
    public static <T, N extends Collection<? extends T>> Stream<T> removeSingletons(final Collection<N> collection) {
        return remove(collection, c -> c.size() == 1).map(CollectionUtil::exactlyOne);
    }

    public static <T, N extends Collection<? extends T>> Stream<N> remove(final Collection<N> collection, final Predicate<N> predicate) {
        final List<N> toRemove = collection.stream().filter(predicate).toList();
        collection.removeIf(predicate);
        return toRemove.stream();
    }

    public static <T> T exactlyOne(final Collection<? extends T> collection) {
        if (collection.size() == 1) {
            return collection.iterator().next();
        } else {
            throw new NoSuchElementException("Collection does not consist of one element: " + collection);
        }
    }

    public static <K, T, N extends Collection<? extends T>> Stream<Map.Entry<K, T>> removeSingletons(final Map<K, N> map) {
        return remove(map, (k, v) -> v.size() == 1).map(Functions.onValue(CollectionUtil::exactlyOne));
    }

    public static <K, T, N extends Collection<? extends T>> Stream<Map.Entry<K, N>> remove(final Map<K, N> map, final BiPredicate<K, N> predicate) {
        final Map<K, N> toRemove = map.entrySet().stream().filter(Functions.apply(predicate)).collect(CollectorUtil.toMap());
        map.keySet().removeIf(toRemove::containsKey);
        return toRemove.entrySet().stream();
    }

    public static <K, V, N extends Collection<V>> Map<K, V> reduceValue(final Map<K, N> map, final BinaryOperator<V> operator) {
        return transform(map, l -> l.stream().reduce(operator).orElseThrow());
    }

    public static <K, V, U> Map<K, U> transform(final Map<K, V> map, final Function<V, U> function) {
        return transform(map, HashMap::new, function);
    }

    public static <K, V, U> Map<K, U> transform(final Map<K, V> map, Supplier<Map<K, U>> supplier, final Function<V, U> function) {
        final Map<K, U> returnValue = supplier.get();
        map.forEach((k, v) -> returnValue.put(k, function.apply(v)));
        return returnValue;
    }

    public static <T> List<T> startOf(final Collection<T> collection, final int end) {
        return startOf(collection, ArrayList::new, end);
    }

    public static <T, N extends Collection<? super T>> N startOf(final Collection<T> collection, final Supplier<N> supplier, final int end) {
        final N n = supplier.get();
        int count = 0;
        for (final T t : collection) {
            if (count == end) {
                return n;
            }
            n.add(t);
            ++count;
        }
        return n;
    }

    public static <K, V> Map<V, List<K>> reverseMap(final Map<K, V> map) {
        return map.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
    }

    public static <K, V, M> Map<M, V> keyWrap(Map<K, V> original, Function<M, K> keyMapper, Function<K, M> reverseMapper) {
        return new KeyWrapMap<>(original, keyMapper, reverseMapper);
    }

    public static <K, V> Map<K, V> mapOf(final K kA, final V vA, final K kB, final V vB) {
        final Map<K, V> mapper = new HashMap<>();
        mapper.put(kA, vA);
        mapper.put(kB, vB);
        return mapper;
    }

    public static <K, V> Map<K, V> mapOf(final K kA, final V vA, final K kB, final V vB, final K kC, final V vC) {
        final Map<K, V> mapper = new HashMap<>();
        mapper.put(kA, vA);
        mapper.put(kB, vB);
        mapper.put(kC, vC);
        return mapper;
    }

    public static <K, V> Map<K, V> mapOf(final K kA, final V vA, final K kB, final V vB, final K kC, final V vC, final K kD, final V vD) {
        final Map<K, V> mapper = new HashMap<>();
        mapper.put(kA, vA);
        mapper.put(kB, vB);
        mapper.put(kC, vC);
        mapper.put(kD, vD);
        return mapper;
    }

    public static <T> List<T> subtract(final List<T> original, final T subtrahend) {
        return original.stream().filter(t -> !subtrahend.equals(t)).collect(Collectors.toList());
    }

    public static <T> Set<T> intersection(final Set<T> a, final Set<T> b) {
        if (a.size() > b.size()) {
            return intersection(b, a);
        } else {
            final HashSet<T> intersection = new HashSet<>(a);
            intersection.retainAll(b);
            return intersection;
        }
    }

    public static <T> Iterator<T> reverse(final List<T> list) {
        final ListIterator<T> delegate = list.listIterator(list.size());
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return delegate.hasPrevious();
            }

            @Override
            public T next() {
                return delegate.previous();
            }
        };
    }

    public static <A extends Collection<B>, B extends Comparable<B>> Comparator<A> comparingElements() {
        return Comparator.<A>comparingInt(Collection::size).thenComparing((A a, A b) -> {
            final Iterator<B> aI = a.iterator();
            final Iterator<B> bI = b.iterator();
            while (aI.hasNext()) {
                final int result = aI.next().compareTo(bI.next());
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        });
    }

    public static <T> List<List<T>> transpose(List<List<T>> input) {
        return transpose(input, ArrayList::new);
    }

    public static <T> List<List<T>> transpose(List<List<T>> input, final Supplier<List<T>> factory) {
        final List<Iterator<T>> iterators = input.stream().map(List::iterator).toList();
        final List<List<T>> returnValue = new ArrayList<>();
        while (true) {
            if (!iterators.stream().anyMatch(Iterator::hasNext)) {
                break;
            }
            returnValue.add(iterators.stream().filter(Iterator::hasNext).map(Iterator::next).collect(Collectors.toCollection(factory)));
        }
        return returnValue;
    }

    public static <T, A, B> List<T> zip(final List<A> a, final List<B> b, final BiFunction<A, B, T> pair) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Lists must be same size");
        }
        final List<T> returnValue = new ArrayList<>();
        for (int index = 0; index < a.size(); ++index) {
            returnValue.add(pair.apply(a.get(index), b.get(index)));
        }
        return returnValue;
    }

    public static <T extends Comparable<? super T>> List<T> sorted(List<T> list) {
        Collections.sort(list);
        return list;
    }

    public static NavigableMap<Integer, Integer> runs(final List<Integer> gaps) {
        return Streams.runs(gaps.stream().mapToInt(x -> x));
    }

    public static Integer mostCommon(final List<Integer> numbers) {
        return numbers.stream().collect(CollectorUtil.frequency()).entrySet().stream().max(Comparator.comparingLong(Map.Entry::getValue)).orElseThrow().getKey();
    }

    public static <K> Map<K, BigInteger> mergeFrequencyMaps(Map<K, BigInteger> a, Map<K, BigInteger> b) {
        final Map<K, BigInteger> newMap = new HashMap<>(a);
        b.forEach((key, value) -> {
            if (newMap.containsKey(key)) {
                newMap.put(key, value.add(newMap.get(key)));
            } else {
                newMap.put(key, value);
            }
        });
        return newMap;
    }

    public static <V> void swap(final ArrayList<V> list, final int a, final int b) {
        final V tmp = list.get(a);
        list.set(a, list.get(b));
        list.set(b, tmp);
    }

    public static <V, U> Stream<U> selfCross(final List<V> list, final BiFunction<V, V, U> consumer) {
        return IntStream.range(0, list.size() - 1).boxed().flatMap(a -> IntStream.range(a + 1, list.size()).mapToObj(b -> consumer.apply(list.get(a), list.get(b))));
    }

    public static <V> Stream<Set<V>> subsets(final Set<V> set) {
        return IntStream.range(0, powExact(2, set.size())).boxed().map(Functions.<Set<V>, Integer, Set<V>>bindLeft(CollectionUtil::subset, set));
    }

    private static <V> Set<V> subset(final Set<V> set, final int mask) {
        return Streams.asMap(set.stream()).filter(e -> (powExact(2, e.getKey()) & mask) > 0).map(Map.Entry::getValue).collect(Collectors.toSet());
    }

    public static <T> T singleton(final Collection<T> collection) {
        return collection.stream().findAny().orElseThrow();
    }
}
