package com.w_wins.advent.twentytwentyfour.daynineteen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record Nineteen(
        Set<String> towels,
        Map<String, Long> countMemoization,
        Pattern possiblePattern) {
    public static Nineteen fromTowels(Set<String> setTowels) {
        return new Nineteen(setTowels, new HashMap<>(), Pattern.compile("^(" + setTowels.stream().map(Pattern::quote).collect(Collectors.joining("|")) + ")*$"));
    }

    public boolean possible(final String pattern) {
        if (pattern.isBlank()) {
            return true;
        }
        return possiblePattern.matcher(pattern).find();
    }

    public long count(final String towelPattern) {
        final Long memoizedReturnValue = countMemoization.get(towelPattern);
        if (memoizedReturnValue == null) {
            final Map<Integer, Long> substringLengthToCounts = towels().stream().filter(towelPattern::startsWith).collect(Collectors.groupingBy(String::length, HashMap::new, Collectors.counting()));
            final Optional<Long> singleTowelForRestCount = Optional.ofNullable(substringLengthToCounts.remove(towelPattern.length()));
            final long sumOfCounts = Math.addExact(singleTowelForRestCount.orElse(0L), substringLengthToCounts.entrySet().stream().mapToLong(e -> Math.multiplyExact(e.getValue(), count(towelPattern.substring(e.getKey())))).sum());
            countMemoization.put(towelPattern, sumOfCounts);
            return sumOfCounts;
        } else {
            return memoizedReturnValue;
        }
    }
}
