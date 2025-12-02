package com.w_wins.common;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ArrayMap<T> extends AbstractMap<Integer, T> {
    private final T[] array;

    public ArrayMap(final T[] setArray) {
        array = setArray;
    }

    @Override
    public Set<Map.Entry<Integer, T>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Map.Entry<Integer, T>> iterator() {
                return new Iterator<>() {
                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < array.length;
                    }

                    @Override
                    public Map.Entry<Integer, T> next() {
                        return new ArrayMap.Entry(index++);
                    }
                };
            }

            @Override
            public int size() {
                return array.length;
            }
        };
    }

    @Override
    public T get(final Object key) {
        if (key instanceof Integer) {
            return array[(Integer) key];
        } else {
            return null;
        }
    }

    @Override
    public T put(final Integer key, final T value) {
        final T old = get(key);
        array[key] = value;
        return old;
    }

    private class Entry implements Map.Entry<Integer, T> {
        private final int index;

        public Entry(final int setIndex) {
            index = setIndex;
        }

        @Override
        public Integer getKey() {
            return index;
        }

        @Override
        public T getValue() {
            return array[index];
        }

        @Override
        public T setValue(final T value) {
            final T old = getValue();
            array[index] = value;
            return old;
        }
    }
}
