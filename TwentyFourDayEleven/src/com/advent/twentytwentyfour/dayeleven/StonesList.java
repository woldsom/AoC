package com.advent.twentytwentyfour.dayeleven;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Streams;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record StonesList(
        List<BigInteger> list
) {
    public static StonesList parse(Stream<String> lines) {
        return new StonesList(Streams.split(lines.collect(CollectorUtil.singleton()), " ").map(BigInteger::new).toList());
    }

    public int count() {
        return list().size();
    }

    public StonesList blink() {
        return new StonesList(this.list().stream().parallel().mapMulti(StonesList::rules).toList());
    }

    public StonesList blink(final int count) {
        final AtomicReference<StonesList> reference = new AtomicReference<>(this);
        IntStream.range(0, count).forEach(_ -> reference.getAndUpdate(StonesList::blink));
        return reference.get();
    }

    public static void rules(final BigInteger stone, final Consumer<BigInteger> newStones) {
        if (BigInteger.ZERO.equals(stone)) {
            newStones.accept(BigInteger.ONE);
        } else {
            final String digits = stone.toString();
            if (digits.length() % 2 == 0) {
                newStones.accept(new BigInteger(digits.substring(0, digits.length() / 2)));
                newStones.accept(new BigInteger(digits.substring(digits.length() / 2)));
            } else {
                newStones.accept(stone.multiply(BigInteger.valueOf(2024)));
            }
        }
    }

    public StonesList blinkMemo(final Map<BigInteger, List<BigInteger>> memo) {

        return new StonesList(list().stream().parallel().<BigInteger>mapMulti((stone, consumer) -> {
            final List<BigInteger> memoized = memo.get(stone);
            if (memoized != null) {
                memoized.forEach(consumer);
            } else {
                final List<BigInteger> list = new ArrayList<>();
                rules(stone, list::add);
                memo.put(stone, list);
                list.forEach(consumer);
            }
        }).toList());
    }
}
