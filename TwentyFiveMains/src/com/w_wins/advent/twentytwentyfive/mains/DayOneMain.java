package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayone.Instruction;
import com.w_wins.advent.twentytwentyfive.dayone.One;
import com.w_wins.adventcommon.AdventTwoPartEvaluator;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.concurrent.atomic.AtomicInteger;

public class DayOneMain {
    static void main() {
        final AtomicInteger count = new AtomicInteger(0);
        final AtomicInteger countTwo = new AtomicInteger(0);
        IO.println(new Utf8ResourceLines(One.class, "/input.txt").evaluate(new AdventTwoPartEvaluator(lines -> {
            lines.map(Instruction::asInt).reduce(50, (a, b) -> {
                final int result = a + b;
                if (result % 100 == 0) {
                    count.incrementAndGet();
                }
                return result;
            });
            return count.get();
        }, lines -> {
            lines.map(Instruction::asInt).reduce(50, (a, b) -> {
                if (b < 0) {
                    final int alternateStart = -a;
                    final int alternateAdd = -b;
                    final int alternateResult = alternateStart + alternateAdd;
                    countTwo.addAndGet(Math.abs(div(alternateResult) - div(alternateStart)));
                    return a + b;
                } else {
                    final int result = a + b;
                    countTwo.addAndGet(Math.abs(div(result) - div(a)));
                    return result;
                }
            });
            return countTwo.get();
        })));
    }

    private static int div(final int result) {
        final int diff = com.w_wins.common.Math.modulusFull(result, 100);
        return (result - diff) / 100;
    }
}
