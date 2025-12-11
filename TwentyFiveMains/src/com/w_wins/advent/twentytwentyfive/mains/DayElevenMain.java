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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class DayElevenMain {
    static void main() {
        final Map<String, Set<String>> nameMap = new Utf8ResourceLines(Device.class, "/input.txt").evaluate(lines -> lines.map(Device::parse).collect(CollectorUtil.toMap()));
        final Map<String, Device> devices = Device.deviceMap(nameMap);
        final List<String> topologicalSorted = Graphs.topologicalSort(nameMap);
        IO.println(topologicalSorted);
        final Map<String, Integer> ranks = Streams.asMap(topologicalSorted.stream()).map(Functions.flipEntry()).collect(CollectorUtil.toMap());
        final Map<Device, Paths> graph = new HashMap<>(Map.of(devices.get(topologicalSorted.getFirst()), Paths.getStart()));
        final Map<Device, Paths> youGraph = new HashMap<>();
        final Function<Device, Integer> order = device -> ranks.get(device.label());
        if (!devices.values().stream().map(order).allMatch(Objects::nonNull)) {
            throw new IllegalArgumentException();
        }
        final BinaryHeap<Integer, Device> heap = new BinaryHeap<>(devices.values(), order);
        while (!heap.isEmpty()) {
            final Device current = heap.pop();
            if ("you".equals(current.label())) {
                youGraph.put(current, Paths.getStart());
            }
            current.outputs().stream().sorted(Comparator.comparing(order)).forEachOrdered(child -> {
                graph.merge(child, Paths.howPathsAreAffectedAt(current.label()).apply(graph.get(current)), Paths::add);
                if (youGraph.containsKey(current)) {
                    youGraph.merge(child, youGraph.get(current), Paths::add);
                }
            });
        }
        IO.println("Part 1: " + youGraph.get(devices.get("out")).sum());
        IO.println("Part 2: " + graph.get(devices.get("out")).both());
    }
}
