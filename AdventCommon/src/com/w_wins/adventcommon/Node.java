package com.w_wins.adventcommon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class Node<V> {
    private final V value;
    private final Set<Node<V>> neighbours;

    public Node(final V setValue) {
        value = setValue;
        neighbours = new HashSet<>();
    }

    public V getValue() {
        return value;
    }

    public Node<V> createAndConnect(final V setValue) {
        final Node<V> newNode = fromValue(setValue);
        connect(newNode);
        return newNode;
    }

    public static <V> Node<V> fromValue(final V setValue) {
        return new Node<>(setValue);
    }

    public void connect(final Node<V> other) {
        neighbours.add(other);
        System.out.println(value + ": I now have " + neighbours.size() + " children");
        other.neighbours.add(this);
        System.out.println(other.value + ": I now have " + other.neighbours.size() + " children");
    }

    public int size() {
        return visit(node -> 1).stream().mapToInt(x -> x).sum();
    }

    public <R> List<R> visit(final Function<Node<V>, R> perNode) {
        return visitPaths(new HashSet<>(), new ArrayList<>(), l -> perNode.apply(l.get(l.size() - 1)));
    }

    public <R> List<R> visitPaths(final Function<List<Node<V>>, R> perNode) {
        return visitPaths(new HashSet<>(), new ArrayList<>(), perNode);
    }

    private <R> List<R> visitPaths(final Set<Node<V>> visited, List<Node<V>> path, final Function<List<Node<V>>, R> perNode) {
        if (visited.contains(this)) {
            return List.of();
        } else {
            visited.add(this);
            //System.err.println(path);
            path.add(this);
            final List<R> returnList = new ArrayList<>();
            returnList.add(perNode.apply(path));
            neighbours.forEach(node -> returnList.addAll(node.visitPaths(visited, path, perNode)));
            path.remove(path.size() - 1);
            //System.err.println(path);
            return returnList;
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "value=" + value +
                ", neighbours=" + neighbours.size() +
                '}';
    }

    public boolean connected(final Node<V> other) {
        return visit(n -> n.equals(other)).stream().anyMatch(b -> b);
    }
}
