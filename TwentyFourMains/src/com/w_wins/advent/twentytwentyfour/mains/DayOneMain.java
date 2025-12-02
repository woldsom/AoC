package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.dayone.One;
import com.w_wins.common.CollectionUtil;
import com.w_wins.common.Math;
import com.w_wins.common.Streams;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.List;

public class DayOneMain {
    void main() {
        final List<List<Integer>> input = new Utf8ResourceLines(One.class, "/input.txt").evaluate(
                lines -> lines.map(line -> Streams.split(line, "   ").map(Integer::parseInt).toList()).toList()
        );
        final List<List<Integer>> lists = CollectionUtil.transpose(input).stream().map(CollectionUtil::sorted).toList();
        final List<Integer> left = lists.getFirst();
        final List<Integer> right = lists.getLast();
        System.out.println(CollectionUtil.zip(left, right, Math::absoluteDifference).stream().mapToInt(x -> x).sum());
        System.out.println(left.stream().mapToLong(leftId -> {
            return right.stream().filter(leftId::equals).count() * leftId;
        }).sum());
    }
}
