package com.w_wins.advent.twentytwentyfour.dayfourteen;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.adventcommon.MapGrid;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Streams;
import com.w_wins.numbers.euclidean.extended.EuclideanDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public record Robot(
        Coordinate position,
        Coordinate velocity,
        Coordinate bounds) {
    public static Robot parse(final String line, final Coordinate bounds) {
        final List<Coordinate> parts = Streams.split(line, " ").map(assignment -> {
            final int start = assignment.indexOf('=');
            final int assignmentStart = assignment.indexOf(',');
            return new Coordinate(Integer.parseInt(assignment.substring(start + 1, assignmentStart)), Integer.parseInt(assignment.substring(assignmentStart + 1)));
        }).toList();
        return new Robot(parts.getFirst(), parts.getLast(), bounds);
    }

    public static void advance(final List<Robot> robots, final int count) {
        final ListIterator<Robot> iterator = robots.listIterator();
        while (iterator.hasNext()) {
            final Robot old = iterator.next();
            iterator.set(old.advance(count));
        }
    }

    public static EasterEgg findByFramePredicate(final List<Robot> robotsSource, final Predicate<List<Robot>> predicate) {
        final List<Robot> robots = new ArrayList<>(robotsSource);
        final Coordinate bounds = robots.getFirst().bounds();
        return IntStream.range(0, bounds.x() * bounds.y()).boxed().<EasterEgg>mapMulti((count, downStream) -> {
            if (predicate.test(robots)) {
                downStream.accept(new EasterEgg(count, robots));
            }
            advance(robots, 1);
        }).findFirst().orElseThrow();
    }

    public static Map<Coordinate, Boolean> positions(final List<Robot> robots) {
        return robots.stream().map(Robot::position).distinct().map(Functions.entry(_ -> Boolean.TRUE)).collect(CollectorUtil.toMap());
    }

    public static MapGrid<Boolean> makeGrid(final List<Robot> robots, final int setColumnCount, final int setRowCount) {
        return new MapGrid<>(positions(robots), setColumnCount, setRowCount, Boolean.FALSE);
    }

    public Robot minus(final Robot second) {
        return new Robot(position().minus(second.position()).wrap(bounds()), velocity().minus(second.velocity()).wrap(bounds()), bounds());
    }

    public Robot advance(int count) {
        return new Robot(new Coordinate(advance(position().x(), velocity().x(), bounds().x(), count), advance(position().y(), velocity().y(), bounds().y(), count)), velocity(), bounds());
    }

    private int advance(final int p, final int v, final int bound, final int count) {
        return (p + ((v + bound) % bound) * count) % bound;
    }

    public Optional<String> quadrant() {
        final Coordinate quadrant = new Coordinate(Integer.compare(position().x(), bounds().x() / 2), Integer.compare(position().y(), bounds().y() / 2));
        if (quadrant.x() == 0 || quadrant.y() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(quadrant.toString());
        }
    }

    public int period() {
        return velocity().x() == 0 ? velocity().y() == 0 ? 0 : bounds.y() : velocity().y() == 0 ? bounds().x() : bounds().x() * bounds.y();
    }

    public Coordinate componentTimeToOrigo() {
        return new Coordinate(EuclideanDomain.withInteger().extendedEuclidean(velocity().x(), bounds().x()).bezoutA() * position().x(), EuclideanDomain.withInteger().extendedEuclidean(velocity().y(), bounds().y()).bezoutA() * position().y()).wrap(bounds());
    }
}
