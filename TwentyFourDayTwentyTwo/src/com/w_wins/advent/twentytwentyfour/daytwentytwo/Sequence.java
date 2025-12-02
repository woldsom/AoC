package com.w_wins.advent.twentytwentyfour.daytwentytwo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Gatherers;

public final class Sequence {
    private final int[] values;

    public Sequence(
            int[] values) {
        this.values = values;
    }

    public static void dump(Map.Entry<Sequence, Integer> sequenceIntegerEntry) {
        System.err.println(Arrays.toString(sequenceIntegerEntry.getKey().values()) + ":" + sequenceIntegerEntry.getValue());
    }

    public static Comparator<? super Map.Entry<Integer, Map.Entry<Sequence, Integer>>> comparingByKey() {
        return (a, b) -> {
            final int resultValue = a.getKey().compareTo(b.getKey());
            System.err.println("Comparing " + a + " and " + b + " result is " + resultValue);
            return resultValue;
        };
    }

    public void dump() {
        System.err.println(Arrays.toString(values()));
    }

    public Sequence diffs() {
        return new Sequence(Arrays.stream(values()).boxed().gather(Gatherers.windowSliding(2)).mapToInt(list -> list.getLast() - list.getFirst()).toArray());
    }

    public int price() {
        return values()[4];
    }

    public String pretty() {
        return "Sequence[values=[" + Arrays.toString(values()) + "]]";
    }

    public int[] values() {
        return values;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Sequence) obj;
        return Arrays.equals(this.values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.toString(values));
    }

    @Override
    public String toString() {
        return pretty();
    }

}
