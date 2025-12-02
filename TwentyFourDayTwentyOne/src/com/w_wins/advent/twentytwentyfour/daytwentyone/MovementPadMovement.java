package com.w_wins.advent.twentytwentyfour.daytwentyone;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

public record MovementPadMovement(
        PadKey lastPress,
        PadKey toPress) {
    // map from robot 1's wanted movement to robot 2's movement's frequencies
    public static final Map<MovementPadMovement, Set<Map<MovementPadMovement, BigInteger>>> MOVE_FREQUENCIES = PadKey.stream().flatMap(lastPress -> PadKey.stream().map(toPress -> new MovementPadMovement(lastPress, toPress))).map(Functions.entry(key -> findShortestPermutations(key))).collect(CollectorUtil.toMap());

    // robot 1 wants to move ofMovement and then press it, which is
    // several step movements, what movements does robot 2 have to make, including presses for each,
    // to make it happen, given they start out over the activate key?
    public static Set<Map<MovementPadMovement, BigInteger>> findShortestPermutations(final MovementPadMovement ofMovement) {
        final Map<Coordinate, Integer> relativeFrequencies = ofMovement.relative().countComponents();
        final Map<MovementPadMovement, BigInteger> set;
        if (relativeFrequencies.isEmpty()) {
            return Set.of(Map.of(justPress(PadKey.ACTIVATE), BigInteger.ONE));
        } else if (relativeFrequencies.size() < 2) {
            final Map.Entry<Coordinate, Integer> soleEntry = relativeFrequencies.entrySet().stream().collect(CollectorUtil.singleton());
            final PadKey key = PadKey.fromMovement(soleEntry.getKey());
            final Map<MovementPadMovement, BigInteger> sides = Map.of(new MovementPadMovement(PadKey.ACTIVATE, key), BigInteger.ONE, new MovementPadMovement(key, PadKey.ACTIVATE), BigInteger.ONE);
            if (soleEntry.getValue() == 1) {
                return Set.of(sides);
            } else {
                final Map<MovementPadMovement, BigInteger> returnValue = new HashMap<>(sides);
                returnValue.put(new MovementPadMovement(key, key), BigInteger.valueOf(soleEntry.getValue() - 1));
                return Set.of(returnValue);
            }
        } else if (relativeFrequencies.size() > 2) {
            throw new IllegalArgumentException();
        } else {
            final int keyPresses = relativeFrequencies.values().stream().mapToInt(x -> x).sum();
            final Coordinate startCoordinate = ofMovement.lastPress().relativeToA();
            if (keyPresses == 2) {
                final List<Coordinate> options = new ArrayList<>(relativeFrequencies.keySet());
                final List<PadKey> asKeys = options.stream().map(PadKey::fromMovement).toList();
                final Map<MovementPadMovement, BigInteger> lastFirst = Map.of(new MovementPadMovement(PadKey.ACTIVATE, asKeys.getLast()), BigInteger.ONE, new MovementPadMovement(asKeys.getLast(), asKeys.getFirst()), BigInteger.ONE, new MovementPadMovement(asKeys.getFirst(), PadKey.ACTIVATE), BigInteger.ONE);
                final Map<MovementPadMovement, BigInteger> firstFirst = Map.of(new MovementPadMovement(PadKey.ACTIVATE, asKeys.getFirst()), BigInteger.ONE, new MovementPadMovement(asKeys.getFirst(), asKeys.getLast()), BigInteger.ONE, new MovementPadMovement(asKeys.getLast(), PadKey.ACTIVATE), BigInteger.ONE);
                if (options.getFirst().plus(startCoordinate).equals(PadKey.getMissing())) {
                    return Set.of(lastFirst);
                } else if (options.getFirst().plus(startCoordinate).equals(PadKey.getMissing())) {
                    return Set.of(firstFirst);
                } else {
                    return Set.of(firstFirst,lastFirst);
                }
            } else {
                final PadKey single = PadKey.fromMovement(relativeFrequencies.entrySet().stream().filter(e -> e.getValue() == 1).map(Map.Entry::getKey).collect(CollectorUtil.singleton()));
                final PadKey repeat = PadKey.fromMovement(relativeFrequencies.keySet().stream().filter(not(single.movement()::equals)).collect(CollectorUtil.singleton()));
                final BiConsumer<Integer, Consumer<? super PadKey[]>> lambda = (Integer singlePosition, Consumer<? super PadKey[]> consumer) -> {
                    final PadKey[] arrangement = new PadKey[keyPresses + 2];
                    Arrays.fill(arrangement, repeat);
                    arrangement[0] = PadKey.ACTIVATE;
                    arrangement[arrangement.length - 1] = PadKey.ACTIVATE;
                    arrangement[singlePosition + 1] = single;
                    //System.err.println("Arrangement is "+Arrays.toString(arrangement)+" for "+ofMovement);
                    final AtomicReference<Coordinate> current = new AtomicReference<>(startCoordinate);
                    if (Arrays.stream(arrangement).map(PadKey::movement).map(a -> current.updateAndGet(a::plus)).allMatch(not(PadKey.getMissing()::equals))) {
                        if (!arrangement[1].equals(arrangement[3])) {
                            consumer.accept(arrangement);
                        }
                    }
                }; // Idea gets very confused about this lambda
                //System.err.println("Was there an arrangement?");
                return IntStream.range(0, keyPresses).boxed().mapMulti(lambda).map(array -> Arrays.stream(array).gather(Gatherers.windowSliding(2)).map(list -> new MovementPadMovement(list.getFirst(), list.getLast())).collect(CollectorUtil.frequency(BigInteger::valueOf)))
                        .collect(Collectors.toSet());
            }
        }
    }

    public static MovementPadMovement justPress(final PadKey key) {
        return new MovementPadMovement(key, key);
    }

    public int distance() {
        return relative().manhattanMagnitude();
    }

    public Coordinate relative() {
        return toPress().relativeToA().minus(lastPress().relativeToA());
    }

}
