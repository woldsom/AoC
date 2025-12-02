package com.w_wins.advent.twentytwentyfour.mains;

import com.advent.twentytwentyfour.dayeleven.StonesFrequency;
import com.advent.twentytwentyfour.dayeleven.StonesList;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.concurrent.TimeUnit;

public final class DayElevenMain {
    void main() {
        final long start = System.nanoTime();
        final StonesFrequency stones = StonesFrequency.withList(new Utf8ResourceLines(StonesList.class, "/example.txt").evaluate(StonesList::parse).list());
        System.out.println(stones.blink(75000));
        System.err.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-start)+" ms");
    }
}
