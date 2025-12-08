package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayeight.Eight;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.List;

public class DayEightMain {
    static void main() {
        final List<Eight> eights = new Utf8ResourceLines(Eight.class, "/test.txt").evaluate(lines -> lines.map(Eight::parse).toList());
        IO.println(eights);
    }
}
