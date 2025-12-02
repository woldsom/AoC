package com.w_wins.advent.twentytwentyfour.dayseventeen;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Math;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Constraints(
        Map<Integer, Map<Integer, Map<Integer, Boolean>>> positionToBIndexToBitToXor,
        Map<Integer, Boolean> positionToAValueRequirement) {
    public static Constraints fromMask(final CombinedMask mask, final int bIndex) {
        final Map<Integer, Map<Integer, Map<Integer, Boolean>>> positionToBIndexToBitToXor = new HashMap<>();
        final Map<Integer, Boolean> positionToAValueRequirement = new HashMap<>();
        IntStream.range(0, 3).forEach(bit -> positionToAValueRequirement.put(bIndex * 3 + bit, (mask.a().bits() & Math.pow(2, bit)) > 0));
        IntStream.range(0, 3).forEach(bit -> positionToBIndexToBitToXor.put(bIndex * 3 + mask.b().shift() + bit, new HashMap<>(Map.of(bIndex, Map.of(bit, (mask.b().xorMask() & Math.pow(2, bit)) > 0)))));
        return new Constraints(positionToBIndexToBitToXor, positionToAValueRequirement);
    }

    private static Map<Integer, Map<Integer, Boolean>> copyOuter(final Map<Integer, Map<Integer, Boolean>> map) {
        final HashMap<Integer, Map<Integer, Boolean>> returnValue = new HashMap<>();
        map.forEach((key, value) -> returnValue.put(key, new HashMap<>(value)));
        return returnValue;
    }

    public Optional<SortedMap<Integer, Boolean>> fillMask(List<Integer> b) {
        final SortedMap<Integer, Boolean> returnValue = new TreeMap<>();
        for (int bitIndex = 0; bitIndex < b.size() * 3 + 7; bitIndex++) {
            final Map<Integer, Map<Integer, Boolean>> bIndexToBitToXor = positionToBIndexToBitToXor().getOrDefault(bitIndex, Map.of());
            final Set<Boolean> required = bIndexToBitToXor.entrySet().stream().map(Functions.onKey(b::get)).flatMap(e -> {
                return e.getValue().entrySet().stream().map(Functions.onKey(whatBit -> ((1 << whatBit) & e.getKey()) > 0)).map(bitAndXor -> bitAndXor.getKey() ^ bitAndXor.getValue());
            }).collect(Collectors.toSet());
            Optional.ofNullable(positionToAValueRequirement.get(bitIndex)).ifPresent(required::add);
            if (required.size() > 1) {
                return Optional.empty();
            } else if (!required.isEmpty()) {
                returnValue.put(bitIndex, required.stream().collect(CollectorUtil.singleton()));
            }
        }
        return Optional.of(returnValue);
    }

    public Optional<BigInteger> lowestA(List<Integer> b) {
        final Optional<SortedMap<Integer, Boolean>> partial = fillMask(b);
//        System.out.println(partial);
        return partial.map((SortedMap<Integer, Boolean> map) -> {
            return map.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).map(BigInteger.ONE::shiftLeft).reduce(BigInteger::add).orElse(BigInteger.ZERO);
        });
    }

    public Constraints merge(final Constraints other) throws ConstraintConflictException {
        final Set<ConstraintConflictException> throwing = new HashSet<>();
        final Map<Integer, Map<Integer, Map<Integer, Boolean>>> mergePositionToBIndexToBitToXor = new HashMap<>();
        final Map<Integer, Boolean> mergePositionToAValueRequirement = new HashMap<>(positionToAValueRequirement());
        other.positionToAValueRequirement().forEach((key,value)->{
            final Boolean mergeResult = mergePositionToAValueRequirement.put(key, value);
            if(mergeResult!=null && mergeResult.equals(value)) {
                throwing.add(new ConstraintConflictException("Can't merge conflicting A's"));
            }
        });
        Stream.concat(positionToBIndexToBitToXor().keySet().stream(), other.positionToBIndexToBitToXor().keySet().stream()).forEach(position -> {
            if (positionToBIndexToBitToXor().containsKey(position) && other.positionToBIndexToBitToXor().containsKey(position)) {
                final Map<Integer, Map<Integer, Boolean>> newMap = copyOuter(positionToBIndexToBitToXor().get(position));
                final Map<Integer, Map<Integer, Boolean>> otherValue = other.positionToBIndexToBitToXor().get(position);
                try {
                    merge(newMap, otherValue);
                } catch (
                        ConstraintConflictException e) {
                    throwing.add(e);
                }
            } else if (positionToBIndexToBitToXor().containsKey(position)) {
                mergePositionToBIndexToBitToXor.put(position, copyOuter(positionToBIndexToBitToXor().get(position)));
            } else {
                mergePositionToBIndexToBitToXor.put(position, copyOuter(other.positionToBIndexToBitToXor().get(position)));
            }
        });
        if (!throwing.isEmpty()) {
            throw throwing.stream().findAny().orElseThrow();
        }
        final Constraints returnValue = new Constraints(mergePositionToBIndexToBitToXor, mergePositionToAValueRequirement);
        //System.err.println("Merged "+this+"\n and "+other+"\n resulting in "+returnValue);
        return returnValue;
    }

    private void merge(final Map<Integer, Map<Integer, Boolean>> newMap, final Map<Integer, Map<Integer, Boolean>> other) throws ConstraintConflictException {
        final Set<ConstraintConflictException> throwing = new HashSet<>();
        Stream.concat(newMap.keySet().stream(), other.keySet().stream()).forEach(bIndex -> {
            if (newMap.containsKey(bIndex) && other.containsKey(bIndex)) {
                final Map<Integer, Boolean> newMap2 = new HashMap<>(newMap.get(bIndex));
                if (other.get(bIndex).keySet().stream().anyMatch(newMap2::containsKey)) {
                    throwing.add(new ConstraintConflictException("Same position, bIndex, and at bIndex " + bIndex + " when merging " + newMap + " and " + other));
                } else {
                    newMap2.putAll(other.get(bIndex));
                }
            } else if (!newMap.containsKey(bIndex)) {
                final Map<Integer, Boolean> map = other.get(bIndex);
                newMap.put(bIndex, new HashMap<>(map));
            }
        });
        if (!throwing.isEmpty()) {
            throw throwing.stream().findAny().orElseThrow();
        }
    }

    public Constraints copy() {
        final Map<Integer, Map<Integer, Map<Integer, Boolean>>> newPositionToBIndexToBitToXor = new HashMap<>();
        final Map<Integer, Boolean> newPositionToAValueRequirement = new HashMap<>(positionToAValueRequirement());
        positionToBIndexToBitToXor().forEach((key,value)->{
            newPositionToBIndexToBitToXor.put(key,copyOuter(value));
        });
        return new Constraints(newPositionToBIndexToBitToXor, newPositionToAValueRequirement);
    }
}
