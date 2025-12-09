package com.w_wins.advent.twentytwentyfive.daynine;

import com.w_wins.adventcommon.Coordinate;

import java.util.Comparator;
import java.util.List;

public record Rectangle(Coordinate a, Coordinate b, boolean convex) implements Comparable<Rectangle> {

    public static final Comparator<Rectangle> COMPARATOR = Comparator.comparing(Rectangle::area);

    public static Rectangle thatIsConvex(final Coordinate a, final Coordinate b) {
        return new Rectangle(a, b, true);
    }

    public static Rectangle ofLines(final List<Coordinate> lines) {
        final Coordinate first = lines.get(1).minus(lines.getFirst());
        final Coordinate second = lines.getLast().minus(lines.get(1));
        final int trace = (first.x() + second.x()) * (second.y() - first.y());
        if (trace == 0) {
            return null;
        } else if (trace > 0) {
            return new Rectangle(lines.getFirst(), lines.getLast(), true);
        } else {
            return new Rectangle(lines.getFirst(), lines.getLast(), false);
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

    public boolean intersects(Rectangle rectangle) {
        if (convex() || !rectangle.convex()) {
            throw new IllegalArgumentException("Compared " + rectangle + " using " + this);
        }
        final int minX = Math.min(a().x(), b().x());
        final int minY = Math.min(a().y(), b().y());
        final int maxX = Math.max(a().x(), b().x());
        final int maxY = Math.max(a().y(), b().y());
        final int otherMinX = Math.min(rectangle.a().x(), rectangle.b().x());
        final int otherMinY = Math.min(rectangle.a().y(), rectangle.b().y());
        final int otherMaxX = Math.max(rectangle.a().x(), rectangle.b().x());
        final int otherMaxY = Math.max(rectangle.a().y(), rectangle.b().y());
        final boolean xInside = minX > otherMinX && minX < otherMaxX || maxX < otherMaxX && maxX > otherMinX;
        final boolean yInside = minY > otherMinY && minY < otherMaxY || maxY < otherMaxY && maxY > otherMinY;
        final boolean xContains = maxX >= otherMinX && minX <= otherMinX && minX <= otherMaxX && maxX >= otherMaxX; //??
        final boolean yContains = maxY >= otherMinY && minY <= otherMinY && minY <= otherMaxY && maxY >= otherMaxY; //??
        return (xInside || xContains) && (yInside || yContains);
    }

    @Override
    public int compareTo(final Rectangle o) {
        return COMPARATOR.compare(this, o);
    }
}
