package com.w_wins.advent.twentytwentyfour.daynine;

import java.util.Map;
import java.util.Optional;

public record Slot(
        int originalOrder,
        int size,
        int position) {
    public Optional<Slot> minus(final File file) {
        final Optional<Slot> newSlot = Optional.of(new Slot(originalOrder(), size() - file.size(), position() + file.size())).filter(slot -> slot.size() > 0);
        //System.err.println(this+" minus "+file+" equals "+newSlot);
        return newSlot;
    }
}
