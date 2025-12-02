package com.w_wins.iostream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class LineGroupProcessor implements Consumer<Stream<String>> {
    private final Consumer<Stream<String>> delegate;

    public LineGroupProcessor(final Consumer<Stream<String>> setDelegate) {
        delegate = setDelegate;
    }

    @Override
    public void accept(final Stream<String> stringStream) {
        final List<String> lineAccumulator = new ArrayList<>();
        stringStream.forEach(line->{
            if(line.isBlank()) {
                send(lineAccumulator);
            } else {
                lineAccumulator.add(line);
            }
        });
        send(lineAccumulator);
    }

    private void send(final List<String> lineAccumulator) {
        if (lineAccumulator.size() > 0) {
            delegate.accept(lineAccumulator.stream());
            lineAccumulator.clear();
        }
    }
}
