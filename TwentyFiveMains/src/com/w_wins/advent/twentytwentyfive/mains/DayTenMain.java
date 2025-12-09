package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayten.Ten;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.stream.Stream;

public class DayTenMain {
    static void main() {
        IO.println(new Utf8ResourceLines(Ten.class,"/test.txt").evaluate(Stream::toList));
    }
}
