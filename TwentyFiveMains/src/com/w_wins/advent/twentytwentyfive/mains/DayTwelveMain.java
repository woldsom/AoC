package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.daytwelve.Shape;
import com.w_wins.advent.twentytwentyfive.daytwelve.Tree;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Either;
import com.w_wins.common.Functions;
import com.w_wins.iostream.LineGroupEvaluator;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DayTwelveMain {
    static void main() {
        final List<Tree> trees2 = new ArrayList<>();
        final List<Either<Shape, List<Tree>>> input = new Utf8ResourceLines(Shape.class, "/input.txt").evaluate(new LineGroupEvaluator<>(lineGroup -> {
            final List<String> lines = lineGroup.toList();
            return Shape.tryParse(lines).<Either<Shape, List<Tree>>>map(Either::a).orElseGet(() -> Either.b(lines.stream().map(Tree::parseTree).toList()));
        }, Collectors.toList()
        ));
        final List<Shape> shapes = input.stream().filter(Either::isA).map(Either::getA).toList();
        final List<Tree> trees = input.stream().filter(Either::isB).map(Either::getB).collect(CollectorUtil.singleton());
        IO.println(trees.stream().filter(Functions.bindRight(Tree::fits,shapes)::apply).count());
    }
}
