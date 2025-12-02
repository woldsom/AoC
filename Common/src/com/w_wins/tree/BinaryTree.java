package com.w_wins.tree;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class BinaryTree {
    public static <T, V> T addNode(final T tree, final V value, final Function<T, Optional<T>> left, final Function<T, Optional<T>> right, final BiFunction<V, T, BinaryTreeDirection> getDirection, final Function<V, T> createNode, final BiFunction<T, V, T> updateValue, final BiFunction<T, T, T> updateLeft, final BiFunction<T, T, T> updateRight) {
        final Map<BinaryTreeDirection, Function<T, Optional<T>>> hasDirection = BinaryTreeDirection.wrapAsMap(left, right, null);
        final Map<BinaryTreeDirection, BiFunction<T, T, T>> update = BinaryTreeDirection.wrapAsMap(updateLeft, updateRight, null);
        final BinaryTreeDirection direction = getDirection.apply(value, tree);
        if (direction == BinaryTreeDirection.CURRENT_NODE) {
            return updateValue.apply(tree, value);
        } else {
            final Optional<T> directionHasNode = hasDirection.get(direction).apply(tree);
            final BiFunction<T, T, T> directionSetter = update.get(direction);
            final T newTree = directionHasNode
                    .map(newNode -> directionSetter.apply(tree, addNode(newNode, value, left, right, getDirection, createNode, updateValue, updateLeft, updateRight)))
                    .orElseGet(() -> directionSetter.apply(tree, createNode.apply(value)));
            System.err.println("Progress:");
            System.err.println(newTree);
            return newTree;
        }
    }
}
