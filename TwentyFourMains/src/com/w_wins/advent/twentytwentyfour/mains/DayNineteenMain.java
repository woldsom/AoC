package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daynineteen.Nineteen;
import com.w_wins.common.Streams;
import com.w_wins.iostream.TwoLineGroupBuilder;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public final class DayNineteenMain {
    void main() {
        final TwoLineGroupBuilder<List<String>, List<String>> words = new Utf8ResourceLines(Nineteen.class, "/input.txt").evaluate(new TwoLineGroupBuilder<>(lines -> lines.flatMap(line -> Streams.split(line, ",")).map(String::trim).toList(), Stream::toList));
        final Set<String> towels = new HashSet<>(words.getFirstGroupResult());
        final Set<String> patterns = new HashSet<>(words.getSecondGroupResult());
        final Nineteen nineteen = Nineteen.fromTowels(towels);
        System.out.println(patterns.stream().filter(nineteen::possible).count());
        System.out.println(patterns.stream().mapToLong(nineteen::count).sum());
    }
}
