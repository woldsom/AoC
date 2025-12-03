package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daythree.Three;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DayThreeMain {

    public static final Comparator<Integer> DESCENDING = Comparator.<Integer>naturalOrder().reversed();

    static void main() {
        final List<List<Integer>> input = new Utf8ResourceLines(Three.class, "/input.txt").evaluate(lines -> lines.map(line -> line.chars().mapToObj(c -> c - '0').toList()).toList());
        final int sum = input.stream().mapToInt(bank -> {
            final List<Integer> sorted = getSorted(bank);
            if (sorted.getFirst().equals(sorted.get(1))) {
                return sorted.getFirst() * 11;
            }
            final int largestPosition = bank.indexOf(sorted.getFirst());
            if (largestPosition < bank.size() - 1) {
                return sorted.getFirst() * 10 + getSorted(bank.subList(largestPosition + 1, bank.size())).getFirst();
            }
            final Set<Integer> sortedSet = new TreeSet<>(DESCENDING);
            sortedSet.addAll(sorted);
            final int secondLargest = sortedSet.stream().skip(1).findFirst().orElseThrow();
            final int secondLargestPosition = bank.indexOf(secondLargest);
            return secondLargest * 10 + getSorted(bank.subList(secondLargestPosition + 1, bank.size())).getFirst();
        }).sum();
        IO.println(sum);
    }

    private static List<Integer> getSorted(final List<Integer> unmodifiable) {
        final List<Integer> sorted = new ArrayList<>(unmodifiable);
        sorted.sort(DESCENDING);
        return sorted;
    }
}
