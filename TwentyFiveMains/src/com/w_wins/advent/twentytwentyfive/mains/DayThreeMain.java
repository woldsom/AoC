package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daythree.BatteryBankGatherer;
import com.w_wins.adventcommon.AdventTwoPartEvaluator;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Strings;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.function.Function;
import java.util.stream.Stream;

public class DayThreeMain {
    static void main() {
        IO.println(new Utf8ResourceLines(BatteryBankGatherer.class, "/input.txt").evaluate(new AdventTwoPartEvaluator(run(2), run(12))));
    }

    private static Function<Stream<String>, Long> run(final int batteryCount) {
        return lines -> lines.mapToLong(line -> Strings.characters(line).gather(new BatteryBankGatherer(line.length(), batteryCount)).collect(CollectorUtil.singleton())).sum();
    }
}
