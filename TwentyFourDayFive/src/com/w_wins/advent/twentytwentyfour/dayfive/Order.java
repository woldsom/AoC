package com.w_wins.advent.twentytwentyfour.dayfive;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Streams;
import com.w_wins.common.ComparisonResult;
import com.w_wins.common.OpportunisticComparator;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public record Order(
        int first,
        int second) {
    public static Order parse(String pipeSeparated) {
        return Streams.pairStream(Streams.split(pipeSeparated, "|").map(Integer::parseInt), Order::new).collect(CollectorUtil.singleton());
    }

    public static Comparator<Integer> asComparator(final Set<Order> orders) {
        return OpportunisticComparator.asComparatorDefaultingTo(0, OpportunisticComparator.combine(orders.stream().map(Order::asOpportunisticComparator).collect(Collectors.toSet())));
    }

    public OpportunisticComparator<Integer> asOpportunisticComparator() {
        return (a, b) -> a == first() && b == second() ? ComparisonResult.FIRST_LESS : a == second() && b == first() ? ComparisonResult.SECOND_LESS : ComparisonResult.UNSURE;
    }
}
