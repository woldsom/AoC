package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayfour.Four;
import com.w_wins.iostream.Utf8ResourceLines;

public class DayFourMain {
    static void main() {
        IO.println(new Utf8ResourceLines(Four.class,"/test.txt").evaluate(lines->lines.map(Four::parse).toList()));
    }
}
