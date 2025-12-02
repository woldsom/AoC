package com.w_wins.advent.twentytwentyfour.fifteen;

import com.w_wins.adventcommon.Coordinate;
import com.w_wins.adventcommon.Grids;
import com.w_wins.adventcommon.MapGrid;
import com.w_wins.common.Strings;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.GridParser;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.function.Predicate.not;


public final class Warehouse {
    private final Map<Coordinate, Square> map;
    private Coordinate robot;

    public Warehouse(final Coordinate setRobot, final Map<Coordinate, Square> setMap) {
        map = setMap;
        robot = setRobot;
    }

    public static GridParser<Square> parser() {
        return new GridParser<>(GridConfig.CHAR_GRID, Square::parse, null);
    }

    public static List<Coordinate> parseDirection(final Stream<String> lines) {
        return Strings.characters(lines.collect(Collectors.joining())).map(character -> switch (character) {
            case 'v' ->
                    new Coordinate(0, 1);
            case '^' ->
                    new Coordinate(0, -1);
            case '>' ->
                    new Coordinate(1, 0);
            case '<' ->
                    new Coordinate(-1, 0);
            default ->
                    throw new IllegalArgumentException("Wrong: " + character);
        }).toList();
    }

    public static int gps(final Coordinate where) {
        return where.y() * 100 + where.x();
    }

    private boolean pushDoublesVertically(final Coordinate direction) {
        Coordinate stone = robot;
        while (map.get(stone).isStone()) {
            stone = stone.plus(direction);
        }
        return switch (map.get(stone)) {
            case FLOOR -> {
                Stream.iterate(stone, not(robot::equals), c -> c.minus(direction)).forEach(c -> {
                    final Square newValue = map.get(c.minus(direction));
                    map.put(c, newValue);
                });
                map.put(robot, Square.FLOOR);
                yield true;
            }
            case WALL ->
                    false;
            default ->
                    throw new IllegalStateException();
        };
    }

    public int sumGps() {
        return map.entrySet().stream().filter(e -> Square.STONE.equals(e.getValue())).map(Map.Entry::getKey).mapToInt(Warehouse::gps).sum();
    }

    public void move(final List<Coordinate> movements) {
        movements.forEach(this::move);
    }

    private void move(final Coordinate direction) {
        robot = robot.plus(direction);
        switch (map.get(robot)) {
            case FLOOR:
                break;
            case WALL:
                robot = robot.minus(direction);
                break;
            case STONE:
                if (!push(direction)) {
                    robot = robot.minus(direction);
                }
                break;
            default:
                throw new IllegalStateException("Wrong: " + map.get(robot) + " at " + robot);
        }
    }

    private boolean push(final Coordinate direction) {
        Coordinate stone = robot;
        while (Square.STONE.equals(map.get(stone))) {
            stone = stone.plus(direction);
        }
        return switch (map.get(stone)) {
            case FLOOR -> {
                map.put(stone, Square.STONE);
                map.put(robot, Square.FLOOR);
                yield true;
            }
            case WALL ->
                    false;
            default ->
                    throw new IllegalStateException();
        };
    }

    public void moveDouble(final List<Coordinate> movements) {
        movements.forEach(this::moveDouble);
    }

    private void moveDouble(Coordinate direction) {
        robot = robot.plus(direction);
        switch (map.get(robot)) {
            case FLOOR:
                break;
            case WALL:
                robot = robot.minus(direction);
                break;
            case STONE:
            case STONE_HANGER:
                if (!pushDoubles(direction)) {
                    robot = robot.minus(direction);
                }
                break;
            default:
                throw new IllegalStateException("Wrong: " + map.get(robot) + " at " + robot);
        }
        //System.err.println("After move:");
        //Grids.debug(new MapGrid<>(map, 20, 10, Square.FLOOR), Square::character);
    }

    private boolean pushDoubles(final Coordinate direction) {
        if (direction.x() == 0) {
            final Optional<Set<Coordinate>> result = pushDoublesHorizontally(List.of(robot.plus(Square.STONE_HANGER.equals(map.get(robot)) ? new Coordinate(-1, 0) : Coordinate.ORIGO)), direction);
            result.ifPresent(stones -> updateStonesHorizontally(stones, direction));
            return result.isPresent();
        } else {
            return pushDoublesVertically(direction);
        }
    }

    private void updateStonesHorizontally(final Set<Coordinate> stones, final Coordinate direction) {
        if (!stones.isEmpty()) {
            final Set<Coordinate> stoneCoordinates = stones.stream().flatMap(stone -> IntStream.range(0, 2).mapToObj(x -> new Coordinate(x, 0)).map(stone::plus)).collect(Collectors.toSet());
            final Set<Coordinate> coordinates = stoneCoordinates.stream().map(direction::plus).collect(Collectors.toSet());
            coordinates.addAll(stoneCoordinates);
            final Comparator<Coordinate> comparator = direction.y() > 0 ? Coordinate.Y_FIRST_COMPARATOR.reversed() : Coordinate.Y_FIRST_COMPARATOR;
            //System.err.println("Updating "+coordinates.size()+" "+coordinates.stream().sorted(comparator).toList()+" because of stones "+stones.size()+" "+stones);
            coordinates.stream().sorted(comparator).forEachOrdered(stone -> {
                final Coordinate from = stone.minus(direction);
                if (coordinates.contains(from)) {
                    final Square value = map.get(from);
                    //System.err.println("Taking from stone at "+from+" for "+stone+": "+value);
                    map.put(stone, value);
                } else {
                    //System.err.println("Taking from floor at "+from+" for "+stone+ " because from isn't in "+coordinates);
                    map.put(stone, Square.FLOOR);
                }
            });
        }
    }

    public Optional<Set<Coordinate>> pushDoublesHorizontally(final List<Coordinate> stones, final Coordinate direction) {
        final Coordinate hanger = new Coordinate(1, 0);
        final Set<Coordinate> nextStones = new HashSet<>();
        for (final Coordinate stone : stones) {
            final Coordinate next = stone.plus(direction);
            if (Square.WALL.equals(map.get(next)) || Square.WALL.equals(map.get(next.plus(hanger)))) {
                return Optional.empty();
            }
            if (Square.STONE_HANGER.equals(map.get(next))) {
                nextStones.add(next.minus(hanger));
            }
            if (Square.STONE.equals(map.get(next.plus(hanger)))) {
                nextStones.add(next.plus(hanger));
            }
            if (Square.STONE.equals(map.get(next))) {
                nextStones.add(next);
            }
        }
        //System.err.println("From "+stones+" next is "+nextStones);
        if (nextStones.isEmpty()) {
            //System.err.println("So I'm returning "+stones);
            return Optional.of(new HashSet<>(stones));
        }
        final List<Coordinate> list = StreamSupport.stream(nextStones.spliterator(), false).sorted().toList();
        final Optional<Set<Coordinate>> nextResult = pushDoublesHorizontally(list, direction);
        if (nextResult.isEmpty()) {
            //System.err.println(" and returning "+stones);
            return Optional.empty();
        } else {
            return Optional.of(Stream.concat(StreamSupport.stream(((Collection<Coordinate>) nextResult.orElseThrow()).spliterator(), false), StreamSupport.stream(stones.spliterator(), false)).collect(Collectors.toSet()));
        }
    }
}
