package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.dayseven.Equation;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.List;

public final class DaySevenMain {
    void main() {
        final List<Equation> values = new Utf8ResourceLines(Equation.class, "/input.txt").evaluate(lines -> lines.map(Equation::parse).toList());
        System.out.println(values.stream().filter(value-> !value.binarySolutions().isEmpty()).mapToLong(Equation::target).sum());
        System.out.println(values.stream().filter(value-> !value.trinarySolutions().isEmpty()).mapToLong(Equation::target).sum());
    }
}
