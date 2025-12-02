package com.w_wins.advent.twentytwentyfour.daynine;

public record File(
        int originalOrder,
        int size,
        int position) {
    public long checkSum() {
        final long checkSum = Math.multiplyExact(Math.addExact(Math.multiplyExact(size(), (long) position()), (long) size() * (size() - 1) / 2), originalOrder() / 2);
        //System.err.println("Checksum " + checkSum + " for " + this);
        return checkSum;
    }

    public File movedTo(final Slot slot) {
        return new File(originalOrder(),size(), slot.position());
    }
}
