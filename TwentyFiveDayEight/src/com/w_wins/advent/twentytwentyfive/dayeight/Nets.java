package com.w_wins.advent.twentytwentyfive.dayeight;

import com.w_wins.common.CollectionUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Nets(Map<Coordinate, Net> map, int target) {
    public Stream<Integer> netSizes() {
        return map().values().stream().distinct().map(Net::size).sorted(Comparator.reverseOrder());
    }

    public static Nets withTarget(final int target) {
        return new Nets(new HashMap<>(), target);
    }

    public OptionalLong extendWithAndPossiblyScore(final Edge edge) {
        final Net net = extendWith(edge);
        if (net.size() == target()) {
            return OptionalLong.of(edge.score());
        }
        return OptionalLong.empty();
    }

    private Net extendWith(final Edge edge) {
        final Set<Net> netsTouchingEdge = edge.points().stream().filter(map()::containsKey).map(map()::get).collect(Collectors.toSet());
        return switch (netsTouchingEdge.size()) {
            case 0 -> createNet(edge);
            case 1 -> expandNet(edge, CollectionUtil.singleton(netsTouchingEdge));
            case 2 -> connectTwoNets(edge, netsTouchingEdge);
            default -> throw new IllegalArgumentException();
        };
    }

    public Net createNet(final Edge edge) {
        final Net net = edge.asNet();
        integrate(net);
        return net;
    }

    private void integrate(final Net net) {
        net.coordinates().forEach(point -> map().put(point, net));
    }

    public Net expandNet(final Edge edge, final Net net) {
        net.subsume(edge);
        integrate(net);
        return net;
    }

    public Net connectTwoNets(final Edge edge, final Set<Net> nets) {
        final Net newNet = nets.stream().reduce(Net::union).orElseThrow();
        newNet.subsume(edge);
        integrate(newNet);
        return newNet;
    }
}
