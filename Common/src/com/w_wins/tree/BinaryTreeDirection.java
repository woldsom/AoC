package com.w_wins.tree;

import com.w_wins.common.ArrayMap;
import com.w_wins.common.CollectionUtil;

import java.util.Map;

public enum BinaryTreeDirection {
    LEFT,RIGHT, CURRENT_NODE;
    public static <F> Map<BinaryTreeDirection,F> wrapAsMap(F left,F right, F current) {
        return CollectionUtil.keyWrap(new ArrayMap<>((F[])new Object[]{left,right,current}),Enum::ordinal, i -> BinaryTreeDirection.values()[i]);
    }
}
