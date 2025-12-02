package com.w_wins.common;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class StreamUtil {
    public static <R,U> Consumer<R> accumulate(U initial, BiFunction<U,R,U> accumulator){
        return new Accumulator<>(initial, accumulator);
    };
}
