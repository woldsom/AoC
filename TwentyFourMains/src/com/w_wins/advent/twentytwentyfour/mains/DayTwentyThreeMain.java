package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daytwentythree.Edge;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Streams;
import com.w_wins.common.Graphs;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class DayTwentyThreeMain {
    void main() {
        final Set<Edge> edges = new Utf8ResourceLines(Edge.class, "/input.txt").evaluate(lines -> lines.map(Edge::parseLine).collect(Collectors.toSet()));
        final Map<String, Set<Edge>> edgesByVertex = edges.stream().flatMap(edge -> edge.vertexes().stream().map(Functions.entry(_ -> edge))).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));
        final Map<String, Set<String>> neighbours = edges.stream().flatMap(edge -> edge.vertexes().stream().map(Functions.entry(_ -> edge))).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(e -> e.getValue().other(e.getKey()), Collectors.toSet())));
        final List<String> nodesByConnectivity = edges.stream().flatMap(e -> e.vertexes().stream()).collect(CollectorUtil.frequency()).entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).toList();
        System.out.println(Edge.findTrianglesContainingCount(edges, edgesByVertex, nodesByConnectivity, s -> s.startsWith("t")));
        // not correct System.out.println(new TreeSet<>(Set.of("iy", "ad", "ae", "fj", "qu", "dx", "yp", "ia", "dm", "kw", "ea", "jh")));
        System.out.println(new TreeSet<>(Streams.split("bc, hk, am, wf, gy, xk, th, qf, tj, cz, xo, li, dc", ", ").collect(Collectors.toSet())).stream().collect(Collectors.joining(",")));
        final long start = System.nanoTime();
        System.out.println(Edge.findClique13(edges, edgesByVertex, nodesByConnectivity));
        System.out.println(System.nanoTime()-start);
        final long start2 = System.nanoTime();
        System.out.println("Properly: " + String.join(",", Graphs.largestClique(new HashSet<>(nodesByConnectivity), neighbours::get, (a, b) -> neighbours.get(a).contains(b)).stream().sorted().toList()));
        System.out.println(System.nanoTime()-start2);
    }
}
