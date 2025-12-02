package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfive.daytwentyfive.KeyOrLock;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.SimpleGridParser;
import com.w_wins.iostream.LineGroupEvaluator;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class DayTwentyFiveMain {
    void main() {
        final Set<Grid<String>> input = new Utf8ResourceLines(KeyOrLock.class, "/example.txt").evaluate(new LineGroupEvaluator<>(new SimpleGridParser(GridConfig.CHAR_GRID), Collectors.toSet()));
        final Map<Boolean, List<KeyOrLock>> entries = input.stream().map(KeyOrLock::process).collect(Collectors.partitioningBy(KeyOrLock::key, Collectors.toList()));
        System.out.println(entries.get(true).stream().flatMap(key->entries.get(false).stream().map(lock->List.of(key,lock))).filter(KeyOrLock::fit).count());
    }
}
