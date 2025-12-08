package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daynine.Nine;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.List;
import java.util.stream.Stream;

public class DayNineMain {
    static void main() {
        final List<String> list = new Utf8ResourceLines(Nine.class, "/test.txt").evaluate(Stream::toList);
        IO.println(list);
    }
}
