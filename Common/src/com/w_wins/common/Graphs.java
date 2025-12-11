package com.w_wins.common;

import com.w_wins.collections.BinaryHeap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public final class Graphs {
    public static <T> Set<T> largestClique(final Set<T> vertexes, final Function<T, Set<T>> neighbours, final BiPredicate<T, T> isNeighbours) {
        final BinaryHeap<Integer, T> heap = BinaryHeap.build(vertexes, (Function<T, Integer>) neighbours.andThen(Set::size));
        final AtomicBest<Set<T>> largestClique = new AtomicBest<>();
        while (!heap.isEmpty()) {
            final T vertex = heap.pop();
            final Set<T> neighbourSet = neighbours.apply(vertex);
            neighbourSet.stream().filter(heap::contains).forEach(n -> heap.decrementKey(n, heap.peekKey(n) - 1));
            bronKerbosch(Set.of(vertex), neighbourSet, neighbourSet, largestClique, neighbours, isNeighbours);
        }
        return largestClique.consumeAndReturnValue().orElse(Set.of());
    }

    private static <T> void bronKerbosch(final Set<T> r, final Set<T> p, final Set<T> x, final AtomicBest<Set<T>> best, final Function<T, Set<T>> neighbours, final BiPredicate<T, T> isNeighbours) {
        if (p.isEmpty() && x.isEmpty()) {
            best.isBest(r.size(), r);
        } else {
            final Set<T> modP = new HashSet<>(p);
            final Set<T> modX = new HashSet<>(x);
            final T pivot = Stream.concat(p.stream(), x.stream()).max(Comparator.comparingInt(neighbours.andThen(Set::size)::apply)).orElseThrow();
            final Predicate<T> notPivotNeighbour = not(Functions.bindLeft(isNeighbours::test, pivot)::apply);
            p.stream().filter(notPivotNeighbour).forEach(v -> {
                final Set<T> modR = new HashSet<>(r);
                modR.add(v);
                final Set<T> pAndVN = new HashSet<>(modP);
                final Set<T> xAndVN = new HashSet<>(modX);
                final Set<T> vNeighbours = neighbours.apply(v);
                pAndVN.retainAll(vNeighbours);
                xAndVN.retainAll(vNeighbours);
                bronKerbosch(modR, pAndVN, xAndVN, best, neighbours, isNeighbours);
                modP.remove(v);
                modX.add(v);
            });
        }
    }

    public static <T> List<T> topologicalSort(Map<T, Set<T>> outgoingConnections) {
        final Map<T, Set<T>> workingCopy = new HashMap<>(outgoingConnections);
        final List<T> returnValue = new ArrayList<>();
        while (!workingCopy.isEmpty()) {
            final Set<T> roots = workingCopy.keySet().stream().filter(candidate -> workingCopy.values().stream().noneMatch(s -> s.contains(candidate))).collect(Collectors.toSet());
            returnValue.addAll(roots);
            workingCopy.keySet().removeAll(roots);
        }
        return returnValue;
    }
}
