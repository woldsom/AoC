package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daynine.Rectangle;
import com.w_wins.adventcommon.AdventTwoPartEvaluator;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.CollectionUtil;
import com.w_wins.common.Strings;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class DayNineMain {
    static void main() {
        IO.println(new Utf8ResourceLines(Rectangle.class, "/input.txt").evaluate(new AdventTwoPartEvaluator(DayNineMain::partOne, DayNineMain::partTwo)));
    }

    private static long partTwo(final Stream<String> lines) {
        final List<Coordinate> points = new ArrayList<>(points(lines));
        final List<Rectangle> candidates = new ArrayList<>(CollectionUtil.selfCross(points, Rectangle::thatIsConvex).toList());
        candidates.sort(Rectangle.COMPARATOR);
        points.add(points.getFirst());
        points.add(points.get(1));
        points.stream().gather(Gatherers.windowSliding(3)).forEach(corner->{
            final Rectangle rectangle = Rectangle.ofLines(corner);
            if(rectangle ==null) {
                throw new IllegalArgumentException("Did not expect bad rectangles, but this one was: "+corner);
            }
            if(!rectangle.convex()){
                candidates.removeIf(rectangle::intersects);
            }
        });
        return candidates.getLast().area();
    }

    private static long partOne(final Stream<String> lines) {
        return CollectionUtil.selfCross(points(lines), Coordinate::minus).mapToLong(c -> Math.multiplyExact(c.x() + 1L, c.y() + 1L)).max().orElseThrow();
    }

    private static List<Coordinate> points(final Stream<String> lines) {
        return lines.map(DayNineMain::parseCoordinate).toList();
    }

    private static Coordinate parseCoordinate(String string) {
        final List<Integer> list = Strings.split(string, ",").map(Integer::parseInt).toList();
        return new Coordinate(list.getFirst(), list.getLast());
    }
}
