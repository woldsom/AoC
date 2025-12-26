package com.w_wins.common;

import java.lang.Math;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Streams {
    public static <T, U> Stream<U> pairStream(final Collection<T> collection, final BiFunction<T, T, U> function) {
        return pairStream(collection.stream(), function);
    }

    public static <T, U> Stream<U> pairStream(final Stream<T> stream, final BiFunction<T, T, U> function) {
        final AtomicReference<T> previousElement = new AtomicReference<>();
        return splitFirst(stream, previousElement::set).map(x -> function.apply(previousElement.getAndSet(x), x));
    }

    public static <T> Stream<T> splitFirst(final Stream<T> stream, final Consumer<T> firstConsumer) {
        final int[] counter = new int[]{0};
        return stream.sequential().filter(t ->
                {
                    if (counter[0]++ == 0) {
                        firstConsumer.accept(t);
                        return false;
                    } else {
                        return true;
                    }
                }
        );
    }

    public static <T> Stream<T> splitLast(final Stream<T> stream, final Consumer<T> lastConsumer) {
        final Spliterator<T> delegate = stream.spliterator();
        final AtomicReference<Optional<T>> previousElement = new AtomicReference<>();
        final Spliterator<T> iterator = new Spliterators.AbstractSpliterator<T>(delegate.estimateSize(), delegate.characteristics() & ~Spliterator.SIZED) {
            @Override
            public boolean tryAdvance(final Consumer<? super T> action) {
                final AtomicReference<T> elementReference = new AtomicReference<>();
                if (delegate.tryAdvance(elementReference::set)) {
                    Optional<T> returnValue;
                    do {
                        returnValue = previousElement.get();
                    } while (!previousElement.compareAndSet(returnValue, Optional.ofNullable(elementReference.get())));
                    if (returnValue != null) {
                        action.accept(returnValue.orElse(null));
                    }
                    return true;
                } else {
                    lastConsumer.accept(previousElement.get().orElse(null));
                    return false;
                }
            }
        };
        return StreamSupport.stream(iterator, false);
    }

    public static <T, U> Stream<U> unpartition(final Map<Boolean, ? extends Collection<T>> partitionMap, final Function<T, U> falseMap, final Function<T, U> trueMap) {
        return Stream.concat(partitionMap.get(false).stream().map(falseMap), partitionMap.get(true).stream().map(trueMap));
    }

    public static IntStream intUnpartition(final Map<Boolean, ? extends Collection<? extends Number>> partitionMap, final IntUnaryOperator falseMap, final IntUnaryOperator trueMap) {
        return IntStream.concat(partitionMap.get(false).stream().mapToInt(Number::intValue).map(falseMap), partitionMap.get(true).stream().mapToInt(Number::intValue).map(trueMap));
    }

    public static void processPairStream(final IntStream stream, final IntBiConsumer consumer) {
        final int[] element = new int[1];
        final boolean[] initialized = new boolean[1];
        stream.forEach(x -> {
            try {
                if (!initialized[0]) {
                    initialized[0] = true;
                } else {
                    consumer.accept(element[0], x);
                }
            } finally {
                element[0] = x;
            }
        });
    }

    public static void circularPairStream(final IntStream stream, final IntBiConsumer consumer) {
        final int[] elements = new int[2];
        final boolean[] initialized = new boolean[1];
        stream.forEach(x -> {
            try {
                if (!initialized[0]) {
                    elements[1] = x;
                    initialized[0] = true;
                } else {
                    consumer.accept(elements[0], x);
                }
            } finally {
                elements[0] = x;
            }
        });
        consumer.accept(elements[0], elements[1]);
    }

    public static <T> void processPairStream(final Stream<T> stream, final BiConsumer<T, T> consumer) {
        pairStream(stream, (BiFunction<T, T, Void>) (a, b) -> {
            consumer.accept(a, b);
            return null;
        }).forEach((_) -> {
        });
    }

    public static <T> Stream<Map.Entry<Integer, T>> asMap(final Stream<T> sequentialStream) {
        final int[] index = new int[]{0};
        return sequentialStream.sequential().map(v -> Map.entry(index[0]++, v));
    }

    public static <T> Stream<Stream<T>> slidingWindow(final Stream<T> stream, final int windowSize) {
        final Deque<T> window = new ArrayDeque<>(windowSize);
        return stream.sequential().map(element -> {
            window.addLast(element);
            if (window.size() > windowSize) {
                window.removeFirst();
            }
            if (window.size() >= windowSize) {
                return window;
            } else {
                return new ArrayDeque<T>();
            }
        }).filter(deque -> !deque.isEmpty()).map(Collection::stream);
    }

    public static Stream<IntStream> slidingWindow(final IntStream stream, final int windowSize) {
        final Deque<Integer> window = new ArrayDeque<>(windowSize);
        return stream.sequential().mapToObj(element -> {
            window.addLast(element);
            if (window.size() > windowSize) {
                window.removeFirst();
            }
            if (window.size() >= windowSize) {
                return window;
            } else {
                return new ArrayDeque<Integer>();
            }
        }).filter(deque -> !deque.isEmpty()).map(deque -> deque.stream().mapToInt(x -> x));
    }

    @Deprecated
    public static Stream<String> split(final String line, final String separator) {
        return Strings.split(line, separator);
    }

    public static IntStream presentInt(final Stream<OptionalInt> original) {
        return original.filter(OptionalInt::isPresent).mapToInt(OptionalInt::orElseThrow);
    }

    public static LongStream presentLong(final Stream<OptionalLong> original) {
        return original.filter(OptionalLong::isPresent).mapToLong(OptionalLong::orElseThrow);
    }

    public static <K, V> Stream<Map.Entry<K, V>> presentValue(final Stream<Map.Entry<K, Optional<V>>> original) {
        return present(original.map(entry -> entry.getValue().map(value -> Map.entry(entry.getKey(), value))));
    }

    public static <R> Stream<R> present(final Stream<Optional<R>> original) {
        return original.filter(Optional::isPresent).map(Optional::orElseThrow);
    }

    public static Stream<Map.Entry<Integer, Integer>> asMap(final IntStream sequentialStream) {
        final int[] index = new int[]{0};
        return sequentialStream.sequential().mapToObj(v -> Map.entry(index[0]++, v));
    }

    public static <R> Stream<R> pairStream(final IntStream original, final BiIntFunction<R> mapping) {
        final AtomicInteger memory = new AtomicInteger();
        final AtomicBoolean initialized = new AtomicBoolean();
        return original.sequential().filter(i -> {
            if (initialized.compareAndExchange(false, true)) {
                return true;
            }
            if (memory.compareAndSet(0, i)) {
                return false;
            } else {
                throw new ConcurrentModificationException("Somehow a different worker beat us to setting the internal memory in the pair stream transformer");
            }
        }).mapToObj(i -> mapping.apply(memory.getAndSet(i), i));
    }

    public static <R> Stream<R> topK(final Stream<R> original, final Comparator<? super R> comparator, final int k) {
        final SortedSet<R> elements = new TreeSet<>(comparator.reversed());
        original.forEach(element -> {
            if (elements.size() < k) {
                elements.add(element);
            } else {
                final R existingMinimum = elements.last();
                if (comparator.compare(element, existingMinimum) > 0) {
                    elements.add(element);
                    elements.remove(existingMinimum);
                }
            }
        });
        return elements.stream();
    }

    public static IntStream topK(final IntStream original, final int k) {
        final int[] elements = new int[k];
        int[] elementIndex = {0};
        original.forEach(value -> {
            if (elementIndex[0] < k) {
                elements[elementIndex[0]] = value;
                ++elementIndex[0];
                if (elementIndex[0] == k) {
                    Arrays.sort(elements);
                }
            } else {
                final int searchResult = Arrays.binarySearch(elements, value);
                final int toIndex;
                if (searchResult < 0) {
                    toIndex = -1 - searchResult;
                } else {
                    toIndex = searchResult;
                }
                if (toIndex > 0) {
                    for (int index = 0; index < toIndex - 1; ++index) {
                        elements[index] = elements[index + 1];
                    }
                    elements[toIndex - 1] = value;
                }
            }
        });
        return Arrays.stream(elements);
    }

    public static <T> Stream<T> reverse(final List<T> list) {
        if (list.isEmpty()) {
            return Stream.empty();
        } else {
            final Iterator<T> iterator = CollectionUtil.reverse(list);
            return fromIterator(iterator);
        }
    }

    public static <T> Stream<T> fromIterator(final Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    public static <T> boolean allDistinct(final Stream<T> stream) {
        final Set<T> set = new HashSet<>();
        final List<T> list = new ArrayList<>();
        stream.forEach(t -> {
            set.add(t);
            list.add(t);
        });
        return set.size() == list.size();
    }

    public static <T> void circularPairStream(Stream<T> stream, BiConsumer<T, T> consumer) {
        final AtomicReference<T> zero = new AtomicReference<>();
        final AtomicReference<T> one = new AtomicReference<>();
        boolean[] initialized = new boolean[1];
        stream.forEach((x) -> {
            try {
                if (!initialized[0]) {
                    one.set(x);
                    initialized[0] = true;
                } else {
                    consumer.accept(zero.get(), x);
                }
            } finally {
                zero.set(x);
            }

        });
        consumer.accept(zero.get(), one.get());
    }

    public static <T, A, B> Stream<T> zip(final Stream<A> left, final Stream<B> right, final BiFunction<A, B, T> pair) {
        final Spliterator<A> aSpliterator = left.spliterator();
        final Spliterator<B> bSpliterator = right.spliterator();
        final long estimatedSize = Math.min(aSpliterator.estimateSize(), bSpliterator.estimateSize());
        final int characteristics = aSpliterator.characteristics() & bSpliterator.characteristics();
        final Iterator<A> a = Spliterators.iterator(aSpliterator);
        final Iterator<B> b = Spliterators.iterator(bSpliterator);
        return StreamSupport.stream(
                Spliterators.spliterator(new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return a.hasNext() && b.hasNext();
                    }

                    @Override
                    public T next() {
                        return pair.apply(a.next(), b.next());
                    }
                }, estimatedSize, characteristics),
                false
        );
    }

    public static <T> Stream<T> interlace(Stream<T> left, Stream<T> right) {
        return interlace(new int[]{1, 1}, left, right);
    }

    @SafeVarargs
    private static <T> Stream<T> interlace(final int[] order, final Stream<T>... streams) {
        if (order.length > streams.length) {
            throw new IllegalArgumentException("Not enough streams for " + Arrays.toString(order));
        } else {
            final int[] iterationOrder = IntStream.range(0, order.length).mapToObj(Functions.intEntry(index -> order[index])).flatMap(entry -> IntStream.range(0, entry.getValue()).map(_ -> entry.getKey()).boxed()).mapToInt(x -> x).toArray();
            final List<Iterator<T>> iterators = Arrays.stream(streams).map(Stream::iterator).toList();
            final AtomicBoolean done = new AtomicBoolean();
            return IntStream.iterate(0, x -> x + 1).map(x -> iterationOrder[x]).boxed().mapMulti((value, consumer) -> {
                final Iterator<T> iterator = iterators.get(value);
                if (iterator.hasNext() && !done.get()) {
                    consumer.accept(iterator.next());
                } else {
                    done.set(true);
                }
            });
        }
    }

    public static NavigableMap<Integer, Integer> runs(final IntStream integers) {
        return integers.boxed().gather(Gatherers.runs()).reduce(new TreeMap<>(), (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        });
    }

    public static Stream<Boolean> booleanStream() {
        return Stream.of(false, true);
    }

    public static <V> Stream<Map.Entry<Integer, V>> asMap(final Stream<V> stream, final Function<Integer, V> continuation, final int howLong) {
        final List<Map.Entry<Integer, V>> known = asMap(stream).limit(howLong + 1).toList();
        if (known.size() > howLong) {
            throw new IllegalArgumentException("Stream is already " + known.size() + " elements, you asked for it to be extended to " + howLong);
        }
        return IntStream.range(0, howLong).mapToObj(where -> where < known.size() ? known.get(where) : Map.entry(where, continuation.apply(where)));
    }
}
