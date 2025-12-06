package com.w_wins.common;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Strings {
    public static String reverse(String input) {
        return IntStream.iterate(input.length() - 1, i -> i >= 0, i -> i - 1).map(i -> input.charAt(i)).mapToObj(Character::toString).collect(Collectors.joining());
    }

    public static String padLeft(final String string, final String padding, final int newLength) {
        if (string.length() > newLength) {
            throw new IllegalArgumentException("String \"" + string + "\" already above asked for length " + newLength);
        } else if (string.length() == newLength) {
            return string;
        } else {
            if (padding.length() > 1) {
                throw new IllegalArgumentException("Padding should be just one character for now");
            }
            return padding.repeat(newLength - string.length()) + string;
        }
    }

    public static Stream<Character> characters(final String string) {
        return string.chars().mapToObj(c -> (char) c);
    }

    public static Optional<String> notBlank(final String string) {
        if (string.isBlank()) {
            return Optional.empty();
        } else {
            return Optional.of(string);
        }
    }

    public static Character asCharacter(final String string) {
        if (string.length() > 1) {
            throw new IllegalArgumentException("Expected single character, got \"" + string + "\"");
        }
        return string.charAt(0);
    }

    public static Stream<String> evenSplit(final int parts, final String source) {
        if (source.length() % parts != 0) {
            throw new IllegalArgumentException("String not divisible into " + parts + " equal parts \"" + source + "\"");
        }
        final int partLength = source.length() / parts;
        return IntStream.range(0, parts).map(x -> x * partLength).mapToObj(index -> source.substring(index, index + partLength));
    }

    public static Stream<String> split(final String string) {
        return split(string, Pattern.compile("\\s+"));
    }

    public static Stream<String> split(final String string, final String delimiter) {
        return split(string, Pattern.compile(Pattern.quote(delimiter)));
    }

    private static Stream<String> split(final String string, final Pattern pattern) {
        return Arrays.stream(pattern.split(string));
    }
}
