package com.w_wins.advent.twentytwentyfive.dayeleven;

public record Shortcut(Device start, Device end) {
    public Shortcut reversed() {
        return new Shortcut(end(),start());
    }

    @Override
    public String toString() {
        return "Shortcut{" +
                "start=" + start.label() +
                ", end=" + end.label() +
                '}';
    }
}
