package com.w_wins.pathfinding;

import com.w_wins.common.Streams;

import java.util.ArrayList;
import java.util.List;

public record ScoredPath<S, E>(E headElement, GScoredPathElement<S, E> tailElement) {
    public S cost() {
        return tailElement().score();
    }

    public List<E> path() {
        final List<E> elements = new ArrayList<>();
        GScoredPathElement<S, E> current = tailElement();
        do {
            elements.add(current.element());
            current = current.previous();
        } while (current!=null);
        return Streams.reverse(elements).toList();
    }
}
