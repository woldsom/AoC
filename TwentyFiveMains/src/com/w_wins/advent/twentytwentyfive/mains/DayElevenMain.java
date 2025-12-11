package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayeleven.Device;
import com.w_wins.advent.twentytwentyfive.dayeleven.Paths;
import com.w_wins.collections.BinaryHeap;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Graphs;
import com.w_wins.common.Streams;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

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
                    },
                    Paths::add));
        }
        IO.println("Part 2: " + graph.get(devices.get("out")).both());
    }
}
