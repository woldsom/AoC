package com.advent.twentytwentyfour.dayeleven;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Strings;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record StonesFrequency(
        Map<Long, BigInteger> frequency) {


    public static StonesFrequency withList(final List<BigInteger> list) {
        return new StonesFrequency(list.stream().map(BigInteger::longValueExact).map(Functions.entry(_ -> BigInteger.ONE)).collect(CollectorUtil.toMap()));
    }

    public BigInteger blink(final int iterations) {
        IntStream.range(0, iterations).forEach(_ -> blink());
        return frequency().values().stream().reduce(BigInteger::add).orElse(BigInteger.ZERO);
    }

    public void blink() {
        final Stream<Map.Entry<Long, BigInteger>> stream = frequency().entrySet().stream();
        final Map<Long, BigInteger> newValues = stream.mapMulti(Functions.onKeyMulti(StonesFrequency::rules)).collect(CollectorUtil.<Long, BigInteger, BigInteger>toMap(Collectors.collectingAndThen(Collectors.reducing(BigInteger::add), Optional::orElseThrow)));
        frequency().clear();
        frequency().putAll(newValues);
    }

    public static void rules(final long stone, final Consumer<Long> newStones) {
        if (stone == 0) {
            newStones.accept(1L);
        } else {
            final String digits = Long.toString(stone);
            if (digits.length() % 2 == 0) {
                Strings.evenSplit(2, digits).map(Long::parseLong).forEach(newStones);
            } else {
                newStones.accept(Math.multiplyExact(stone, 2024));
            }
        }
    }
}
