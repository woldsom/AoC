package com.w_wins.pathfinding;

public record ScoredPathElement<S, E>(S score, E element, ScoredPathElement<S, E> previous) {
}
