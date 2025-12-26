package com.w_wins.adventcommon;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.fixedwidth.Grid;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class BeamGrid<T> implements Grid<Set<Beam<T>>> {
    private final Map<Integer, SortedMap<Integer, Beam<T>>> vertical;
    private final Map<Integer, SortedMap<Integer, Beam<T>>> horizontal;
    private final int columnCount;
    private final int rowCount;

    public <D> BeamGrid(final Grid<D> source, final Predicate<D> beamPoint, final Function<D, Optional<T>> valueConversion) {
        final Map<Coordinate, T> beamPoints = Coordinate.coordinatesOf(source).map(Functions.entry(Coordinate.gridGetter(source))).filter(entry -> beamPoint.test(entry.getValue())).map(Functions.onValue(valueConversion)).filter(entry -> entry.getValue().isPresent()).map(Functions.onValue(Optional::orElseThrow)).collect(CollectorUtil.toMap());
        vertical = beamPoints.entrySet().stream().collect(
                Collectors.groupingBy(
                        entry -> entry.getKey().x(),
                        Collectors.collectingAndThen(
                                Collectors.mapping(
                                        entry -> Map.entry(entry.getKey().y(), new Beam<>(entry.getKey(), new Coordinate(0, 1), entry.getValue())),
                                        CollectorUtil.toMap()
                                ),
                                TreeMap::new
                        )
                )
        );
        horizontal = beamPoints.entrySet().stream().collect(
                Collectors.groupingBy(
                        entry -> entry.getKey().y(),
                        Collectors.collectingAndThen(
                                Collectors.mapping(
                                        entry -> Map.entry(entry.getKey().x(), new Beam<>(entry.getKey(), new Coordinate(1, 0), entry.getValue())),
                                        CollectorUtil.toMap()
                                ),
                                TreeMap::new
                        )
                )
        );
        columnCount = source.getColumnCount();
        rowCount = source.getRowCount();
    }

    @Override
    public Set<Beam<T>> get(final int column, final int row) {
        final SortedMap<Integer, Beam<T>> verticalMap = vertical.getOrDefault(column, new TreeMap<>());
        final SortedMap<Integer, Beam<T>> horizontalMap = horizontal.getOrDefault(row, new TreeMap<>());
        return Stream.concat(Stream.concat(
                Optional.ofNullable(verticalMap.headMap(row + 1).lastEntry()).map(Map.Entry::getValue).stream(),
                Optional.ofNullable(horizontalMap.headMap(column + 1).lastEntry()).map(Map.Entry::getValue).stream()), Stream.concat(
                Optional.ofNullable(verticalMap.tailMap(row).firstEntry()).map(Map.Entry::getValue).map(Beam::flip).stream(),
                Optional.ofNullable(horizontalMap.tailMap(column).firstEntry()).map(Map.Entry::getValue).map(Beam::flip).stream()
        )).map(beam -> beam.coordinate().equals(new Coordinate(column, row)) ? new Beam<>(beam.coordinate(), new Coordinate(0, 0), beam.value()) : beam).collect(Collectors.toSet());
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public String toString() {
        return "BeamGrid:\n" + IntStream.range(0, getRowCount()).mapToObj(row -> IntStream.range(0, getColumnCount()).mapToObj(column -> {
            final Set<Beam<T>> beams = get(column, row);
            //System.err.println(column + ", " + row + ": " + beams);
            if (beams.size() > 1) {
                return "#";
            } else if (beams.isEmpty()) {
                return ".";
            }
            final Beam<T> beam = beams.stream().collect(CollectorUtil.singleton());
            return switch (beam.direction().x() + beam.direction().y() * 2) {
                case 0 -> "*";
                case -1 -> "<";
                case 1 -> ">";
                case -2 -> "^";
                case 2 -> "v";
                default -> "?";
            };
        }).collect(Collectors.joining())).collect(Collectors.joining("\n"));
    }
}
