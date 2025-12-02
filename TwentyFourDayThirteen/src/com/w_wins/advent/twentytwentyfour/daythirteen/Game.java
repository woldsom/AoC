package com.w_wins.advent.twentytwentyfour.daythirteen;

import com.w_wins.adventcommon.BigCoordinate;
import com.w_wins.adventcommon.Coordinate;

import java.util.List;
import java.util.OptionalLong;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public record Game(
        BigCoordinate a,
        BigCoordinate b,
        BigCoordinate prize) {
    public static Game parse(Stream<String> lines) {
        return parse(lines.toList());
    }

    private static Game parse(final List<String> lines) {
        return new Game(fromLine(lines.get(0)), fromLine(lines.get(1)), parsePrize(lines.get(2)));
    }

    private static BigCoordinate fromLine(final String line) {
        final String[] parts = line.split(Pattern.quote(" "));
        if (parts.length != 4) {
            throw new IllegalArgumentException();
        }
        return new BigCoordinate(parseNumber(parts[2]), parseNumber(parts[3]));
    }

    private static BigCoordinate parsePrize(final String line) {
        final String[] parts = line.split(Pattern.quote(" "));
        if (parts.length != 3) {
            throw new IllegalArgumentException();
        }
        return new BigCoordinate(parseNumber(parts[1]), parseNumber(parts[2]));
    }

    private static int parseNumber(final String string) {
        final String[] parts = string.split(Pattern.quote("=") + "|" + Pattern.quote("+") + "|" + Pattern.quote(","));
        return Integer.parseInt(parts[1]);
    }

    public OptionalLong movesToWin() {
        if (b().coLinearWith(prize())) {
            throw new IllegalArgumentException();
            //return OptionalLong.of(divide(prize(), b()));
        } else if (a().coLinearWith(prize())) {
            throw new IllegalArgumentException();
            //            return OptionalLong.of(divide(prize(), a()));
        } else if ((a().arcTangent() - prize().arcTangent()) * (b().arcTangent() - prize.arcTangent()) >= 0.0) {
            //throw new IllegalArgumentException(this+"");
            return OptionalLong.empty();
        } else {
            final long numerator = ((prize().x() - prize().y()) * (a().x() + a().y()) - (prize().x() + prize().y()) * (a().x() - a().y()));
            final long denominator = ((b().x() - b().y()) * (a().x() + a().y()) - (b().x() + b().y()) * (a().x() - a().y()));
            final long division = Math.divideExact(numerator, denominator);
            final long remainder = numerator - division * denominator;
            if (remainder == 0) {
                final long bCount = division;
                final long otherNumerator = prize().x() - b().x() * bCount;
                final long aCount = otherNumerator / a().x();
                final long otherRemainder = otherNumerator - aCount * a().x();
                if (otherRemainder == 0) {
                    return OptionalLong.of(Math.addExact(Math.multiplyExact(aCount, 3), bCount));
                }
            }
            return OptionalLong.empty();
        }
    }

    private long divide(final BigCoordinate greater, final BigCoordinate smaller) {
        return smaller.x() == 0 ? greater.y() / smaller.y() : greater.x() / smaller.x();
    }

    public Game plus(final long addition) {
        return new Game(a(), b(), prize().plus(new BigCoordinate(addition, addition)));
    }
}
