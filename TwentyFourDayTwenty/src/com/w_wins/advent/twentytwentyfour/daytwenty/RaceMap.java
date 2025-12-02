package com.w_wins.advent.twentytwentyfour.daytwenty;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.CollectorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public record RaceMap(
        List<Coordinate> path) {
    public static RaceMap parse(final Stream<String> lines) {
        final Coordinate xIncrement = new Coordinate(1, 0);
        NavigableMap<Integer, List<Coordinate>> columnToSegments = new TreeMap<>();
        List<Coordinate> currentPathRun = new ArrayList<>();
        Coordinate current = new Coordinate(0, 0);
        for (final String line : lines.toList()) {
            for (char c : line.toCharArray()) {
                switch (c) {
                    case 'S':
                    case 'E':
                    case '.':
                        currentPathRun.add(current);
                        break;
                    case '#':
                        if (!currentPathRun.isEmpty()) {
                            final List<Coordinate> realPath = new ArrayList<>(currentPathRun);
                            final Coordinate start = realPath.getFirst();
                            final Coordinate end = realPath.getLast();
                            System.err.println("Start " + start + ", End " + end + ", set: " + columnToSegments);
                            final Coordinate adjustment = new Coordinate(1, 0);
                            List<Coordinate> left = columnToSegments.get(start.x());
                            if (left != null && left.getFirst().minus(current.minus(adjustment)).manhattanMagnitude() != 1 && left.getLast().minus(current.minus(adjustment)).manhattanMagnitude() != 1) {
                                System.err.println("Not neighbours, clearing: " + current.minus(adjustment) + " is neither " + left.getFirst()+" nor "+left.getLast());
                                left = null;
                            }
                            if (left != null) {
                                columnToSegments.remove(left.getFirst().x());
                                columnToSegments.remove(left.getLast().x());
                            }
                            List<Coordinate> right = columnToSegments.get(end.x());
                            if (right != null && right.getFirst().minus(current.minus(adjustment)).manhattanMagnitude()!=1 && right.getLast().minus(current.minus(adjustment)).manhattanMagnitude()!=1) {
                                System.err.println("Not neighbours, clearing: " + current.minus(adjustment) + " is neither " + right.getFirst()+" nor "+right.getLast());
                                right = null;
                            }
                            if (right != null) {
                                columnToSegments.remove(right.getFirst().x());
                                columnToSegments.remove(right.getLast().x());
                            }
                            System.err.println("For " + current.minus(adjustment) + " left was " + left + "\n and right was " + right);
                            if (left != null) {
                                realPath.addAll(0, left.getFirst().minus(start).manhattanMagnitude()==1 ? left.reversed() : left);
                                checkRun(realPath);
                            }
                            if (right != null) {
                                realPath.addAll(realPath.size(), right.getFirst().minus(end).manhattanMagnitude()==1 ? right : right.reversed());
                                checkRun(realPath);
                            }
                            if (realPath.getFirst().y()+1 == current.y() || realPath.getFirst().y() == current.y()) {
                                columnToSegments.put(realPath.getFirst().x(), realPath);
                                System.err.println("Added " + realPath + "\n to map at " + realPath.getFirst().x());
                            } else {
                                System.err.println("Not first of "+realPath+" at "+current);
                            }
                            if (realPath.getLast().y()+1 == current.y() || realPath.getLast().y() == current.y()) {
                                columnToSegments.put(realPath.getLast().x(), realPath);
                                System.err.println("Added " + realPath + "\n to map at " + realPath.getLast().x());
                            } else {
                                System.err.println("Not last of " + realPath + " at " + current);
                            }
                            currentPathRun = new ArrayList<>();
                        }
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                current = current.plus(xIncrement);
            }
            if (!currentPathRun.isEmpty()) {
                throw new IllegalArgumentException();
            }
            current = new Coordinate(0, current.y() + 1);
            System.err.println("END OF LINE WITH "+columnToSegments.size()+" SEGMENTS");
        }
        return new RaceMap(columnToSegments.values().stream().distinct().collect(CollectorUtil.singleton()));
    }

    private static void checkRun(final List<Coordinate> run) {
        if (!run.stream().gather(Gatherers.windowSliding(2)).allMatch(l -> l.getFirst().minus(l.getLast()).manhattanMagnitude() == 1)) {
            throw new IllegalStateException("not: " + run);
        }
    }
}
