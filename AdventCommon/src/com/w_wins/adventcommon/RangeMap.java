package com.w_wins.adventcommon;

import com.w_wins.common.Maps;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RangeMap<V> {
    private final NavigableMap<LongRange, V> ranges;


    public RangeMap() {
        ranges = new TreeMap<>(Comparator.comparing(LongRange::start));
    }

    public static <T> RangeMap<T> of(final LongRange range, final T value) {
        final RangeMap<T> map = new RangeMap<>();
        map.put(range,value);
        return map;
    }

    public RangeMap<Void> intersection(final RangeMap<Void> b) {
        final RangeMap<Void> returnValue = new RangeMap<>();
        stream().forEach(entry -> b.subMap(entry.getKey()).stream().forEach(entry2 -> returnValue.put(entry2.getKey(), null)));
        return returnValue;
    }

    public void put(final LongRange newRange, V value) {
        final LongRange floor = ranges.floorKey(newRange);
        final Optional<LongRange> floorUnion = Optional.ofNullable(floor).flatMap(f -> f.intersection(newRange).isPresent() ? f.unionRange(newRange) : Optional.empty());
        final LongRange ceiling = ranges.ceilingKey(newRange);
        final Optional<LongRange> ceilingUnion = Optional.ofNullable(ceiling).flatMap(f -> f.intersection(newRange).isPresent()?f.unionRange(newRange):Optional.empty());
        if (floorUnion.isPresent()) {
            if (ceilingUnion.isPresent()) {
                final LongRange mergeRange = new LongRange(floor.start(), ceiling.end());
                if (value==null || ranges.get(ceiling).equals(value) && ranges.get(floor).equals(value)) {
                    ranges.remove(floor);
                    ranges.remove(ceiling);
                    ranges.put(mergeRange, value);
                } else {
                    throw new IllegalArgumentException("Overwriting range with different value, was " + ranges.get(floor) + " and " + ranges.get(ceiling) + ", and would become " + value + " for " + mergeRange);
                }
            } else {
                if (value==null || ranges.get(floor).equals(value)) {
                    ranges.remove(floor);
                    ranges.put(floorUnion.orElseThrow(), value);
                } else {
                    throw new IllegalArgumentException("Overwriting range with different value, was " + ranges.get(floor) + ", and would become " + value + " for " + floorUnion.orElseThrow());
                }
            }
        } else if (ceilingUnion.isPresent()) {
            if (value==null || ranges.get(ceiling).equals(value)) {
                ranges.remove(ceiling);
                ranges.put(ceilingUnion.orElseThrow(), value);
            } else {
                throw new IllegalArgumentException("Overwriting range with different value, was " + ranges.get(ceiling) + ", and would become " + value + " for " + ceilingUnion.orElseThrow());
            }
        } else {
            ranges.put(newRange, value);
        }
    }

    public Optional<V> get(final long key) {
        final Optional<LongRange> floor = Optional.ofNullable(ranges.floorKey(new LongRange(key, key)));
        return floor.flatMap(range -> range.contains(key) ? Optional.of(ranges.get(range)) : Optional.empty());
    }

    public boolean containsKey(final long key) {
        final Optional<LongRange> floor = Optional.ofNullable(ranges.floorKey(new LongRange(key, key)));
        return floor.map(range -> range.contains(key)).orElse(false);
    }

    public Stream<Map.Entry<LongRange, V>> stream() {
        return ranges.entrySet().stream();
    }

    @Override
    public String toString() {
        return "RangeMap["+stream().map(Objects::toString).collect(Collectors.joining(";"))+"]";
    }

    public RangeMap<V> subMap(RangeMap<Void> sources) {
        final RangeMap<V> copy = new RangeMap<>();
        sources.stream().map(Map.Entry::getKey).flatMap(range->subMap(range).stream()).forEach(entry->copy.put(entry.getKey(),entry.getValue()));
        return copy;
    }

    public RangeMap<V> subMap(final LongRange range) {
        final RangeMap<V> copy = new RangeMap<>();
        final NavigableMap<LongRange, V> tailMap = Optional.ofNullable(ranges.floorKey(range.transform(l->l-1))).map(floor->ranges.tailMap(floor,true)).orElse(ranges);
        final NavigableMap<LongRange, V> middleMap = Optional.ofNullable(ranges.floorKey(new LongRange(range.end()+1, range.end()+1))).map(ceiling->tailMap.headMap(ceiling,true)).orElse(tailMap);
        middleMap.entrySet().stream().
                map(entry-> Maps.entry(entry.getKey().intersection(range), entry.getValue())).
                filter(entry->entry.getKey().isPresent()).
                map(entry->Maps.entry(entry.getKey().orElseThrow(),entry.getValue())).
                forEach(entry->copy.put(entry.getKey(),entry.getValue()));
        return copy;
    }

    public Map.Entry<LongRange, V> getLastEntry() {
        return ranges.lastEntry();
    }

    public boolean isEmpty() {
        return ranges.isEmpty();
    }

    public Map.Entry<LongRange, V> getFirstEntry() {
        return ranges.firstEntry();
    }

    public long getStart() {
        return ranges.firstKey().start();
    }

    public long getEnd() {
        return ranges.lastKey().end();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RangeMap<?> rangeMap = (RangeMap<?>) o;
        return Objects.equals(ranges, rangeMap.ranges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ranges);
    }
}
