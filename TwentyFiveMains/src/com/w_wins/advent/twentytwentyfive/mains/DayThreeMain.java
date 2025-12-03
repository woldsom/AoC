package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daythree.Arguments;
import com.w_wins.advent.twentytwentyfive.daythree.BatteryBank;
import com.w_wins.common.Functions;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class DayThreeMain {
    static void main() {
        final List<BatteryBank> input = new Utf8ResourceLines(BatteryBank.class, "/input.txt").evaluate(BatteryBank::parse);
        final Map<Arguments, Long> memo = new HashMap<>();
        final BiFunction<BatteryBank, Integer, Long> memoBound = (batteryBank, batteryCount) -> batteryBank.bestJolt(batteryCount, memo);
        if (Math.addExact(1, 1) == 2) {
            final long part1 = input.stream().map(Functions.bindRight(memoBound, 2)).mapToLong(x -> x).sum();
            final long part2 = input.stream().map(Functions.bindRight(memoBound, 12)).mapToLong(x -> x).sum();
            IO.println(part1);
            IO.println(part2);
        } else {
            IO.println(input.getFirst().bestJolt(12, memo));
            IO.println(new BatteryBank(List.of(1, 1, 1, 1, 1, 1)).bestJolt(3, memo));
        }
    }
}
