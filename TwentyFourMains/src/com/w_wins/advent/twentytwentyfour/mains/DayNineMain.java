package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daynine.PartOneFileSystem;
import com.w_wins.advent.twentytwentyfour.daynine.PartTwoFileSystem;
import com.w_wins.adventcommon.AdventTwoPartEvaluator;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public final class DayNineMain {
    void main() {
        System.out.println(new Utf8ResourceLines(PartOneFileSystem.class, "/input.txt").evaluate(new AdventTwoPartEvaluator(result(PartOneFileSystem::deFragmentBlocks, PartOneFileSystem::parse, PartOneFileSystem::checkSum), result(PartTwoFileSystem::deFragmentFiles, PartTwoFileSystem::parse, PartTwoFileSystem::checkSum))));
    }

    private <T> Function<Stream<String>, Long> result(final Consumer<T> deFragmentMethod, final Function<Stream<String>, T> parse, Function<T, Long> checksum) {
        return lines -> {
            final T fileSystem = parse.apply(lines);
            deFragmentMethod.accept(fileSystem);
            return checksum.apply(fileSystem);
        };
    }
}
