package com.w_wins.advent.twentytwentyfive.dayfive;

import java.math.BigInteger;

public record CountAndRange(BigInteger count, Range uncombined) {
    public static CountAndRange fromRange(Range range) {
        return new CountAndRange(BigInteger.ZERO,range);
    }

    public CountAndRange combine(final CountAndRange next) {
        final CountAndRange result;
        if(uncombined().contains(next.uncombined().startInclusive())){
            if(uncombined().contains(next.uncombined().endInclusive())){
                result= this;
            } else {
                result = new CountAndRange(count(), new Range(uncombined().startInclusive(), next.uncombined().endInclusive()));
            }
        } else {
            result= new CountAndRange(count().add(uncombined().count()),next.uncombined());
        }
        IO.println("Combining "+this+" and "+next+" returning "+result);
        return result;
    }

    public BigInteger total() {
        return count().add(uncombined().count());
    }
}
