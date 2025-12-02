package com.w_wins.advent.twentytwentyfour.daytwentytwo;

import com.w_wins.common.Functions;
import com.w_wins.common.Streams;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;

public record PseudoRandomGenerator(
        int value) {
    public static final int module = 16777216;

    public static int next(final int original) {
        int newValue = original;
        newValue = prune(mix(newValue,shift(newValue,6)));
        newValue = prune(mix(newValue,shift(newValue,-5)));
        newValue = prune(mix(newValue,shift(newValue,11)));
        return newValue;
    }

    public static int nextTwoThousand(final int original) {
        int newValue = original;
        for(int i=0;i<2000;++i) {
            newValue=next(newValue);
        }
        return newValue;
    }

    private static int shift(final int secret, final int i) {
        if (i > 0) {
            return secret << i;
        } else if (i < 0) {
            return secret >> -i;
        } else {
            return secret;
        }
    }

    private static int prune(final int mix) {
        return mix & 16777215;
    }

    private static int mix(final int original, final int i) {
        return original ^ i;
    }

    public static Map<Sequence,Integer> firstOccurrencePrice(int initial) {
        final AtomicInteger atomic = new AtomicInteger(initial);
        return Streams.asMap(IntStream.rangeClosed(0, 2000).map(_ -> atomic.getAndUpdate(PseudoRandomGenerator::next)).map(i->i%10).boxed().gather(Gatherers.windowSliding(5)).map(list -> new Sequence(list.stream().mapToInt(x -> x).toArray())).map(sequence -> Map.entry(sequence.diffs(), sequence.price()))).collect(Collectors.groupingBy(e -> {
            final Sequence key = e.getValue().getKey();
            System.err.println("Finding key "+key.pretty());
            return key;
        }, Collectors.collectingAndThen(Collectors.minBy(Sequence.comparingByKey()), o -> {
            //System.err.println("Calculating after minimum, supposed to get price:"+o);
            //Sequence.dump(o.orElseThrow().getValue());
            return o.orElseThrow().getValue().getValue();
        })));
    }
}
