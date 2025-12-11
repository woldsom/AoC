package com.w_wins.time;

import com.w_wins.common.Strings;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DurationFormatter {
    public static final List<String> METRIC_PREFIX = Strings.characters("nμm kMG").map(c -> Character.toString(c)).toList();
    public static final Map<ChronoUnit,String> CHRONO_UNIT=Map.of(ChronoUnit.HOURS,"h",ChronoUnit.MINUTES,"m",ChronoUnit.SECONDS,"s",ChronoUnit.MILLIS,"ms",ChronoUnit.MICROS,"μs",ChronoUnit.NANOS,"ns");

    public static String format(final Duration duration) {
        final List<ChronoUnit> relevantUnits = new ArrayList<>(Arrays.stream(ChronoUnit.values()).filter(u -> u.isTimeBased() && u.getDuration().compareTo(duration) <= 0).sorted(Comparator.comparing(ChronoUnit::getDuration).reversed()).toList());
        final List<String> parts = new ArrayList<>();
        Duration remainder = Duration.ofNanos(duration.toNanos());
        while (remainder.compareTo(Duration.ZERO) > 0) {
            final ChronoUnit currentUnit = relevantUnits.removeFirst();
            final long unit = currentUnit.getDuration().getNano();
            final long value = remainder.getNano() / unit;
            remainder = Duration.ofNanos(remainder.getNano() - value * unit);
            parts.add(value+CHRONO_UNIT.getOrDefault(currentUnit, currentUnit.name().toLowerCase()));
        }
        return String.join(" ", parts);
    }
}
