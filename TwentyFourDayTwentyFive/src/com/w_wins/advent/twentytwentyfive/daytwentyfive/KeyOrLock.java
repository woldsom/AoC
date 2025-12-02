package com.w_wins.advent.twentytwentyfive.daytwentyfive;

import com.w_wins.common.CollectionUtil;
import com.w_wins.common.Predicates;
import com.w_wins.common.Streams;
import com.w_wins.fixedwidth.Grid;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

public record KeyOrLock(boolean key,
                        List<Integer> pins, int height) {
    public static final Comparator<KeyOrLock> LIST_COMPARATOR = (a,b)->CollectionUtil.<List<Integer>,Integer>comparingElements().compare(a.pins(),b.pins() );

    public static KeyOrLock process(Grid<String> grid) {
        final String matcher = grid.get(0, 0);
        final List<Integer> entries = IntStream.range(0, grid.getColumnCount()).map(column -> Math.toIntExact(IntStream.range(0, grid.getRowCount()).mapToObj(row -> grid.get(column, row)).filter(Predicates.allUntil(not(matcher::equals))).count())).boxed().toList();
        return new KeyOrLock(".".equals(matcher),entries,grid.getRowCount());
    }

    public static boolean fit(List<KeyOrLock> keyThenLock) {
        if(keyThenLock.getFirst().key()^keyThenLock.getLast().key()) {
            return Streams.zip(keyThenLock.getFirst().pins().stream(),keyThenLock.getLast().pins().stream(),Math::subtractExact).allMatch(i->i>=0);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
