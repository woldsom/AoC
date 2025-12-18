package com.w_wins.advent.twentytwentyfive.dayten;

import com.w_wins.common.CollectionUtil;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Streams;
import com.w_wins.common.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.powExact;

public record MachineSpec(List<Boolean> targetLights, Set<Set<Integer>> buttonwiring, List<Integer> joltageNeeded, Map<MachineArguments, OptionalInt> memo) {
    public static MachineSpec parse(String line, final HashMap<MachineArguments, OptionalInt> setMemo) {
        final List<String> sections = Strings.split(line).toList();
        return new MachineSpec(parseLights(sections.getFirst()), parseWiring(sections.subList(1, sections.size() - 1)), parseJoltage(sections.getLast()), setMemo);
    }

    private static List<Integer> parseJoltage(final String joltageSpec) {
        return Strings.split(joltageSpec.substring(1, joltageSpec.length() - 1), ",").map(Integer::parseInt).toList();
    }

    private static Set<Set<Integer>> parseWiring(final List<String> parts) {
        return parts.stream().map(part -> parseSingleWiring(part.substring(1, part.length() - 1))).collect(Collectors.toSet());
    }

    private static Set<Integer> parseSingleWiring(final String wiring) {
        return Strings.split(wiring, ",").map(Integer::parseInt).collect(Collectors.toSet());
    }

    private static List<Boolean> parseLights(final String lights) {
        return Strings.characters(lights).filter(c -> c.equals('.') || c.equals('#')).map(c -> c.equals('#')).toList();
    }

    public int pressesToLight() {
        return Streams.presentInt(IntStream.range(0, powExact(2, buttonwiring().size())).mapToObj(Integer::toBinaryString).map(s -> Strings.padLeft(s, "0", buttonwiring().size())).map(string ->
                Streams.asMap(Strings.characters(string)).filter(e -> e.getValue().equals('1')).map(Map.Entry::getKey).toList()
        ).map(bitField -> {
            final List<Boolean> lights = new ArrayList<>(targetLights());
            if (!bitField.isEmpty()) {
                if (buttonwiring().size() <= bitField.stream().mapToInt(x -> x).max().orElseThrow()) {
                    throw new IllegalArgumentException("Field too large, lights was " + lights + " and field is " + bitField);
                }
                final List<Set<Integer>> buttonList = new ArrayList<>(buttonwiring());
                bitField.forEach(index -> {
                    buttonList.get(index).forEach(button -> {
                        lights.set(button, !lights.get(button));
                    });
                });
            }
            if (lights.stream().noneMatch(x -> x)) {
                return OptionalInt.of(bitField.size());
            } else {
                return OptionalInt.empty();
            }
        })).min().orElseThrow();
    }

    public int pressesToJolt() {
        return pressesToJolt(joltageNeeded(), buttonwiring()).orElseThrow();
    }

    private OptionalInt pressesToJolt(final List<Integer> integers, final Set<Set<Integer>> buttons) {
        final MachineArguments key = new MachineArguments(integers, buttons);
        if (memo().containsKey(key)) {
            return memo().get(key);
        }
        final OptionalInt returnValue = pressesToJoltNoMemo(integers, buttons);
        memo().put(key, returnValue);
        return returnValue;
    }

    private OptionalInt pressesToJoltNoMemo(final List<Integer> joltTarget, final Set<Set<Integer>> buttons) {
        if (joltTarget.stream().allMatch(x -> x == 0)) {
            return OptionalInt.of(0);
        }
        return CollectionUtil.subsets(buttons).<Integer>mapMulti((subset, downstream) -> {
            final ArrayList<Integer> targetResult = new ArrayList<>(joltTarget);
            subset.forEach(button -> button.forEach(field -> targetResult.set(field, targetResult.get(field) - 1)));
            if (targetResult.stream().allMatch(n -> n % 2 == 0 && n >= 0)) {
                targetResult.replaceAll(x -> x / 2);
                OptionalInt recursed = pressesToJolt(targetResult, buttons).stream().map(x -> x * 2 + subset.size()).min();
                recursed.ifPresent(downstream::accept);
            }
        }).mapToInt(x -> x).min();
    }

    public Map<Set<Set<Integer>>, Integer> equations() {
        return Streams.asMap(joltageNeeded().stream()).map(Functions.onKey(index -> buttonwiring().stream().filter(s -> s.contains(index)).collect(Collectors.toSet()))).collect(CollectorUtil.toMap());
    }
}
