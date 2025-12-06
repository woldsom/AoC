package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daysix.Six;
import com.w_wins.adventcommon.AdventTwoPartEvaluator;
import com.w_wins.common.NumberBuilder;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.SimpleGridParser;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

public class DaySixMain {
    static void main() {
        IO.println(new Utf8ResourceLines(Six.class, "/input.txt").evaluate(new AdventTwoPartEvaluator(lines -> {
            final List<List<String>> input = lines.map(line -> Arrays.stream(line.split("\\s+")).filter(not(String::isBlank)).toList()).toList();
            final List<List<Integer>> numbers = new ArrayList<>();
            final List<String> operators = new ArrayList<>();
            input.forEach(row -> {
                final List<Integer> rowList = new ArrayList<>();
                row.forEach(column -> {
                    try {
                        rowList.add(Integer.parseInt(column.trim()));
                    } catch (NumberFormatException _) {
                        operators.add(column.trim());
                    }
                });
                if (!rowList.isEmpty()) {
                    numbers.add(rowList);
                }
            });
            return IntStream.range(0, operators.size()).mapToLong(index -> {
                final LongBinaryOperator function = switch (operators.get(index)) {
                    case "*" -> Math::multiplyExact;
                    case "+" -> Math::addExact;
                    default -> throw new IllegalArgumentException(operators.get(index));
                };
                return numbers.stream().mapToLong(number -> number.get(index)).reduce(function).orElseThrow();
            }).sum();
        }, lines -> {
            final List<String> list = new ArrayList<>(lines.toList());
            final int longest = list.stream().mapToInt(String::length).max().orElseThrow();
            list.replaceAll(line -> line.concat(" ".repeat(longest - line.length())));
            final Grid<String> grid = new SimpleGridParser(GridConfig.CHAR_GRID).apply(list.stream());
            LongBinaryOperator function = null;
            final List<Long> numbers = new ArrayList<>();
            long sum = 0;
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                final NumberBuilder builder = new NumberBuilder();
                boolean spaces = true;
                for (int row = 0; row < grid.getRowCount(); ++row) {
                    final char character = grid.get(column, row).charAt(0);
                    if (character != ' ') {
                        spaces = false;
                        if (row == grid.getRowCount() - 1) {
                            function = switch (character) {
                                case '*' -> Math::multiplyExact;
                                case '+' -> Math::addExact;
                                default -> throw new IllegalArgumentException("" + character);
                            };
                        } else {
                            builder.addDigit(character);
                        }
                    }
                }
                builder.getAndZero().stream().mapToLong(x -> x).forEach(numbers::add);
                if (spaces || column == grid.getColumnCount() - 1) {
                    sum = Math.addExact(sum, numbers.stream().mapToLong(x -> x).reduce(function).orElseThrow());
                    numbers.clear();
                }
            }
            return sum;
        })));
    }
}
