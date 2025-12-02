package com.w_wins.common;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

class KeyWrapMap<V, M, K> extends AbstractMap<M, V> {
    private final Map<K, V> original;
    private final Function<M, K> keyMapper;
    private final Function<K, M> reverseMapper;

    public KeyWrapMap(final Map<K, V> setOriginal, final Function<M, K> setKeyMapper, final Function<K, M> setReverseMapper) {
        original = setOriginal;
        keyMapper = setKeyMapper;
        reverseMapper = setReverseMapper;
    }

    @Override
    public V get(final Object key) {
        final M safeKey;
        try {
            safeKey = (M) key;
        } catch (final ClassCastException cce) {
            return null;
        }
        return original.get(keyMapper.apply(safeKey));
    }

    @Override
    public V put(final M key, final V value) {
        return original.put(keyMapper.apply(key), value);
    }

    @Override
    public V remove(final Object key) {
        final M safeKey;
        try {
            safeKey = (M) key;
        } catch (final ClassCastException cce) {
            return null;
        }
        return original.remove(keyMapper.apply(safeKey));
    }

    @Override
    public Set<Entry<M, V>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Entry<M, V>> iterator() {
                final Iterator<Entry<K, V>> originalIterator = original.entrySet().iterator();
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return originalIterator.hasNext();
                    }

                    @Override
                    public Entry<M, V> next() {
                        final Entry<K, V> originalEntry = originalIterator.next();
                        return new Entry<M, V>() {
                            @Override
                            public int hashCode() {
                                return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
                            }

                            @Override
                            public boolean equals(final Object obj) {
                                if (obj instanceof Map.Entry<?, ?> o) {
                                    return (getKey() == null ? o.getKey() == null : getKey().equals(o.getKey())) && (getValue() == null ? o.getValue() == null : getValue().equals(o.getValue()));
                                } else {
                                    return false;
                                }
                            }

                            @Override
                            public M getKey() {
                                return reverseMapper.apply(originalEntry.getKey());
                            }

                            @Override
                            public V getValue() {
                                return originalEntry.getValue();
                            }

                            @Override
                            public V setValue(final V value) {
                                return originalEntry.setValue(value);
                            }
                        };
                    }
                };
            }

            @Override
            public int size() {
                return original.entrySet().size();
            }

            @Override
            public void clear() {
                original.clear();
            }
        };
    }
}
