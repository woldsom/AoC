package com.w_wins.advent.twentytwentyfour.daytwentyfour;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Either;
import com.w_wins.common.Functions;
import com.w_wins.common.Math;
import com.w_wins.common.Streams;
import com.w_wins.common.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Equation(
        BooleanOperator operator,
        String first,
        String second,
        String target) {
    public static long evaluateSet(final Map<String, Integer> initials, final List<Equation> equations) {
        final Map<String, Either<Boolean, Equation>> result = new HashMap<>();
        initials.forEach((variable, value) -> result.put(variable, Either.a(toBoolean(value))));
        equations.forEach(equation -> result.put(equation.target(), Either.b(equation)));
        while (result.values().stream().anyMatch(Either::isB)) {
            result.entrySet().forEach(current -> {
                final Either<Boolean, Equation> newValue = current.getValue().map(Either::a, e -> {
                    if (e.operands().allMatch(o -> result.get(o).isA())) {
                        return Either.a(e.operator().evaluate(result.get(e.first()).a().orElseThrow(), result.get(e.second()).a().orElseThrow()));
                    } else {
                        return Either.b(e);
                    }
                });
                if (!newValue.equals(current.getValue())) {
                    current.setValue(newValue);
                }
            });
        }
        return result.entrySet().stream().filter(e -> e.getKey().startsWith("z")).sorted(Map.Entry.<String, Either<Boolean, Equation>>comparingByKey().reversed()).mapToLong(e -> e.getValue().a().orElseThrow().booleanValue() ? 1 : 0).reduce(0, (a, b) -> a * 2 + b);
    }

    private static boolean toBoolean(final int value) {
        if (value == 1) {
            return true;
        } else if (value == 0) {
            return false;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static Set<String> findSwaps(final List<Equation> equationsThatAdd) {
        final Map<String, SymbolicEquation> symbolic = symbolic(equationsThatAdd);
        IntStream.range(0, 46).forEach(i -> {
            final String key = "z" + Strings.padLeft("" + i, "0", 2);
            final SymbolicEquation equation = symbolic.get(key).byDepth();
            System.err.println(key + " = " + equation.pretty());
            System.err.println(key + " = " + equation.lowest() + " to " + equation.highest());
        });
        final long x = 4096 * 2048 * 2048L-1;
        final long y = 4096 * 2048 * 2048L-1;
        final Map<String, Boolean> args = fromNumbers(x, y);
        System.err.println(args);
        final long z = calculateDigit(symbolic, args);
        System.err.println("Calculating " + x + "+" + y + ":" + z);
        if (x + y != z) {
            System.err.println("WRONG");
        }
        IntStream.range(0,44).forEach(i->{
            final long val = Math.pow(2, i);
            final long calc = calculateDigit(symbolic, fromNumbers(val, val));
            if(2*val!= calc){
                System.err.println("Wrong for "+i+", was instead "+calc+"("+Long.toBinaryString(calc)+")");
            }
        });
        return Set.of("z00", "aaa", "b02", "y17");
    }

    private static long calculateDigit(final Map<String, SymbolicEquation> equations, final Map<String, Boolean> arguments) {
        return IntStream.range(0, 46).map(x -> 45 - x).mapToObj(Functions.<Integer, String>entry(x -> new BitOperand(false, x).pretty().replace('y', 'z'))::apply).mapToLong(e -> equations.get(e.getValue()).evaluate(arguments) ? 1 : 0).reduce(0, (a, b) -> a * 2 + b);
    }

    private static Map<String, Boolean> fromNumbers(final long x, final long y) {
        return Stream.concat(fromNumbers("x", x), fromNumbers("y", y)).collect(CollectorUtil.toMap());
    }

    private static Stream<Map.Entry<String, Boolean>> fromNumbers(final String var, final long value) {
        return Streams.asMap(Long.toBinaryString(value).chars().map(x -> x - '0').boxed().toList().reversed().stream(), _ -> 0, 45).map(e -> Map.entry(var + Strings.padLeft(e.getKey().toString(), "0", 2), e.getValue() > 0));
    }

    private static Map<String, SymbolicEquation> symbolic(final List<Equation> equationsThatAdd) {
        final Map<String, Either<SymbolicEquation, Equation>> map = new HashMap<>(equationsThatAdd.stream().map(e -> Map.entry(e.target(), Either.<SymbolicEquation, Equation>b(e))).collect(CollectorUtil.toMap()));
        while (map.values().stream().anyMatch(Either::isB)) {
            map.entrySet().forEach(current -> {
                final Either<SymbolicEquation, Equation> newValue = current.getValue().map(Either::a, e -> {
                    if (e.operands().allMatch(o -> o.startsWith("y") || o.startsWith("x") || map.get(o).isA())) {
                        return Either.a(new SymbolicEquation(e.operator(), getOperand(map, e.first()), getOperand(map, e.second())));
                    } else {
                        return Either.b(e);
                    }
                });
                if (!newValue.equals(current.getValue())) {
                    current.setValue(newValue);
                }
            });
        }
        return map.entrySet().stream().map(Functions.onValue(e -> e.a().orElseThrow())).collect(CollectorUtil.toMap());
    }

    private static Either<SymbolicEquation, BitOperand> getOperand(final Map<String, Either<SymbolicEquation, Equation>> map, final String key) {
        if (key.startsWith("x")) {
            return Either.b(new BitOperand(true, Integer.parseInt(key.substring(1))));
        } else if (key.startsWith("y")) {
            return Either.b(new BitOperand(false, Integer.parseInt(key.substring(1))));
        } else {
            return Either.a(map.get(key).a().orElseThrow());
        }
    }

    public Stream<String> operands() {
        return Stream.of(first(), second());
    }
}
