package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayeight.Coordinate;
import com.w_wins.advent.twentytwentyfive.dayeight.Edge;
import com.w_wins.advent.twentytwentyfive.dayeight.Net;
import com.w_wins.common.CollectionUtil;
import com.w_wins.common.Functions;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DayEightMain {
    static void main() {
        final List<Coordinate> coordinates = new Utf8ResourceLines(Coordinate.class, "/input.txt").evaluate(lines -> lines.map(Coordinate::parse).toList());
        final SortedSet<Edge> edges = CollectionUtil.selfCross(coordinates, Coordinate::edge).collect(Collectors.toCollection(TreeSet::new));
        final Map<Coordinate, Net> map = new HashMap<>();
        edges.forEach(edge -> {
            final Set<Net> nets = edge.points().stream().filter(map::containsKey).map(map::get).collect(Collectors.toSet());
            final Net newNet;
            switch (nets.size()) {
                case 0:
                    IO.println("Edge " + edge + " is new");
                    newNet = new Net(new HashSet<>(Set.of(edge)), new HashSet<>(edge.points()));
                    edge.points().forEach(point -> map.put(point, newNet));
                    check(edge, newNet, coordinates);
                    break;
                case 1:
                    IO.println("Edge " + edge + " connects to one existing net");
                    nets.forEach(net -> {
                        net.edges().add(edge);
                        net.coordinates().addAll(edge.points());
                        edge.points().forEach(p->map.put(p,net));
                        check(edge,net, coordinates);
                    });
                    break;
                case 2:
                    IO.println("Edge " + edge + " connects two existing net");
                    newNet = nets.stream().reduce(Net::union).orElseThrow();
                    newNet.edges().add(edge);
                    newNet.coordinates().addAll(edge.points());
                    map.keySet().removeAll(newNet.coordinates());
                    newNet.coordinates().forEach(Functions.bindRight(map::put, newNet)::apply);
                    check(edge, newNet, coordinates);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        });
        final long result = map.values().stream().map(Net::coordinates).distinct().map(Set::size).sorted(Comparator.reverseOrder()).peek(IO::println).limit(3).reduce(Math::multiplyExact).orElseThrow();
        IO.println(result);
        final Set<Set<Edge>> netEdges = map.values().stream().map(Net::edges).collect(Collectors.toSet());
        IO.println(netEdges.stream().mapToInt(Set::size).sum());
        IO.println(map.values().stream().distinct().count());
    }

    private static void check(final Edge edge, final Net newNet, final List<Coordinate> coordinates) {
        if(newNet.coordinates().size()== coordinates.size()){
            IO.println(edge);
            IO.println(edge.points().stream().map(Coordinate::x).mapToLong(x->x).boxed().reduce(1L,Math::multiplyExact));
            throw new IllegalArgumentException();
        }
    }
}
