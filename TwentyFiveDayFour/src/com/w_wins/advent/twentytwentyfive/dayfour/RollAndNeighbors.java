package com.w_wins.advent.twentytwentyfive.dayfour;

import com.w_wins.adventcommon.Coordinate;

import java.util.Objects;
import java.util.Set;

public record RollAndNeighbors(Coordinate position, Set<Coordinate> neighbours) {

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (RollAndNeighbors) obj;
        return Objects.equals(this.position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public String toString() {
        return "Roll[at "+position()+" with neighbours]"+neighbours();
    }
}
