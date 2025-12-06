package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daysix.Six;
import com.w_wins.adventcommon.AdventTwoPartEvaluator;
import com.w_wins.common.CollectionUtil;
import com.w_wins.common.Gatherers;
import com.w_wins.common.Streams;
import com.w_wins.common.Strings;
import com.w_wins.iostream.Utf8ResourceLines;
import com.w_wins.symbolic.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DaySixMain {
    static void main() {
        IO.println(new Utf8ResourceLines(Six.class, "/input.txt").evaluate(new AdventTwoPartEvaluator(DaySixMain::partOne, DaySixMain::partTwo)));
    }

    private static long partOne(Stream<String> lines) {
        final List<LongBinaryOperator> operators = new ArrayList<>();
        final List<List<Long>> operands = CollectionUtil.transpose(Streams.splitLast(lines, line -> Strings.split(line).map(Symbol::asLongOperator).collect(Collectors.toCollection(()->operators))).map(line -> Strings.split(line).map(Long::parseLong).toList()).toList());
        return CollectionUtil.zip(operators, operands,(operator,localOperands)->localOperands.stream().reduce(operator::applyAsLong).orElseThrow()).stream().reduce(Math::addExact).orElseThrow();
    }

    private static long partTwo(Stream<String> lines) {
        return CollectionUtil.transpose(lines.map(line -> Strings.characters(line).toList()).toList()).stream().gather(Gatherers.splitWhen(list -> list.stream().allMatch(Character::isWhitespace))).mapToLong(grid -> {
            final LongBinaryOperator operator = Symbol.asLongOperator(grid.getFirst().getLast().toString());
            return grid.stream().mapToLong(line -> Long.parseLong(line.stream().filter(Character::isDigit).map(Object::toString).collect(Collectors.joining()).trim())).reduce(operator).orElseThrow();
        }).sum();
    }
}
