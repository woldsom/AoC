package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daythree.Three;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.List;

public class DayThreeMain {
    static void main() {
        final List<Three> input = new Utf8ResourceLines(Three.class, "/test.txt").evaluate(Three::parse);
        IO.println(input);
    }
}
