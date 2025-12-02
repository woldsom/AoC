package com.advent.twentytwentyfour.dayeleven;

import com.w_wins.common.Functions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public final class StonesRecurse {
    private final List<BigInteger> list;
    private final Map<BigInteger, SortedMap<Integer, BigInteger>> counts;

    public StonesRecurse(
            List<BigInteger> setList) {
        list = setList;
        counts = new HashMap<>();
    }

    public static StonesRecurse withList(final List<BigInteger> list) {
        return new StonesRecurse(list);
    }

    public BigInteger blink(final int count) {
        return list().stream().map(stone -> blink(stone, count)).reduce(BigInteger::add).orElse(BigInteger.ZERO);
    }

    private BigInteger blink(final BigInteger stone, final int count) {
        if (count == 0) {
            return BigInteger.ONE;
        }
        final List<BigInteger> ruleResult = new ArrayList<>();
        StonesList.rules(stone, ruleResult::add);
        final BigInteger result;
        final SortedMap<Integer, BigInteger> memo1 = counts.get(stone);
        if (memo1 != null) {
            final BigInteger memo2 = memo1.get(count);
            if (memo2 != null) {
                return memo2;
            }
        }
        if (ruleResult.size() == 1) {
            result = blink(ruleResult.getFirst(), count - 1);
        } else if (ruleResult.size() == 2) {
            result = blink(ruleResult.getFirst(), count - 1).add(blink(ruleResult.getLast(), count - 1));
        } else {
            throw new IllegalArgumentException();
        }
        counts.compute(stone, Functions.createIfMissingAndModify(TreeMap::new, (map, value) -> map.put(count, value), result));
        return result;
    }

    public List<BigInteger> list() {
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (StonesRecurse) obj;
        return Objects.equals(this.list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    @Override
    public String toString() {
        return "StonesAlternative[" +
                "list=" + list + ']';
    }

}
