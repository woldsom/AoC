package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.dayfourteen.Robot;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.adventcommon.Grids;
import com.w_wins.adventcommon.Quad;
import com.w_wins.common.CollectionUtil;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Streams;
import com.w_wins.numbers.euclidean.extended.EuclideanDomain;
import com.w_wins.numbers.euclidean.extended.ExtendedEuclideanResult;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;

public final class DayFourteenMain {

    public static final int WIDTH = 101;
    public static final int HEIGHT = 103;
    public static final Coordinate BOUNDS = new Coordinate(WIDTH, HEIGHT);

    public static void main() {
        final List<Robot> robotsSource = new Utf8ResourceLines(Robot.class, "/input.txt").evaluate(lines -> lines.map(line -> Robot.parse(line, new Coordinate(WIDTH, HEIGHT))).toList());
        System.out.println(Streams.present(robotsSource.stream().map(robot -> robot.advance(100)).map(Robot::quadrant)).collect(CollectorUtil.frequency()).values().stream().mapToLong(x -> x).reduce(Math::multiplyExact).orElseThrow());
        // Four different part 2 solutions
        System.out.println(solveByFramePredicate(robotsSource, DayFourteenMain::allPositionsDistinct)); // Actually used to submit answer
        System.out.println(solveByFramePredicate(robotsSource, DayFourteenMain::lotsOfFilledQuads)); // Very slow (iterates every single floor square at least four times)
        System.out.println(solveByFramePredicate(robotsSource, DayFourteenMain::aRobotIsSurrounded));
        System.out.println(solveByRandomRobotsSharingLine(robotsSource)); // probabilistic, but does not calculate any floor grids/frames
    }

    public static int solveByRandomRobotsSharingLine(final List<Robot> robots) {
        final int boundingProduct = BOUNDS.x() * BOUNDS.y();
        final List<Robot> goodBots = robots.stream().filter(r -> r.period() == boundingProduct).toList();
        final RandomGenerator random = RandomGenerator.getDefault();
        final List<Integer> xTimes = new ArrayList<>();
        final List<Integer> yTimes = new ArrayList<>();
        IntStream.range(0, 5000).map(_ -> random.nextInt(goodBots.size())).boxed().gather(Gatherers.windowFixed(2)).forEach(list -> {
            if (list.getFirst().equals(list.getLast())) {
                return;
            }
            final Robot first = goodBots.get(list.getFirst());
            final Robot second = goodBots.get(list.getLast());
            final Coordinate times = first.minus(second).componentTimeToOrigo().wrap(BOUNDS);
            xTimes.add(BOUNDS.x() - times.x());
            yTimes.add(BOUNDS.y() - times.y());
        });
        final int commonXTime = CollectionUtil.mostCommon(xTimes);
        final int commonYTime = CollectionUtil.mostCommon(yTimes);
        final ExtendedEuclideanResult<Integer> boundResult = EuclideanDomain.withInteger().extendedEuclidean(BOUNDS.x(), -BOUNDS.y());
        return ((commonXTime + (commonYTime - commonXTime) * boundResult.bezoutA() * BOUNDS.x()) % boundingProduct + boundingProduct) % boundingProduct;
    }

    public static int solveByFramePredicate(final List<Robot> robotsSource, final Predicate<List<Robot>> framePredicate) {
        return Robot.findByFramePredicate(robotsSource, framePredicate).steps();
    }

    public static boolean aRobotIsSurrounded(final List<Robot> robots) {
        final Map<Coordinate, Long> map = robots.stream().map(Robot::position).collect(CollectorUtil.frequency());
        return map.keySet().stream().anyMatch(c -> c.flatNeighbours(c1 -> c1.x() >= 0 && c1.x() < WIDTH && c1.y() >= 0 && c1.y() < HEIGHT).allMatch(map::containsKey));
    }

    public static boolean allPositionsDistinct(final List<Robot> robots) {
        return robots.stream().collect(Collectors.groupingBy(Robot::position, Collectors.toSet())).values().stream().allMatch(set -> set.size() == 1);
    }

    public static boolean lotsOfFilledQuads(final List<Robot> robots) {
        final Grid<Boolean> grid = Quad.invertGrid(Robot.makeGrid(robots, WIDTH, HEIGHT), quad -> quad.stream().allMatch(x -> x), _ -> Boolean.FALSE);
        final long count = Grids.stream(grid).filter(x -> x).count();
        return count > 10;
    }
}
