package com.w_wins.common;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class StringMap extends AbstractMap<Integer,Character> {
    private final String string;

    public StringMap(final String setString) {
        string = setString;
    }

    @Override
    public Set<Map.Entry<Integer, Character>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Map.Entry<Integer, Character>> iterator() {
                return new Iterator<>() {
                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < string.length();
                    }

                    @Override
                    public Map.Entry<Integer, Character> next() {
                        return new StringMap.Entry(index++);
                    }
                };
            }

            @Override
            public int size() {
                return string.length();
            }
        };
    }

    @Override
    public Character get(final Object key) {
        if (key instanceof Integer) {
            return string.charAt((Integer) key);
        } else {
            return null;
        }
    }

    private class Entry implements Map.Entry<Integer, Character> {
        private final int index;

        public Entry(final int setIndex) {
            index = setIndex;
        }

        @Override
        public Integer getKey() {
            return index;
        }

        @Override
        public Character getValue() {
            return string.charAt(index);
        }

        @Override
        public Character setValue(final Character value) {
            throw new UnsupportedOperationException("Unmodifiable map");
        }

        @Override
        public String toString() {
            return string.substring(0,index)+">"+string.charAt(index)+string.substring(index+1);
        }
    }
}
