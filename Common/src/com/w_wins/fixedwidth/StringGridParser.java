package com.w_wins.fixedwidth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class StringGridParser implements Function<Stream<String>, List<List<String>>> {
    private final GridConfig config;

    public StringGridParser(final GridConfig setConfig) {
        config = setConfig;
    }

    public List<List<String>> apply(final Stream<String> lines) {
        final AtomicReference<Optional<Integer>> lineLength = new AtomicReference<>(Optional.empty());
        final List<List<String>> rows = lines.map(line -> parseRow(line, lineLength)).collect(Collectors.toCollection(ArrayList::new));
        skip(rows, config.headersPresent().contains(HeaderType.TOP) ? 1 : 0, config.topHeaderSeparator());
        skip(rows, rows.size() - (config.headersPresent().contains(HeaderType.BOTTOM)? 1 : 0) - config.bottomHeaderSeparator(), config.bottomHeaderSeparator());
        if (config.cellVerticalSeparator() > 0) {
            int rowIndex = config.headersPresent().contains(HeaderType.TOP) ? 1 : 0;
            while (rowIndex < rows.size() - (config.headersPresent().contains(HeaderType.BOTTOM) ? 2 : 1)) {
                ++rowIndex;
                skip(rows, rowIndex, config.cellVerticalSeparator());
            }
        }
        if (config.emptyTrailing()) {
            final int longestLine = rows.stream().mapToInt(List::size).max().orElse(0);
            rows.stream().filter(line -> line.size() < longestLine).forEach(line -> IntStream.range(line.size(), longestLine).forEach(unneededIndex -> line.add("")));
        }
        return rows;
    }

    List<String> parseRow(final String line, final AtomicReference<Optional<Integer>> lineLength) {
        lineLength.get().ifPresentOrElse(expectedLine -> {
            if (line.length() != expectedLine && !config.emptyTrailing()) {
                throw new IllegalArgumentException("Grid is not even length, expected line to be " + expectedLine + " characters: \"" + line + "\n");
            }
        }, () -> lineLength.set(Optional.of(line.length())));
        final ArrayList<String> returnValue = new ArrayList<>();
        if (config.leftHeader() > 0) {
            returnValue.add(line.substring(0, config.leftHeader()));
        }
        int cellCount = 0;
        for (int index = config.leftHeader() + config.leftHeaderSeparator(); index < line.length() - config.rightHeader() - config.rightHeaderSeparator(); index += config.cellWidth() + config.cellHorizontalSeparator()) {
            returnValue.add(line.substring(index, index + config.cellWidth()));
            ++cellCount;
        }
        if (config.rightHeader() > 0) {
            returnValue.add(line.substring(line.length() - config.rightHeader()));
        }
        final int expectedLength = config.rightHeader() + config.rightHeaderSeparator() + config.leftHeader() + config.leftHeaderSeparator() + cellCount * config.cellWidth() + Math.max(0, cellCount - 1) * config.cellHorizontalSeparator();
        if (line.length() != expectedLength && !config.emptyTrailing()) {
            throw new IllegalArgumentException("Cells are not aligned in line \"" + line + "\"");
        }
        return returnValue;
    }

    void skip(final List<List<String>> rows, final int rowIndex, final int n) {
        if (n > 0) {
            if (rows.size() < rowIndex + n || rowIndex < 0) {
                throw new IllegalArgumentException("Grid is not tall enough, skipped past end when removing separators");
            }
            for (int count = 0; count < n; ++count) {
                rows.remove(rowIndex);
            }
        }
    }
}
