package com.w_wins.advent.twentytwentyfour.daytwentythree;

import com.w_wins.common.AtomicBest;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Streams;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public record Edge(
        Set<String> vertexes) {
    public static Edge parseLine(String line) {
        return new Edge(Streams.split(line, "-").collect(Collectors.toSet()));
    }

    public static long findTrianglesContainingCount(final Set<Edge> edges, final Map<String, Set<Edge>> edgesByVertex, final List<String> nodesByConnectivity, final Predicate<String> test) {
        return streamTrianglesContaining(edges, edgesByVertex, nodesByConnectivity, test).count();
    }

    public static Set<Set<String>> findTrianglesContaining(final Set<Edge> edges, final Map<String, Set<Edge>> edgesByVertex, final List<String> nodesByConnectivity, final Predicate<String> test) {
        return streamTrianglesContaining(edges, edgesByVertex, nodesByConnectivity, test).collect(Collectors.toSet());
    }

    public static Stream<Set<String>> streamTrianglesContaining(final Set<Edge> edges, final Map<String, Set<Edge>> edgesByVertex, final List<String> nodesByConnectivity, final Predicate<String> test) {
        final Set<String> done = new HashSet<>();
        final Stream<Set<String>> triangleStream = nodesByConnectivity.stream().mapMulti((node, collector) -> {
            final SortedSet<String> neighbours = edgesByVertex.get(node).stream().map(firstsEdges -> minus(firstsEdges, node)).filter(not(done::contains)).collect(Collectors.toCollection(TreeSet::new));
            neighbours.forEach(first -> neighbours.headSet(first).forEach(second -> {
                final Edge edge = new Edge(Set.of(first, second));
                if (edges.contains(edge)) {
                    final Set<String> triangle = new HashSet<>(edge.vertexes());
                    triangle.add(node);
                    collector.accept(triangle);
                }
            }));
            done.add(node);
        });
        return triangleStream.filter(v -> v.stream().anyMatch(test));
    }

    private static String minus(final Edge edge, final String oneNode) {
        return edge.vertexes().stream().filter(not(oneNode::equals)).collect(CollectorUtil.singleton());
    }

    public static Set<String> findClique(final Set<Edge> edges, final Map<String, Set<Edge>> edgesByVertex, final List<String> nodesByConnectivity, final int size) {
        final AtomicBest<Set<String>> bestClique = new AtomicBest<>();
        bestClique(bestClique, edges.stream().map(Edge::vertexes).collect(Collectors.toSet()), edgesByVertex);
        return bestClique.consumeAndReturnValue().orElseThrow();
    }

    private static void bestClique(final AtomicBest<Set<String>> bestClique, final Set<Set<String>> cliquesSoFar, final Map<String, Set<Edge>> edgesByVertex) {
        cliquesSoFar.forEach(c -> expand(bestClique, c, edgesByVertex));
    }

    private static void expand(final AtomicBest<Set<String>> bestClique, final Set<String> clique, final Map<String, Set<Edge>> edgesByVertex) {
        final Set<String> candidates = clique.stream().flatMap(node -> Stream.concat(Stream.of(node), edgesByVertex.get(node).stream().map(edge -> minus(edge, node))).filter(not(clique::contains))).collect(Collectors.toSet());
        final Set<String> finalCandidates = candidates.stream().filter(node -> edgesByVertex.get(node).stream().map(edge -> minus(edge, node)).collect(Collectors.toSet()).containsAll(clique)).collect(Collectors.toSet());
        if (finalCandidates.isEmpty()) {
            if (bestClique.isBest(clique.size(), clique)) {
                System.err.println("Best so far: " + clique.size() + " (" + clique + ")");
            }
        } else {
            finalCandidates.forEach(candidate -> {
                final HashSet<String> newClique = new HashSet<>(clique);
                newClique.add(candidate);
                expand(bestClique, newClique, edgesByVertex);
            });
        }
    }

    public static String findClique13(final Set<Edge> edges, final Map<String, Set<Edge>> edgesByVertex, final List<String> nodesByConnectivity) {
        final Map<String, Set<String>> neighbours = edgesByVertex.entrySet().stream().map(e -> Functions.<String, Set<Edge>, Set<String>>onValue(v -> v.stream().map(m -> minus(m, e.getKey())).collect(Collectors.toSet())).apply(e)).collect(CollectorUtil.toMap());
        final Set<Set<String>> potentialCliques = neighbours.entrySet().stream().flatMap(e -> e.getValue().stream().map(removed -> replace(e.getValue(), removed, e.getKey()))).collect(Collectors.toSet());
        return String.join(",", potentialCliques.stream().filter(s -> isClique(neighbours, s)).collect(CollectorUtil.singleton()).stream().sorted().toList());
    }

    private static boolean isClique(final Map<String, Set<String>> neighbours, final Set<String> candidate) {
        return candidate.stream().allMatch(node -> neighbours.get(node).containsAll(minus(candidate, node)));
    }

    private static Set<String> replace(final Set<String> original, final String removed, final String added) {
        final HashSet<String> newSet = new HashSet<>(original);
        newSet.remove(removed);
        newSet.add(added);
        return newSet;
    }

    private static Set<String> plus(final Set<String> original, final String key) {
        final HashSet<String> returnValue = new HashSet<>(original);
        returnValue.add(key);
        return returnValue;
    }

    private static Set<String> minus(final Set<String> clique, final String removed) {
        final HashSet<String> returnValue = new HashSet<>(clique);
        returnValue.remove(removed);
        return returnValue;
    }

    public String other(final String key) {
        return vertexes().stream().filter(not(key::equals)).collect(CollectorUtil.singleton());
    }
}
