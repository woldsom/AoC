package com.w_wins.pathfinding;

import java.util.concurrent.atomic.AtomicReference;

public record FScoredPathElement<S, E>(S heuristicScore, AtomicReference<GScoredPathElement<S, E>> accurate) {
}
