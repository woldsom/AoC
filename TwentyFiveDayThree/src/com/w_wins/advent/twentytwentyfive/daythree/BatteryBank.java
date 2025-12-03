package com.w_wins.advent.twentytwentyfive.daythree;

import com.w_wins.common.LongNumberBuilder;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.addExact;
import static java.lang.Math.multiplyExact;

public record BatteryBank(List<Integer> list) {

    public BatteryBank {
        if (list.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public static List<BatteryBank> parse(final Stream<String> lines) {
        return lines.map(BatteryBank::parse).toList();
    }

    public static BatteryBank parse(final String line) {
        return new BatteryBank(line.chars().mapToObj(c -> c - '0').toList());
    }

    public long bestJolt(int batteryCount, Map<Arguments, Long> memo) {
        final Arguments arguments = new Arguments(this, batteryCount);
        if (memo.containsKey(arguments)) {
            return memo.get(arguments);
        }
        final long result;
        if (batteryCount == 1) {
            result = list().stream().mapToInt(x -> x).max().orElseThrow();
        } else if (batteryCount > list().size()) {
            throw new IllegalArgumentException();
        } else if (batteryCount == list.size()) {
            result = list.stream().mapToLong(Integer::longValue).reduce(0, (a, b) -> addExact(multiplyExact(a, 10), b));
        } else {
            result = IntStream.range(batteryCount - 1, list().size()).mapToObj(
                            indexFromEnd -> new Arguments(new BatteryBank(list().subList(list().size() - indexFromEnd, list().size())), batteryCount - 1)
                    )
                    .mapToLong(
                            nextArguments -> combine(list().get(list().size() - 1 - nextArguments.batteryBank().list().size()), nextArguments.batteryBank().bestJolt(nextArguments.batteryCount(), memo))
                    )
                    .max().orElseThrow();
        }
        memo.put(arguments, result);
        return result;
    }

    private long combine(final int largest, final long rest) {
        return Long.parseLong(largest + Long.toString(rest));
    }
}
