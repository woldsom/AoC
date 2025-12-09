package com.w_wins.advent.twentytwentyfive.daynine;

import com.w_wins.adventcommon.Coordinate;

import java.util.Comparator;
import java.util.List;

public record Square(Coordinate a, Coordinate b, boolean convex) implements Comparable<Square> {

    public static final Comparator<Square> COMPARATOR = Comparator.comparing(Square::area);

    public static Square thatIsConvex(final Coordinate a, final Coordinate b) {
        return new Square(a, b, true);
    }

    public static Square ofLines(final List<Coordinate> lines) {
        final Coordinate first = lines.get(1).minus(lines.getFirst());
        final Coordinate second = lines.getLast().minus(lines.get(1));
        final int trace = (first.x() + second.x()) * (second.y() - first.y());
        if (trace == 0) {
            return null;
        } else if (trace > 0) {
            return new Square(lines.getFirst(), lines.getLast(), true);
        } else {
            return new Square(lines.getFirst(), lines.getLast(), false);
        }
    }

    public long area() {
        final Coordinate diff = a().minus(b());
        if (convex()) {
            return (Math.abs(diff.x()) + 1L) * (Math.abs(diff.y()) + 1L);
        } else {
            return (Math.abs(diff.x()) - 1L) * (Math.abs(diff.y()) - 1L);
        }
    }

    public boolean intersects(Square square) {
        if (convex() || !square.convex()) {
            throw new IllegalArgumentException("Compared " + square + " using " + this);
        }
        final int minX = Math.min(a().x(), b().x());
        final int minY = Math.min(a().y(), b().y());
        final int maxX = Math.max(a().x(), b().x());
        final int maxY = Math.max(a().y(), b().y());
        final int otherMinX = Math.min(square.a().x(), square.b().x());
        final int otherMinY = Math.min(square.a().y(), square.b().y());
        final int otherMaxX = Math.max(square.a().x(), square.b().x());
        final int otherMaxY = Math.max(square.a().y(), square.b().y());
        final boolean xInside = minX > otherMinX && minX < otherMaxX || maxX < otherMaxX && maxX > otherMinX;
        final boolean yInside = minY > otherMinY && minY < otherMaxY || maxY < otherMaxY && maxY > otherMinY;
        final boolean xContains = maxX >= otherMinX && minX <= otherMinX && minX <= otherMaxX && maxX >= otherMaxX; //??
        final boolean yContains = maxY >= otherMinY && minY <= otherMinY && minY <= otherMaxY && maxY >= otherMaxY; //??
        final boolean returnValue = (xInside || xContains) && (yInside || yContains);
        /*
        if(returnValue){
            IO.println("Intersects! "+this+" , "+square);
        } else {
            IO.println("Not! "+this+" , "+square);
        }*/
        return returnValue;
    }

    @Override
    public int compareTo(final Square o) {
        return COMPARATOR.compare(this, o);
    }
}
