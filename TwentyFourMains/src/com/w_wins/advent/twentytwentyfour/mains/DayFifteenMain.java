package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.fifteen.Square;
import com.w_wins.advent.twentytwentyfour.fifteen.Warehouse;
import com.w_wins.adventcommon.AdventTwoPartEvaluator;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.adventcommon.Grids;
import com.w_wins.adventcommon.MapGrid;
import com.w_wins.common.CollectorUtil;
import com.w_wins.fixedwidth.LabelledGrid;
import com.w_wins.iostream.TwoLineGroupBuilder;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class DayFifteenMain {
    private static int partTwo(Stream<String> lines) {
        final TwoLineGroupBuilder<LabelledGrid<Square>, List<Coordinate>> input = parse().apply(lines);
        final Map<Coordinate, Square> map = Grids.toMap(input.getFirstGroupResult()).entrySet().stream().flatMap(entry -> IntStream.range(0, 2).mapToObj(addition -> Map.entry(new Coordinate(entry.getKey().x() * 2 + addition, entry.getKey().y()), hanger(entry.getValue(), addition)))).collect(CollectorUtil.toMap());
        final List<Coordinate> movements = input.getSecondGroupResult();
        final Coordinate robot = map.entrySet().stream().filter(e -> Square.ROBOT.equals(e.getValue())).min(Map.Entry.comparingByKey(Coordinate.Y_FIRST_COMPARATOR)).orElseThrow().getKey();
        System.err.println(map.put(robot, Square.FLOOR));
        System.err.println(map.put(robot.plus(new Coordinate(1, 0)), Square.FLOOR));
        final Warehouse warehouse = new Warehouse(robot, map);
        Grids.debug(new MapGrid<Square>(map, input.getFirstGroupResult().getColumnCount() * 2, input.getFirstGroupResult().getRowCount(), Square.FLOOR), Square::character);
        warehouse.moveDouble(movements);
        return warehouse.sumGps();
    }

    private static Square hanger(final Square value, final int addition) {
        return addition == 0 ? value : Square.STONE.equals(value) ? Square.STONE_HANGER : value;
    }

    private static int partOne(Stream<String> lines) {
        final TwoLineGroupBuilder<LabelledGrid<Square>, List<Coordinate>> input = parse().apply(lines);
        final Map<Coordinate, Square> map = new HashMap<>(Grids.toMap(input.getFirstGroupResult()));
        final List<Coordinate> movements = input.getSecondGroupResult();
        final Coordinate robot = map.entrySet().stream().filter(e -> Square.ROBOT.equals(e.getValue())).collect(CollectorUtil.singleton()).getKey();
        map.put(robot, Square.FLOOR);
        final Warehouse warehouse = new Warehouse(robot, map);
        warehouse.move(movements);
        return warehouse.sumGps();
    }

    private static TwoLineGroupBuilder<LabelledGrid<Square>, List<Coordinate>> parse() {
        return new TwoLineGroupBuilder<>(Warehouse.parser(), Warehouse::parseDirection);
    }

    void main() {
        System.out.println(new Utf8ResourceLines(Warehouse.class, "/input.txt").evaluate(new AdventTwoPartEvaluator(DayFifteenMain::partOne, DayFifteenMain::partTwo)));
    }

}
