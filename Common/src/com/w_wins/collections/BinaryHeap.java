package com.w_wins.collections;

import com.w_wins.common.CollectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class BinaryHeap<K extends Comparable<? super K>, V> {
    private final ArrayList<HeapElement<K, V>> binary;
    private final Map<V, Integer> indexOf = new HashMap<>();

    private BinaryHeap(final ArrayList<HeapElement<K, V>> setBinary) {
        binary = setBinary;
        IntStream.range(0, binary.size()).forEach(index -> {
            indexOf.put(binary.get(index).value(), index);
        });
    }

    public BinaryHeap(final Collection<V> elements, final Function<V, K> setComparableExtractor) {
        binary = new ArrayList<>(elements.stream().map(HeapElement.withFunction(setComparableExtractor)).toList());
        heapify();
        IntStream.range(0, binary.size()).forEach(index -> indexOf.put(binary.get(index).value, index));
    }

    public static <K extends Comparable<? super K>, V> BinaryHeap<K, V> build(final Collection<V> vertexes, final Function<V, K> comparableExtractor) {
        return new BinaryHeap<>(vertexes, comparableExtractor);
    }

    public V pop() {
        final HeapElement<K, V> returnValue = binary.getFirst();
        indexOf.remove(returnValue.value());
        final HeapElement<K, V> replaceValue = binary.removeLast();
        if (!binary.isEmpty()) {
            binary.set(0, replaceValue);
            indexOf.put(replaceValue.value(), 0);
            bigGoLeafward(0);
        }
        return returnValue.value();
    }

    public V peek() {
        return binary.getFirst().value();
    }

    public void increaseKey(V v, K newComparable) {
        final Integer index = indexOf.get(v);
        if (index == null) {
            throw new NoSuchElementException();
        }
        binary.set(index, new HeapElement<>(newComparable, v));
        indexOf.put(v, index);
        bigGoLeafward(index);
    }

    public boolean isEmpty() {
        return binary.isEmpty();
    }

    private void heapify() {
        final int nonLeaf = (binary.size() + 1) / 2;
        IntStream.range(0, nonLeaf).map(x -> nonLeaf - 1 - x).forEach(this::bigGoLeafward);
    }

    private void bigGoLeafward(final int from) {
        int currentPercolate = from;
        while (currentPercolate < binary.size()) {
            final int leftChildIndex = currentPercolate * 2 + 1;
            final int rightChildIndex = currentPercolate * 2 + 2;
            if (leftChildIndex < binary.size()) {
                if (rightChildIndex < binary.size() && binary.get(rightChildIndex).key().compareTo(binary.get(leftChildIndex).key()) < 0) {
                    if (binary.get(rightChildIndex).key().compareTo(binary.get(currentPercolate).key()) < 0) {
                        CollectionUtil.swap(binary, currentPercolate, rightChildIndex);
                        indexOf.put(binary.get(currentPercolate).value, currentPercolate);
                        indexOf.put(binary.get(rightChildIndex).value, rightChildIndex);
                        currentPercolate = rightChildIndex;
                    } else {
                        break;
                    }
                } else {
                    if (binary.get(leftChildIndex).key().compareTo(binary.get(currentPercolate).key()) < 0) {
                        CollectionUtil.swap(binary, currentPercolate, leftChildIndex);
                        indexOf.put(binary.get(currentPercolate).value, currentPercolate);
                        indexOf.put(binary.get(leftChildIndex).value, leftChildIndex);
                        currentPercolate = leftChildIndex;
                    } else {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    public Stream<V> stream() {
        final Iterator<V> iterator = new Iterator<V>() {
            @Override
            public boolean hasNext() {
                return !isEmpty();
            }

            @Override
            public V next() {
                return pop();
            }
        };
        return StreamSupport.stream(Spliterators.spliterator(iterator, binary.size(), Spliterator.SORTED | Spliterator.ORDERED), false);
    }

    public BinaryHeap<K, V> copy() {
        return new BinaryHeap<>(new ArrayList<>(binary));
    }

    public K peekKey(final V vertex) {
        final Integer index = indexOf.get(vertex);
        if (index == null) {
            throw new NoSuchElementException(vertex + " not in " + binary.stream().map(HeapElement::value).toList());
        }
        return binary.get(index).key();
    }

    public void decrementKey(final V value, final K newKey) {
        final Integer index = indexOf.get(value);
        if (index == null) {
            throw new NoSuchElementException();
        }
        binary.set(index,new HeapElement<>(newKey,value));
        int currentIndex = index;
        while (currentIndex > 0) {
            final int parent = (currentIndex - 1) / 2;
            if (binary.get(parent).key().compareTo(binary.get(currentIndex).key()) > 0) {
                CollectionUtil.swap(binary, currentIndex, parent);
                indexOf.put(binary.get(parent).value, parent);
                indexOf.put(binary.get(currentIndex).value, currentIndex);
            } else {
                break;
            }
            currentIndex = parent;
        }
    }

    public boolean contains(V value) {
        return indexOf.containsKey(value);
    }

    @Override
    public String toString() {
        return "BinaryHeap[" + content() + "]";
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return this.content().equals(((BinaryHeap<?, ?>) other).content());
    }

    private Set<V> content() {
        return binary.stream().map(HeapElement::value).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private record HeapElement<K extends Comparable<? super K>, V>(
            K key,
            V value) {
        public static <K extends Comparable<? super K>, V> Function<? super V, HeapElement<K, V>> withFunction(final Function<V, K> comparableExtractor) {
            return v -> new HeapElement<K, V>(comparableExtractor.apply(v), v);
        }
    }
}
