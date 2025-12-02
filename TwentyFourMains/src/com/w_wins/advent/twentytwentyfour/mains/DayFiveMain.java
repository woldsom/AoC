package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.dayfive.Order;
import com.w_wins.common.Streams;
import com.w_wins.iostream.TwoLineGroupBuilder;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class DayFiveMain {
    public static void main() {
        final TwoLineGroupBuilder<Set<Order>, List<List<Integer>>> inputs = new Utf8ResourceLines(Order.class, "/input.txt").evaluate(new TwoLineGroupBuilder<>(
                lines -> lines.map(Order::parse).collect(Collectors.toSet()),
                lines -> lines.map(line -> Streams.split(line, ",").map(Integer::parseInt).collect(Collectors.<Integer, List<Integer>>toCollection(ArrayList::new))).toList()
        ));
        final Set<Order> orders = inputs.getFirstGroupResult();
        final Map<Boolean, List<List<Integer>>> parts = inputs.getSecondGroupResult().stream().collect(Collectors.partitioningBy(list -> sort(list, orders)));
        printMiddleSum(parts.get(true));
        printMiddleSum(parts.get(false));
    }

    public static boolean sort(final List<Integer> list, final Set<Order> orders) {
        final List<Integer> originalOrder = new ArrayList<>(list);
        list.sort(Order.asComparator(orders));
        return originalOrder.equals(list);
    }

    public static void printMiddleSum(final List<List<Integer>> oddList) {
        System.out.println(oddList.stream().mapToInt(list -> list.get(list.size() / 2)).sum());
    }
}
