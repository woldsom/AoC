package com.w_wins.pathfinding;

public record GScoredPathElement<S, E>(S score, E element, GScoredPathElement<S, E> previous) {
}
