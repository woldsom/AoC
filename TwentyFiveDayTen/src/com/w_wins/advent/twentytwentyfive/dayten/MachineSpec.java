package com.w_wins.advent.twentytwentyfive.dayten;

import com.w_wins.common.BitStream;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Streams;
import com.w_wins.common.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.powExact;

public record MachineSpec(List<Boolean> targetLights, Set<Set<Integer>> buttonwiring, List<Integer> joltageNeeded, Map<MachineArguments, OptionalInt> memo) {
    private static final Comparator<OptionalInt> COMPARATOR = Comparator.comparing(o -> o.orElse(Integer.MAX_VALUE));

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
            //IO.println("Memo");
            return memo().get(key);
        }
        final OptionalInt returnValue = pressesToJoltNoMemo(integers, buttons);
        //IO.println("Result was " + returnValue);
        memo().put(key, returnValue);
        //IO.println(memo().size());
        return returnValue;
    }

    private OptionalInt pressesToJoltNoMemo(final List<Integer> joltTarget, final Set<Set<Integer>> buttons) {
        //IO.println("Called pressesToJolt " + joltTarget + " with " + buttons);
        if (joltTarget.stream().allMatch(x -> x == 0)) {
            return OptionalInt.of(0);
        }
        final Map<Integer, Integer> frequencies = buttons.stream().flatMap(set -> set.stream().map(i -> Map.entry(i, 1))).collect(CollectorUtil.toMap(Collectors.<Integer>summingInt(x -> x)));
        if (Streams.asMap(joltTarget.stream()).anyMatch(e -> e.getValue() > 0 && frequencies.getOrDefault(e.getKey(), 0) <= 0)) {
            //IO.println("Impossible to reduce "+joltTarget+" with "+buttons);
            return OptionalInt.empty();
        }
        final Map<Integer, Integer> forced = Streams.asMap(joltTarget.stream()).filter(e -> frequencies.getOrDefault(e.getKey(), 0) == 1).collect(CollectorUtil.toMap());
        if (!forced.isEmpty()) {
            final int toForce = forced.keySet().stream().findAny().orElseThrow();
            final Set<Integer> onlyButton = buttons.stream().filter(button -> button.contains(toForce)).findAny().orElseThrow();
            final List<Integer> newTarget = new ArrayList<>(joltTarget);
            final int times = joltTarget.get(toForce);
            for (final Integer el : onlyButton) {
                if (newTarget.get(el) < times) {
                    return OptionalInt.empty();
                } else {
                    newTarget.set(el, newTarget.get(el) - times);
                }
            }
            //IO.println("Pressed mandatory button "+onlyButton);
            final OptionalInt result = pressesToJolt(newTarget, subList(buttons, onlyButton));
            return result.isPresent() ? OptionalInt.of(result.getAsInt() + times) : result;
        }
        final OptionalInt resultValue = buttons.stream().map(jolts -> {
            final int maxTimes = jolts.stream().mapToInt(joltTarget::get).min().orElseThrow();
            if (maxTimes == 0) {
                return OptionalInt.empty();
            }
            final int minTimes = 0;

            return IntStream.iterate(maxTimes, x -> x > 0, x -> x - 1).mapToObj(times -> {
                final List<Integer> joltResult = Streams.asMap(joltTarget.stream()).map(entry -> jolts.contains(entry.getKey()) ? entry.getValue() - times : entry.getValue()).toList();
                final Set<Set<Integer>> remainingButtons = subList(buttons, jolts);
                //IO.println("Recurring with " + joltResult + " and " + remainingButtons + " due to " + times + " presses");
                final OptionalInt pressesResult = pressesToJolt(joltResult, remainingButtons);
                //IO.println("Recurred returned " + pressesResult);
                return pressesResult.isPresent() ? OptionalInt.of(pressesResult.getAsInt() + times) : OptionalInt.empty();
            }).min(COMPARATOR).orElse(OptionalInt.empty());
        }).min(COMPARATOR).orElse(OptionalInt.empty());
        return resultValue;
    }

    private Set<Set<Integer>> subList(final Set<Set<Integer>> buttons, final Set<Integer> usedButton) {
        final Set<Set<Integer>> modList = new HashSet<>(buttons);
        if (!modList.remove(usedButton)) {
            throw new IllegalArgumentException(buttons + " did not contain " + usedButton);
        }
        return modList;
    }

    public String stats() {
        final int finalSum = joltageNeeded().stream().mapToInt(x -> x).sum();
        final AtomicInteger sum = new AtomicInteger(finalSum);
        final AtomicInteger lBound = new AtomicInteger();
        buttonwiring().stream().map(s -> Map.entry(s.stream().mapToInt(joltageNeeded()::get).min().orElse(0) * s.size(), s.size())).sorted(Map.Entry.<Integer, Integer>comparingByKey().reversed()).forEachOrdered(entry -> {
            final IntBinaryOperator sub = (x, y) -> x - y;
            if (sum.get() > entry.getKey()) {
                sum.accumulateAndGet(entry.getKey(), sub);
                lBound.accumulateAndGet(entry.getValue(), Integer::sum);
            } else if (sum.get() > entry.getKey() / entry.getValue()) {
                final int count = sum.get() / (entry.getKey() / entry.getValue());
                sum.accumulateAndGet(count * entry.getKey() / entry.getValue(), sub);
                lBound.accumulateAndGet(count, Integer::sum);
            }
        });
        final int uBound = finalSum / buttonwiring().stream().mapToInt(Set::size).min().orElseThrow();
        return "Low bound: " + lBound + ", high bound: "+uBound;
    }

    public String stats2() {
        final SortedSet<Set<Integer>> virtualButtons = new TreeSet<>(Comparator.comparing(Set::size));
        virtualButtons.addAll(buttonwiring());
        final List<Set<Integer>> buttons = new ArrayList<>(buttonwiring());
        IntStream.range(0, powExact(2, buttons.size())).mapToObj(index -> Streams.present(Streams.zip(BitStream.decodeRecurse(index, this.buttonwiring().size()).boxed(), this.buttonwiring().stream(), (onOff, button) -> onOff > 0 ? Optional.of(button) : Optional.empty())).collect(Collectors.toSet()))
                .forEach(sets -> {
                    final Map<Integer, Long> freq = sets.stream().flatMap(Collection::stream).collect(Collectors.groupingBy(x -> x, Collectors.counting()));
                    final long mostFrequent = freq.values().stream().mapToLong(x -> x).max().orElse(Long.MAX_VALUE);
                    if (mostFrequent < 3) {
                        if (mostFrequent == 1) {
                            virtualButtons.add(freq.keySet());
                        } else if (mostFrequent == 2) {
                            sets.stream().filter(set -> sets.stream().allMatch(o -> o.containsAll(set))).forEach(subtract -> {
                                final Set<Integer> virtual = new HashSet<>(freq.keySet());
                                virtual.removeAll(subtract);
                                if (virtual.stream().allMatch(e -> freq.get(e) == 1)) {
                                    virtualButtons.add(virtual);
                                }
                            });
                        }
                    }
                });
        return "Best virtual buttons: " + virtualButtons.stream().limit(3).toList() + " ( for " + this.toPretty() + ")";
    }

    public Map<Set<Set<Integer>>, Integer> equations() {
        return Streams.asMap(joltageNeeded().stream()).map(Functions.onKey(index -> buttonwiring().stream().filter(s -> s.contains(index)).collect(Collectors.toSet()))).collect(CollectorUtil.toMap());
    }

    public String toPretty() {
        return "MachineSpec{" +
                "joltageNeeded=" + joltageNeeded +
                ", buttonwiring=" + buttonwiring +
                ", targetLights=" + targetLights +
                '}';
    }

    public int solve() {
        return 0;
    }
}
