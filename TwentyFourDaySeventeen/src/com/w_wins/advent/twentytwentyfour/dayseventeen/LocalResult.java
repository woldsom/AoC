package com.w_wins.advent.twentytwentyfour.dayseventeen;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public record LocalResult(
        BigInteger a,
        List<BigInteger> output) {
    public static LocalResult from(Map.Entry<BigInteger, List<BigInteger>> result) {
        return new LocalResult(result.getKey(),result.getValue());
    }
}
