package com.w_wins.advent.twentytwentyfour.daytwentyone;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.CollectionUtil;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Strings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.util.function.Predicate.not;

public record Pad(
        Coordinate activate,
        List<Coordinate> digitsOrDirections,
        String key,
        Coordinate missing) {
    public static final Coordinate[] directions = new Coordinate[]{new Coordinate(0, -1), new Coordinate(-1, 0), new Coordinate(0, 1), new Coordinate(1, 0)};
    public static final Pad NUMERIC = new Pad(new Coordinate(2, 3), List.of(
            new Coordinate(1, 3),
            new Coordinate(0, 2),
            new Coordinate(1, 2),
            new Coordinate(2, 2),
            new Coordinate(0, 1),
            new Coordinate(1, 1),
            new Coordinate(2, 1),
            new Coordinate(0, 0),
            new Coordinate(1, 0),
            new Coordinate(2, 0)
    ), "0123456789", new Coordinate(0, 3));
    public static final Pad DIRECTIONAL = new Pad(new Coordinate(2, 0), List.of(
            new Coordinate(1, 0),
            new Coordinate(0, 1),
            new Coordinate(1, 1),
            new Coordinate(2, 1)
    ), "^<v>", new Coordinate(0, 0));
    private static final int UP = 0;
    private static final int LEFT = 1;
    private static final int DOWN = 2;
    private static final int RIGHT = 3;

    public static String getPush(final String target) {
        return allPushes(target).stream().min(Comparator.comparing(String::length)).orElseThrow();
    }

    //029A: <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
    public static int getShortestPushCount(final String target) {
        final Set<String> firstDirectional = sequences(target, NUMERIC);
        System.err.println("Combinations on inner:" + firstDirectional.size());
        final Set<String> secondDirectional = firstDirectional.stream().map(s2 -> sequences(s2, DIRECTIONAL)).collect(CollectorUtil.coalescingSet());
        System.err.println("Combinations on first directional:" + secondDirectional.size());
        final int minSequence = secondDirectional.stream().mapToInt(s2 -> shortestSequences(s2, DIRECTIONAL)).min().orElseThrow();
// 68
        System.err.println("Shortest sequence at second directional pad is " + minSequence);
        return Math.toIntExact(secondDirectional.stream().filter(s2 -> isSequenceThisLong(s2, DIRECTIONAL, minSequence)).count());
    }

    public static int getShortestPushSize(final String target) {
        final Set<String> firstDirectional = sequences(target, NUMERIC);
        //System.err.println("Combinations on inner:" + firstDirectional.size());
        final Set<String> secondDirectional = firstDirectional.stream().map(s2 -> sequences(s2, DIRECTIONAL)).collect(CollectorUtil.coalescingSet());
        //System.err.println("Combinations on first directional:" + secondDirectional.size());
        final int returnValue = secondDirectional.stream().mapToInt(s2 -> shortestSequences(s2, DIRECTIONAL)).min().orElseThrow();
        System.err.println("Shortest push is " + returnValue + " for " + target);
        return returnValue;
    }

    public static BigInteger getShortestPushSizeTwentyFive(final String target) {
        final Set<String> one = sequences(target, NUMERIC);
        return one.stream().map(single -> {
            Set<Map<MovementPadMovement, BigInteger>> initial = Set.of(Stream.concat(
                            Stream.of("A"),
                            single.chars().mapToObj(Character::toString)
                    ).map(PadKey::fromLetter).gather(Gatherers.windowSliding(2)).map(list -> new MovementPadMovement(list.getFirst(), list.getLast())).collect(CollectorUtil.frequency())
                    .entrySet().stream().map(Functions.onValue(BigInteger::valueOf)).collect(CollectorUtil.toMap()));
            for (int i = 0; i < 2; ++i) {
                initial = iterate(initial);
            }
            return score(initial);
        }).min(BigInteger::compareTo).orElseThrow();
    }

    private static Set<Map<MovementPadMovement, BigInteger>> iterate(final Set<Map<MovementPadMovement, BigInteger>> initial) {
        return initial.stream().map(map -> {
            final Set<Map<MovementPadMovement, BigInteger>> all = new HashSet<>();
            all.add(map.entrySet().stream().map(entry ->
            {
                return MovementPadMovement.MOVE_FREQUENCIES.get(entry.getKey()).stream().min(Comparator.comparing(map4 -> map4.values().stream().reduce(BigInteger::add).orElseThrow())).orElseThrow();
                /*return MovementPadMovement.MOVE_FREQUENCIES.get(entry.getKey()).stream().map(thirdMap->{
                     return thirdMap.entrySet().stream().map(Functions.onValue(entry.getValue()::multiply)).collect(CollectorUtil.toMap());
                });*/
            }).reduce(CollectionUtil::mergeFrequencyMaps).orElseThrow());
            return all;
        }).collect(CollectorUtil.coalescingHashSet());
    }

    private static BigInteger score(final Set<Map<MovementPadMovement, BigInteger>> initial) {
        return initial.stream().map(map -> map.values().stream().reduce(BigInteger::add).orElseThrow()).min(BigInteger::compareTo).orElseThrow();
    }

    private static boolean isSequenceThisLong(final String target, final Pad pad, final int size) {
        final AtomicReference<Coordinate> positionState = new AtomicReference<>(pad.activate());
        return target.chars().mapToObj(x -> (char) x).map(Object::toString).mapToInt(s -> findDistanceToTarget(pad, positionState, s)).sum() == size;
    }

    public static Set<String> allPushes(final String target) {
        final Set<String> firstDirectional = sequences(target, NUMERIC);
        //System.err.println("Combinations on inner:"+firstDirectional.size());
        final Set<String> secondDirectional = firstDirectional.stream().map(s2 -> sequences(s2, DIRECTIONAL)).collect(CollectorUtil.coalescingSet());
        //System.err.println("Combinations on first directional:"+secondDirectional.size());
        final Set<String> result = secondDirectional.stream().map(s2 -> sequences(s2, DIRECTIONAL)).collect(CollectorUtil.coalescingSet());
        //System.err.println("Combinations on second directional:"+result.size());
        return result;
    }

    public static Set<String> sequences(final String target, final Pad pad) {
        final AtomicReference<Coordinate> positionState = new AtomicReference<>(pad.activate());
        final Set<String> returnValue = target.chars().mapToObj(x -> (char) x).map(Object::toString).map(s -> findTarget(pad, positionState, s)).reduce(Pad::cartesianJoins).orElse(Set.of());
        return returnValue;
    }

    public static int shortestSequences(final String target, final Pad pad) {
        final AtomicReference<Coordinate> positionState = new AtomicReference<>(pad.activate());
        final int returnValue = target.chars().mapToObj(x -> (char) x).map(Object::toString).map(s -> findDistanceToTarget(pad, positionState, s)).reduce(Math::addExact).orElse(0);
        //System.err.println("Size of " + target + " is " + returnValue);
        return returnValue;
    }

    public static Set<String> cartesianJoins(final Set<String> a, final Set<String> b) {
        return a.stream().flatMap(start -> {
            return b.stream().map(end -> start + end);
        }).collect(Collectors.toSet());
    }

    public static Set<String> findTarget(final Pad pad, final AtomicReference<Coordinate> positionState, final String target) {
        final Coordinate targetCoordinates = "A".equals(target) ? pad.activate() : pad.digitsOrDirections().get(pad.key().indexOf(target));
        final StringBuilder returnValue = new StringBuilder();
        final AtomicBoolean throughMissing = new AtomicBoolean();
        IntStream.range(0, positionState.get().x() - targetCoordinates.x()).forEach(_ -> {
            returnValue.append("<");
            positionState.getAndUpdate(c -> c.plus(directions[LEFT]));
            if (pad.missing().equals(positionState.get())) {
                throughMissing.set(true);
            }
        });
        IntStream.range(0, positionState.get().y() - targetCoordinates.y()).forEach(_ -> {
            returnValue.append("^");
            positionState.getAndUpdate(c -> c.plus(directions[UP]));
            if (pad.missing().equals(positionState.get())) {
                throughMissing.set(true);
            }
        });
        IntStream.range(0, targetCoordinates.y() - positionState.get().y()).forEach(_ -> {
            returnValue.append("v");
            positionState.getAndUpdate(c -> c.plus(directions[DOWN]));
            if (pad.missing().equals(positionState.get())) {
                throughMissing.set(true);
            }
        });
        IntStream.range(0, targetCoordinates.x() - positionState.get().x()).forEach(_ -> {
            returnValue.append(">");
            positionState.getAndUpdate(c -> c.plus(directions[RIGHT]));
        });
        if (!positionState.get().equals(targetCoordinates)) {
            throw new IllegalStateException();
        }
        Collection<String> strings = permute(returnValue.toString());
        final Set<String> set = StreamSupport.stream(strings.spliterator(), false).map(s -> s + "A").collect(Collectors.toSet());
        if (throughMissing.get()) {
            set.remove(returnValue + "A");
        }
        return StreamSupport.stream(set.spliterator(), false).filter(check -> true).collect(Collectors.toSet());
    }

    public static int findDistanceToTarget(final Pad pad, final AtomicReference<Coordinate> positionState, final String target) {
        final Coordinate targetCoordinates = "A".equals(target) ? pad.activate() : pad.digitsOrDirections().get(pad.key().indexOf(target));
        final Coordinate sourceCoordinates = positionState.getAndSet(targetCoordinates);
        final Coordinate distance = targetCoordinates.minus(sourceCoordinates);
        final int returnValue = distance.manhattanMagnitude() + 1;
        //System.err.println("When finding " + target + ", moves from " + sourceCoordinates + " to " + targetCoordinates + " is " + returnValue+ " because distance is "+distance+" and one press");
        return returnValue;
    }

    public static int countTarget(final Pad pad, final AtomicReference<Coordinate> positionState, final String target) {
        final Coordinate targetCoordinates = "A".equals(target) ? pad.activate() : pad.digitsOrDirections().get(pad.key().indexOf(target));
        final Coordinate sourceCoordinates = positionState.getAndSet(targetCoordinates);
        final Coordinate delta = sourceCoordinates.minus(targetCoordinates);
        final int smallest = min(abs(delta.x()), abs(delta.y()));
        final int total = abs(delta.x()) + abs(delta.y());
        return choose(smallest, total) - ((targetCoordinates.x() == pad.missing().x() && sourceCoordinates.y() == pad.missing().y() || targetCoordinates.y() == pad.missing().y() && sourceCoordinates.x() == pad.missing().x()) ? 1 : 0);
    }

    private static int permuteCount(final String string) {
        final Map<String, Long> letterFrequency = string.chars().mapToObj(Character::toString).collect(CollectorUtil.frequency());
        return choose(Math.toIntExact(StreamSupport.stream(letterFrequency.values().spliterator(), false).min(Long::compareTo).orElseThrow()), string.length());
    }

    private static int choose(final int choices, final int outOf) {
        if (choices * 2 > outOf) {
            return choose(outOf - choices, outOf);
        } else if (choices == 0) {
            return 1;
        }
        final int numerator = IntStream.rangeClosed(1 + outOf - choices, outOf).reduce(Math::multiplyExact).orElse(1);
        final int divisor = IntStream.rangeClosed(1, choices).reduce(Math::multiplyExact).orElse(1);
        if (divisor == 0) {
            throw new ArithmeticException(numerator + "/" + divisor + " is an error for choosing " + choices + " out of " + outOf);
        }
        return numerator / divisor;
    }

    public static Set<String> permute(final String string) {
        final Map<String, Long> letterFrequency = string.chars().mapToObj(Character::toString).collect(CollectorUtil.frequency());
        final Set<String> set;
        if (letterFrequency.size() < 2) {
            set = Set.of(string);
        } else if (letterFrequency.size() > 2) {
            throw new IllegalArgumentException();
        } else {
            if (new ArrayList<>(List.of(2L, 2L)).equals(new ArrayList<>(letterFrequency.values()))) {
                final String[] letters = letterFrequency.keySet().toArray(String[]::new);
                set = IntStream.range(0, 3).boxed().flatMap(firstPosition -> IntStream.range(firstPosition + 1, 4).mapToObj(secondPosition -> {
                    final String[] arrangement = new String[]{letters[0], letters[0], letters[0], letters[0]};
                    arrangement[firstPosition] = letters[1];
                    arrangement[secondPosition] = letters[1];
                    return String.join("", arrangement);
                })).collect(Collectors.toSet());
            } else {
                if (string.length() == 2) {
                    set = Set.of(string, Strings.reverse(string));
                } else {
                    final String single = StreamSupport.stream(((Collection<Map.Entry<String, Long>>) letterFrequency.entrySet()).spliterator(), false).filter(e -> e.getValue() == 1).map(Map.Entry::getKey).collect(CollectorUtil.singleton());
                    final String repeat = StreamSupport.stream(((Collection<String>) letterFrequency.keySet()).spliterator(), false).filter(not(single::equals)).collect(CollectorUtil.singleton());
                    set = IntStream.range(0, string.length()).mapToObj(singlePosition -> {
                        final String[] arrangement = new String[string.length()];
                        Arrays.fill(arrangement, repeat);
                        arrangement[singlePosition] = single;
                        return String.join("", arrangement);
                    }).collect(Collectors.toSet());
                }
            }
        }
        return set;
    }

}
