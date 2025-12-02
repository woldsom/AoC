package com.w_wins.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JoinMap<K, V> implements Map<K, V> {
    private final Map<K, V> first, second;

    public JoinMap(final Map<K, V> setFirst, final Map<K, V> setSecond) {
        first = setFirst;
        second = setSecond;
    }

    @Override
    public int size() {
        return first.size() + second.size();
    }

    @Override
    public boolean isEmpty() {
        return first.isEmpty() && second.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return first.containsKey(key) || second.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return first.containsValue(value) || second.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        final V firstEntry = first.get(key);
        if (firstEntry != null || first.containsKey(key)) {
            return firstEntry;
        }
        return second.get(key);
    }

    @Override
    public V put(final K key, final V value) {
        throw new UnsupportedOperationException("Unmodifiable");
    }

    @Override
    public V remove(final Object key) {
        throw new UnsupportedOperationException("Unmodifiable");
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Unmodifiable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Unmodifiable");
    }

    @Override
    public Set<K> keySet() {
        return entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Collection<V> values() {
        return Stream.concat(first.values().stream(),second.values().stream()).toList();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Stream.concat(first.entrySet().stream(), second.entrySet().stream()).collect(Collectors.toUnmodifiableSet());
    }
}
