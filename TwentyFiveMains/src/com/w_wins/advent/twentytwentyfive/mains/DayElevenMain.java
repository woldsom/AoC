package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayeleven.Paths;
import com.w_wins.collections.BinaryHeap;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Graphs;
import com.w_wins.common.Streams;
import com.w_wins.common.Strings;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DayElevenMain {
    static void main() {
        final Map<String, Set<String>> nameMap = new Utf8ResourceLines(Paths.class, "/input.txt")
                .evaluate(lines -> lines
                        .map(line -> Strings.split(line, ": ").toList())
                        .map(parts ->
                                Map.entry(
                                        parts.getFirst(),
                                        Strings.split(parts.getLast()).collect(Collectors.toSet())
                                )).collect(CollectorUtil.toMap()));
        final List<String> topologicalSorted = Graphs.topologicalSort(nameMap);
        final Map<String, Integer> ranks = Streams.asMap(topologicalSorted.stream())
                .map(Functions.flipEntry()).collect(CollectorUtil.toMap());
        final Map<String, Paths> graph = new HashMap<>(Map.of(topologicalSorted.getFirst(), Paths.getStart()));
        final Map<String, Paths> fromYouGraph = new HashMap<>();
        final BinaryHeap<Integer, String> heap = new BinaryHeap<>(topologicalSorted, ranks::get);
        while (!heap.isEmpty()) {
            final String current = heap.pop();
            if ("you".equals(current)) {
                fromYouGraph.put(current, Paths.getStart());
            }
            final Paths troubleCounter = graph.get(current).visit(current);
            nameMap.get(current).stream().sorted(Comparator.comparing(ranks::get)).forEachOrdered(child -> {
                graph.merge(child, troubleCounter, Paths::add);
                if (fromYouGraph.containsKey(current)) {
                    fromYouGraph.merge(child, fromYouGraph.get(current), Paths::add);
                }
            });
        }
        IO.println("Part 1: " + fromYouGraph.get("out").paths());
        IO.println("Part 2: " + graph.get("out").paths());
    }
}
