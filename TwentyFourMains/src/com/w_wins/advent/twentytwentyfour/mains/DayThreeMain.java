package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daythree.Three;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.quote;

public final class DayThreeMain {
    void main() {
        final String line = new Utf8ResourceLines(Three.class, "/input.txt").getLines().collect(Collectors.joining());
        final Pattern partOnePattern = Pattern.compile(quote("mul(") + "(\\d{1,3})" + quote(",") + "(\\d{1,3})" + quote(")"));
        final Matcher partOneMatcher = partOnePattern.matcher(line);
        System.out.println(partOneMatcher.results().mapToLong(result -> Math.multiplyExact(Long.parseLong(result.group(1)), Long.parseLong(result.group(2)))).sum());
        final Pattern partTwoPattern = Pattern.compile(quote("do()") + "|" + quote("don't()"));
        final Matcher partTwoMatcher = partTwoPattern.matcher(line);
        final SortedMap<Integer, Boolean> map = new TreeMap<>(partTwoMatcher.results().map(result -> Functions.<Integer, Boolean>entry().apply(result.start(), "do()".equals(result.group()))).collect(CollectorUtil.toMap()));
        map.put(0, true);
        partOneMatcher.reset();
        System.out.println(partOneMatcher.results().filter(result -> map.headMap(result.start()).lastEntry().getValue()).mapToLong(result -> Math.multiplyExact(Long.parseLong(result.group(1)), Long.parseLong(result.group(2)))).sum());
    }
}
