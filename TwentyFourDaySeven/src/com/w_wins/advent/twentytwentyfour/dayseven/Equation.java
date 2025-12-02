package com.w_wins.advent.twentytwentyfour.dayseven;

import com.w_wins.common.Math;
import com.w_wins.common.Streams;
import com.w_wins.common.Strings;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

public record Equation(
        long target,
        List<Long> operands) {
    public static Equation parse(String line) {
        final String[] parts = line.split(Pattern.quote(": "), 2);
        return new Equation(Long.parseLong(parts[0]), Streams.split(parts[1], " ").map(Long::parseLong).toList());
    }

    public List<List<Operator>> binarySolutions() {
        return LongStream.range(0, Math.pow(2, operands().size() - 1)).mapToObj(mask -> Strings.padLeft(Long.toBinaryString(mask), "0", operands().size() - 1).chars().mapToObj(c -> c == '1' ? Operator.PLUS : Operator.TIMES).toList()).filter(operators -> {
            final long evaluation = evaluate(operators);
            final boolean returnValue = target() == evaluation;
            //System.err.println(this+" is "+(returnValue?"":"not ")+evaluation+" for "+operators);
            return returnValue;
        }).toList();
    }

    private long evaluate(final List<Operator> operators) {
        final AtomicLong resultSoFar = new AtomicLong(operands.getFirst());
        Streams.<Void, Operator, Long>zip(operators.stream(), operands().stream().skip(1).mapToLong(x -> x).boxed(), (Operator a, Long b) -> {
            final long start = resultSoFar.get();
            final long result = a.apply(start, (long) b);
            if (start != resultSoFar.compareAndExchange(start, result)) {
                throw new ConcurrentModificationException();
            }
            return null;
        }).toList();
        return resultSoFar.get();
    }

    public List<List<Operator>> trinarySolutions() {
        return LongStream.range(0, Math.pow(3, operands().size() - 1)).mapToObj(mask -> Strings.padLeft(Long.toString(mask,3), "0", operands().size() - 1).chars().mapToObj(c -> c == '1' ? Operator.PLUS : c=='2'?Operator.PIPE:Operator.TIMES).toList()).filter(operators -> {
            final long evaluation = evaluate(operators);
            final boolean returnValue = target() == evaluation;
            //System.err.println(this+" is "+(returnValue?"":"not ")+evaluation+" for "+operators);
            return returnValue;
        }).toList();
    }
}
