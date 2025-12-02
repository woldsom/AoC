package com.w_wins.advent.twentytwentyfour.daytwelve;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public record RegionOld(
        String what,
        Map<Coordinate, Integer> perimeter,
        Set<Coordinate> area) {
    public static Gatherer<? super BadOldContainer, ?, RegionOld> gatherer() {
        return null;
    }

    public static RegionOld simple(final Coordinate coordinate, final String what) {
        return new RegionOld(what, coordinate.manhattanNeighbours(_ -> true).map(Functions.entry(_ -> 1)).collect(CollectorUtil.toMap()), new HashSet<>(Set.of(coordinate)));
    }

    public static Set<RegionOld> coalesce(final Map<Coordinate, BadOldContainer> map) {
        return map.values().stream().map(BadOldContainer::state).map(AtomicReference::get).map(Set::of).<Set<RegionOld>>map(HashSet::new).reduce(new HashSet<>(), (a, b) -> {
            return coalesce(a, b, map.entrySet().stream().map(Functions.onValue(container -> container.state().get())).collect(CollectorUtil.toMap()));
        });
    }

    private static Set<RegionOld> coalesce(final Set<RegionOld> a, final Set<RegionOld> b, final Map<Coordinate, RegionOld> map) {
        final Set<String> keys = Stream.concat(a.stream(), b.stream()).map(RegionOld::what).collect(Collectors.toSet());
        final Set<RegionOld> returnValue = new HashSet<>();
        final Map<String, Set<RegionOld>> aMap = toMap(a);
        final Map<String, Set<RegionOld>> bMap = toMap(b);
        keys.forEach(letter -> {
            returnValue.addAll(coalesceSingleLetter(aMap.get(letter), bMap.get(letter)));
        });
        return returnValue;
    }

    private static Set<RegionOld> coalesceSingleLetter(final Set<RegionOld> a, final Set<RegionOld> b) {
        final Set<RegionOld> candidates = new HashSet<>();
        if (a != null) {
            candidates.addAll(a);
        }
        if (b != null) {
            candidates.addAll(b);
        }
        if (candidates.isEmpty()) {
            return candidates;
        }
        final AtomicBoolean done = new AtomicBoolean();
        while (!done.get()) {
            final Optional<List<RegionOld>> match = findTwoMatching(candidates);
            match.ifPresentOrElse(list -> {
                final RegionOld c = coalesce(list.getFirst(), list.getLast());
                candidates.remove(list.getFirst());
                candidates.remove(list.getLast());
                candidates.add(c);
            }, () -> done.set(true));
        }
        return candidates;
    }

    private static Optional<List<RegionOld>> findTwoMatching(final Set<RegionOld> a) {
        if (a.isEmpty()) {
            return Optional.empty();
        } else {
            final Optional<List<RegionOld>> returnValue = a.stream().flatMap(aRegion -> {
                return a.stream().filter(not(aRegion::equals)).map(bRegion -> List.of(aRegion, bRegion));
            }).filter(list -> {
                return list.getFirst().perimeter().keySet().stream().anyMatch(list.getLast().area()::contains);
            }).findAny();
            //System.err.println("Matches in " + a + ": " + returnValue);
            return returnValue;
        }
    }

    private static RegionOld coalesce(final RegionOld a, final RegionOld b) {
        final Set<Coordinate> area = new HashSet<>(a.area());
        area.addAll(b.area());
        final Map<Coordinate, Integer> perimeter = new HashMap<>(a.perimeter());
        b.perimeter().forEach((where, count) -> perimeter.merge(where, count, Math::addExact));
        area.forEach(perimeter::remove);
        //System.err.println("Joined " + a + " and " + b + " into new area " + area + " and perimeter " + perimeter);
        return new RegionOld(a.what(), perimeter, area);
    }


    public int price() {
        return Math.multiplyExact(perimeter().values().stream().mapToInt(x -> x).sum(), area().size());
    }

    public int discountPrice() {
        return Math.multiplyExact(newPerimeter(), area().size());
    }

    private int newPerimeter() {
        return Arrays.stream(Coordinate.MANHATTAN_COORDINATES).mapToInt(direction -> {
            final SortedMap<Integer,SortedSet<Integer>> directionPerimeter = new TreeMap<>();
            area().stream().map(direction::plus).filter(not(area()::contains)).forEach(perimeterPart->{
                final int componentOne=perimeterPart.x()*direction.x()+perimeterPart.y()*direction.y();
                final int componentTwo=perimeterPart.x()*direction.clockwise().x()+perimeterPart.y()*direction.clockwise().y();
                directionPerimeter.merge(componentOne,new TreeSet<>(Set.of(componentTwo)),Functions.createAndModifySame(TreeSet::new, SortedSet::addAll));
            });
            return directionPerimeter.values().stream().mapToInt(set-> {
                return Math.toIntExact(set.stream().map(x -> x - 1).filter(not(set::contains)).count());
            }).sum();
        }).sum();
    }

    private static Map<String, Set<RegionOld>> toMap(Set<RegionOld> regions) {
        return regions.stream().map(Functions.entry(RegionOld::what)).map(e -> Map.entry(e.getValue(), e.getKey())).collect(CollectorUtil.toMap(Collectors.toCollection(HashSet::new)));
    }

    public String asTwoDimensional() {
        final int lowestColumn = perimeter().keySet().stream().mapToInt(Coordinate::x).min().orElseThrow();
        final int highestColumn = perimeter().keySet().stream().mapToInt(Coordinate::x).max().orElseThrow();
        final int lowestRow = perimeter().keySet().stream().mapToInt(Coordinate::y).min().orElseThrow();
        final int highestRow = perimeter().keySet().stream().mapToInt(Coordinate::y).max().orElseThrow();
        return IntStream.rangeClosed(lowestRow, highestRow).mapToObj(row -> IntStream.rangeClosed(lowestColumn, highestColumn).mapToObj(column -> new Coordinate(column, row)).map(spot -> perimeter().containsKey(spot) ? perimeter().get(spot).toString() : area().contains(spot) ? what() : ".").collect(Collectors.joining())).collect(Collectors.joining("\n"));
    }
}
