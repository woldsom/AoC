package com.w_wins.advent.twentytwentyfive.daythree;

import com.w_wins.common.LongNumberBuilder;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

public record BatteryBankGatherer(int length, int batteryCount) implements Gatherer<Character, BatteryBankGatherer.State, Long> {

    @Override
    public Supplier<State> initializer() {
        return () -> new State(new ArrayDeque<>(), new AtomicInteger());
    }

    @Override
    public Integrator<State, Character, Long> integrator() {
        return (progress, character, downstream) -> {
            while (progress.stack().size() + length - progress.position().get() > batteryCount() && !progress.stack().isEmpty() && progress.stack().peek() < character) {
                progress.stack().pop();
            }
            if (progress.stack().size() < batteryCount()) {
                progress.stack().push(character);
            }
            if (progress.position().get() == length - 1) {
                final LongNumberBuilder numberBuilder = new LongNumberBuilder();
                progress.stack().reversed().forEach(numberBuilder::addDigit);
                downstream.push(numberBuilder.getAndZero().orElseThrow());
                return false;
            } else {
                progress.position().incrementAndGet();
                return true;
            }
        };
    }

    public record State(Deque<Character> stack, AtomicInteger position) {
    }
}
