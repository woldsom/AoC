package com.w_wins.advent.twentytwentyfour.daytwo;

import com.w_wins.common.Streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.w_wins.common.Math.sgn;
import static com.w_wins.common.Math.absoluteDifference;

public enum ReactorStatus {
    UNSAFE, SAFE, SAFE_DAMPENED;

    public static ReactorStatus parse(final String report) {
        return checkLevels(Streams.split(report, " ").mapToInt(Integer::parseInt).toArray());
    }

    public static boolean checkTransitionAndDirection(int a, int b, int direction) {
        return Set.of(1, 2, 3).contains(absoluteDifference(a, b)) && sgn(b - a) * direction != -1;
    }

    public static ReactorStatus checkLevels(final int[] levels) {
        return sumStatus(safeParts(levels));
    }

    public static List<List<Integer>> safeParts(final int[] levels) {
        return Arrays.stream(levels).boxed().map(List::of).map(List::of).reduce((first, second) -> {
            final List<Integer> previousLast = first.getLast();
            final int previous = previousLast.getLast();
            final int previousDirection = previousLast.size() < 2 ? 0 : sgn(previous - previousLast.get(previousLast.size() - 2));
            final List<Integer> currentFirst = second.getFirst();
            final int current = currentFirst.getFirst();
            final int currentDirection = currentFirst.size() < 2 ? 0 : sgn(currentFirst.get(1) - current);
            final List<List<Integer>> returnValue = new ArrayList<>();
            if (previousDirection * currentDirection == -1 || previous == current || !checkTransitionAndDirection(previous, current, sgn(currentDirection + previousDirection))) {
                returnValue.addAll(first);
                returnValue.addAll(second);
            } else {
                returnValue.addAll(first);
                returnValue.removeLast();
                final List<Integer> merged = new ArrayList<>(previousLast);
                merged.addAll(currentFirst);
                returnValue.add(merged);
                second.stream().skip(1).forEach(returnValue::add);
            }
            return returnValue;
        }).orElseThrow();
    }

    public static ReactorStatus sumStatus(final List<List<Integer>> monotonicParts) {
        return switch (monotonicParts.size()) {
            case 1 ->
                    SAFE;
            case 2 -> {
                if (monotonicParts.getFirst().size() == 1 || monotonicParts.getLast().size() == 1 || (checkTransitionAndDirection(monotonicParts.getFirst().getLast(), monotonicParts.getLast().getFirst(), sgn(monotonicParts.getFirst().get(1) - monotonicParts.getFirst().getFirst())) && monotonicParts.getFirst().size() == 2) || checkTransitionAndDirection(monotonicParts.getFirst().get(monotonicParts.getFirst().size() - 2), monotonicParts.getLast().getFirst(), sgn(monotonicParts.getFirst().get(1) - monotonicParts.getFirst().getFirst())) || checkTransitionAndDirection(monotonicParts.getFirst().getLast(), monotonicParts.getLast().get(1), sgn(monotonicParts.getFirst().get(1) - monotonicParts.getFirst().getFirst()))) {
                    yield SAFE_DAMPENED;
                } else {
                    yield UNSAFE;
                }
            }
            case 3 -> {
                if (monotonicParts.get(1).size() == 1 && checkTransitionAndDirection(monotonicParts.getFirst().getLast(), monotonicParts.getLast().getFirst(), monotonicParts.getFirst().size() > 1 ? sgn(monotonicParts.getFirst().get(1) - monotonicParts.getFirst().getFirst()) : sgn(monotonicParts.getLast().getLast() - monotonicParts.getLast().get(monotonicParts.getLast().size() - 2)))) {
                    yield SAFE_DAMPENED;
                } else {
                    yield UNSAFE;
                }
            }
            default ->
                    UNSAFE;
        };
    }
}
