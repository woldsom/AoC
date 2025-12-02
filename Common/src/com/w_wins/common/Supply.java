package com.w_wins.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class Supply {
    public static <T> Supplier<Set<T>> set() {
        return HashSet::new;
    }

    public static <T> Supplier<List<T>> list() {
        return ArrayList::new;
    }
}
