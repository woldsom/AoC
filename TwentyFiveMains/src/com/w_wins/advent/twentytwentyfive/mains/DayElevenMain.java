package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayeleven.Device;
import com.w_wins.advent.twentytwentyfive.dayeleven.Paths;
import com.w_wins.advent.twentytwentyfive.dayeleven.Shortcut;
import com.w_wins.collections.BinaryHeap;
import com.w_wins.common.CollectionUtil;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Graphs;
import com.w_wins.common.Streams;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DayElevenMain {
    static void main() {
        final Map<String, Set<String>> nameMap = new Utf8ResourceLines(Device.class, "/input.txt").evaluate(lines -> lines.map(Device::parse).collect(CollectorUtil.toMap()));
        final Set<Device> leaves = new HashSet<>();
        final Map<String, Device> devices = new HashMap<>(nameMap.keySet().stream().map(Functions.entry(Device::ofName)).collect(CollectorUtil.toMap()));
        devices.forEach((name, device) -> {
            nameMap.get(name).stream().map(k -> {
                final Device child = devices.get(k);
                if (child == null) {
                    final Device leaf = Device.ofName(k);
                    leaves.add(leaf);
                    return leaf;
                }
                return child;
            }).forEach(e -> {
                device.outputs().add(e);
            });
        });
        leaves.forEach(leaf -> {
            devices.put(leaf.label(), leaf);
            nameMap.put(leaf.label(), new HashSet<>());
        });
        IO.println("Part 1: " + devices.get("you").pathsTo("out"));
        final List<String> topologicalSorted = Graphs.topologicalSort(nameMap);
        IO.println(topologicalSorted);
        final Map<String, Integer> ranks = Streams.asMap(topologicalSorted.stream()).map(e -> Map.entry(e.getValue(), e.getKey())).collect(CollectorUtil.toMap());
        final Map<Device, Paths> graph = new HashMap<>(Map.of(devices.get(topologicalSorted.getFirst()), Paths.getStart()));
        final Function<Device, Integer> order = device -> ranks.get(device.label());
        if (!devices.values().stream().map(order).allMatch(Objects::nonNull)) {
            throw new IllegalArgumentException();
        }
        final BinaryHeap<Integer, Device> heap = new BinaryHeap<>(devices.values(), order);
        while (!heap.isEmpty()) {
            final Device current = heap.pop();
            final Paths paths = graph.get(current);
            current.outputs().stream().sorted(Comparator.comparing(order)).forEachOrdered(child -> graph.merge(child,
                    switch (current.label()) {
                        case "dac" -> paths.butDac();
                        case "fft" -> paths.butFft();
                        default -> paths;
                    }
                    , Paths::add));
        }
        IO.println("Part 2: " + graph.get(devices.get("out")).both());
    }

    private static void dumb(final Map<String, Device> devices) {
        final List<Device> devicesList = new ArrayList<>(devices.values());
        final Set<Shortcut> shortcuts = CollectionUtil.selfCross(devicesList, Shortcut::new).<Shortcut>mapMulti((shortcut, downstream) -> {
            downstream.accept(shortcut);
            downstream.accept(shortcut.reversed());
        }).collect(Collectors.toSet());
        IO.println(shortcuts.size() + " shortcuts");
        final Map<Shortcut, OptionalLong> shortcutCosts = shortcuts.stream().map(Functions.entry(s -> s.start().pathsToMax(s.end().label(), 10, 10))).collect(CollectorUtil.toMap());
        final Map<Device, Map<Device, List<OptionalLong>>> startCost2 = shortcutCosts.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().start(), Collectors.groupingBy(e -> e.getKey().end(), Collectors.mapping(Map.Entry::getValue, Collectors.toList()))));
        final Map<Device, Map<Device, OptionalLong>> startCost = startCost2.entrySet().stream().map(Functions.onValue(v -> v.entrySet().stream().map(Functions.onValue(l -> {
            if (l.size() != 1) {
                throw new IllegalArgumentException("Wrong list " + l + " for " + v);
            }
            return l.getFirst();
        })).collect(CollectorUtil.toMap()))).collect(CollectorUtil.toMap());
        final Map<Device, Map<Device, List<OptionalLong>>> endCost2 = shortcutCosts.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().end(), Collectors.groupingBy(e -> e.getKey().start(), Collectors.mapping(Map.Entry::getValue, Collectors.toList()))));
        final Map<Device, Map<Device, OptionalLong>> endCost = endCost2.entrySet().stream().map(Functions.onValue(v -> v.entrySet().stream().map(Functions.onValue(l -> {
            if (l.size() != 1) {
                throw new IllegalArgumentException("Wrong list " + l + " for " + v);
            }
            return l.getFirst();
        })).collect(CollectorUtil.toMap()))).collect(CollectorUtil.toMap());
        IO.println(shortcutCosts.size());
        final Random random = new Random();
        while (
                shortcutCosts.get(new Shortcut(devices.get("you"), devices.get("out"))).isEmpty() &&
                        shortcutCosts.get(new Shortcut(devices.get("svr"), devices.get("fft"))).isEmpty() &&
                        shortcutCosts.get(new Shortcut(devices.get("svr"), devices.get("dac"))).isEmpty() &&
                        shortcutCosts.get(new Shortcut(devices.get("dac"), devices.get("out"))).isEmpty() &&
                        shortcutCosts.get(new Shortcut(devices.get("fft"), devices.get("out"))).isEmpty()

        ) {
            final List<Shortcut> emptyList = shortcutCosts.entrySet().stream().filter(e -> e.getValue().isEmpty()).map(Map.Entry::getKey).toList();
            //final Shortcut target = emptyList.get(random.nextInt(emptyList.size()));
            final Shortcut target = emptyList.stream().filter(candidate -> startCost.get(candidate.start()).values().stream().allMatch(OptionalLong::isPresent) && endCost.get(candidate.end()).values().stream().allMatch(OptionalLong::isPresent)).findAny().orElseThrow();
            final Map<Device, OptionalLong> from = startCost.get(target.start());
            final Map<Device, OptionalLong> to = endCost.get(target.end());
            if (from.entrySet().stream().filter(e -> !e.getKey().equals(target.end())).map(Map.Entry::getValue).allMatch(OptionalLong::isPresent) && to.entrySet().stream().filter(e -> !e.getKey().equals(target.start())).map(Map.Entry::getValue).allMatch(OptionalLong::isPresent)) {
                final Set<Device> intersection = CollectionUtil.intersection(from.keySet(), to.keySet());
                if (intersection.size() != from.size() - 1 || intersection.size() != to.size() - 1) {
                    throw new IllegalArgumentException("From: " + from.size() + ", to: " + to.size() + ", intersection: " + intersection.size());
                }
                final long newCount = from.entrySet().stream().filter(e -> !e.getKey().equals(target.end())).map(Map.Entry::getValue).mapToLong(start ->
                        Math.multiplyExact(to.entrySet().stream().filter(e -> !e.getKey().equals(target.start())).map(Map.Entry::getValue).mapToLong(OptionalLong::orElseThrow).sum(), start.orElseThrow())
                ).sum();
                final OptionalLong value = OptionalLong.of(newCount);
                shortcutCosts.put(target, value);
                startCost.get(target.start()).put(target.end(), value);
                endCost.get(target.end()).put(target.start(), value);
                IO.println("Remaining: " + emptyList.size());
            }
        }
    }
}
