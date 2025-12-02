package com.w_wins.pathfinding;

import com.w_wins.common.Functions;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public final class AStar<S, E> {
    private final BinaryOperator<S> summer;
    private final Comparator<S> comparator;
    private final Supplier<S> zero;

    public AStar(final BinaryOperator<S> setSummer, final Comparator<S> setComparator, final Supplier<S> setZero) {
        summer = setSummer;
        comparator = setComparator;
        zero = setZero;
    }

    public Optional<ScoredPath<S, E>> shortest(final E start, final E goal, final Function<E, Iterable<E>> neighbours, final BiFunction<E, E, S> score, final Function<E, S> heuristic) {
        final Comparator<FScoredPathElement<S, E>> pathComparator = Comparator.comparing(FScoredPathElement::heuristicScore, comparator);
        final Queue<FScoredPathElement<S, E>> heap = new PriorityQueue<>(pathComparator);
        final Map<E, AtomicReference<GScoredPathElement<S, E>>> singletons = new HashMap<>();
        final AtomicReference<GScoredPathElement<S, E>> startGElement = new AtomicReference<>(new GScoredPathElement<>(zero.get(), start, null));
        singletons.put(start, startGElement);
        final FScoredPathElement<S, E> startElement = new FScoredPathElement<>(heuristic.apply(start), startGElement);
        heap.add(startElement);
        while (!heap.isEmpty()) {
            final FScoredPathElement<S, E> current = heap.poll();
            final GScoredPathElement<S, E> gCurrent = current.accurate().get();
            if (gCurrent.element().equals(goal)) {
                return Optional.of(new ScoredPath<>(start, gCurrent));
            } else {
                neighbours.apply(gCurrent.element()).forEach(neighbour -> {
                    final S gScore = summer.apply(gCurrent.score(), score.apply(gCurrent.element(), neighbour));
                    if (!singletons.containsKey(neighbour) || comparator.compare(gScore, singletons.get(neighbour).get().score()) < 0) {
                        final GScoredPathElement<S, E> neighbourElement = new GScoredPathElement<>(gScore, neighbour, gCurrent);
                        final AtomicReference<GScoredPathElement<S, E>> reference = singletons.compute(neighbour, Functions.createIfMissingAndModify(AtomicReference::new, AtomicReference::set, neighbourElement));
                        heap.add(new FScoredPathElement<>(summer.apply(heuristic.apply(neighbour), neighbourElement.score()), reference));
                    }
                });
            }
        }
        return Optional.empty();
    }
}
