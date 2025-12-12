package com.w_wins.advent.twentytwentyfive.daytwelve;

import com.w_wins.common.Streams;
import com.w_wins.common.Strings;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public record Tree(int largestDimension, int smallestDimension, List<Integer> packageRequirements) {
    public static Tree parseTree(final String line) {
        final List<String> parts = new LinkedList<>(Strings.split(line).toList());
        final String sizeString = parts.removeFirst();
        final int[] sizes = Strings.split(sizeString.substring(0,sizeString.length()-1),"x").mapToInt(Integer::parseInt).toArray();
        Arrays.sort(sizes);
        return new Tree(sizes[1],sizes[0],parts.stream().map(Integer::parseInt).toList());
    }

    public boolean fits(List<Shape> shapes){
        if(largestDimension()*smallestDimension()< Streams.asMap(shapes.stream().mapToInt(Shape::weight)).mapToInt(e->packageRequirements().get(e.getKey())*e.getValue()).sum()){
            return false;
        } else {
            IO.println("Can't determine if fit in "+this);
            return true;
        }
    }
}
