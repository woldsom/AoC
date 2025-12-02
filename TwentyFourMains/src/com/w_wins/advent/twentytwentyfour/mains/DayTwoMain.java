package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daytwo.ReactorStatus;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.List;

import static java.util.function.Predicate.not;

public final class DayTwoMain {
    void main(){
        final List<ReactorStatus> statuses = new Utf8ResourceLines(ReactorStatus.class, "/input.txt").evaluate(lines -> lines.map(ReactorStatus::parse).toList());
        System.out.println(statuses.stream().filter(ReactorStatus.SAFE::equals).count());
        System.out.println(statuses.stream().filter(not(ReactorStatus.UNSAFE::equals)).count());
    }
}
