package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daytwelve.Twelve;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.stream.Stream;

public class DayTwelveMain {
    static void main() {
        IO.println(new Utf8ResourceLines(Twelve.class,"/test.txt").evaluate(Stream::toList));
    }
}
