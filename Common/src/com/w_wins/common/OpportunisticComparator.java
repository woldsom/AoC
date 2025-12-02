package com.w_wins.common;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public interface OpportunisticComparator<T> {
    ComparisonResult compare(T first, T second);

    static <T> OpportunisticComparator<T> combine(Set<OpportunisticComparator<T>> comparators) {
        return (a, b) -> {
            return comparators.stream().map(comparator -> comparator.compare(a, b)).filter(not(ComparisonResult.UNSURE::equals)).collect(Collectors.groupingBy(x -> x, Collectors.counting())).keySet().stream().collect(CollectorUtil.singleton());
        };
    }

    static <T> Comparator<T> asComparatorDefaultingTo(int defaultValue, OpportunisticComparator<T> comparator) {
        return (a, b) -> comparator.compare(a, b).toInt(defaultValue);
    }
}
