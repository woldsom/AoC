package com.w_wins.iostream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Utf8ResourceLines {
    private final Class<?> origin;
    private final String resourceName;

    public Utf8ResourceLines(final Class<?> setOrigin, final String setResourceName) {
        origin = setOrigin;
        resourceName = setResourceName;
        if (!origin.getModule().isNamed()) {
            throw new IllegalArgumentException("Origin specified (" + setOrigin + ") is not in a named module, but in " + origin.getModule());
        }
    }

    public <T> T evaluate(final Function<Stream<String>, T> processor) {
        try (final Stream<String> lines = getLines()) {
            return processor.apply(lines);
        }
    }

    public Stream<String> getLines() {
        final InputStream input = origin.getResourceAsStream(resourceName);
        if (input == null) {
            throw new NullPointerException("Resource " + resourceName + " not found");
        }
        return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)).lines().onClose(() -> {
            try {
                input.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public void process(final Consumer<Stream<String>> processor) {
        try (final Stream<String> lines = getLines()) {
            processor.accept(lines);
        }
    }
}
