package com.w_wins.common;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class Predicates {
    public static <T, U, R> Predicate<T> bindRight(final BiPredicate<T, U> predicate, final U value) {
        return t -> predicate.test(t, value);
    }

    public static <T, U, R> Predicate<U> bindLeft(final BiPredicate<T, U> predicate, final T value) {
        return u -> predicate.test(value, u);
    }

    public static <T, R> Predicate<T> testing(final Function<T, R> function, Predicate<R> predicate) {
        return t -> predicate.test(function.apply(t));
    }

    public static <R> Predicate<R> allUntil(final Predicate<R> setPredicate) {
        final AtomicBoolean encountered = new AtomicBoolean();
        return record -> {
            if (encountered.getAcquire()) {
                return false;
            } else {
                if (setPredicate.test(record)) {
                    encountered.setRelease(true);
                    return false;
                } else {
                    return true;
                }
            }
        };
    }

    public static <R> Predicate<R> allUntilAfter(final Predicate<R> setPredicate) {
        final AtomicBoolean encountered = new AtomicBoolean();
        return record -> {
            if (encountered.getAcquire()) {
                return false;
            } else {
                if (setPredicate.test(record)) {
                    return !encountered.compareAndExchange(false, true);
                } else {
                    return true;
                }
            }
        };
    }

    public static <R> Predicate<R> pickOne(final Predicate<R> setPredicate) {
        final AtomicBoolean encountered = new AtomicBoolean();
        return record -> {
            if (!encountered.getAcquire()) {
                if (setPredicate.test(record)) {
                    return !encountered.compareAndExchange(false, true);
                }
            }
            return false;
        };
    }

    public static IntPredicate allUntilInt(final IntPredicate setPredicate) {
        final AtomicBoolean encountered = new AtomicBoolean();
        return record -> {
            if (encountered.getAcquire()) {
                return false;
            } else {
                if (setPredicate.test(record)) {
                    encountered.setRelease(true);
                    return false;
                } else {
                    return true;
                }
            }
        };
    }

    public static IntPredicate allUntilAfterInt(final IntPredicate setPredicate) {
        final AtomicBoolean encountered = new AtomicBoolean();
        return record -> {
            if (encountered.getAcquire()) {
                return false;
            } else {
                if (setPredicate.test(record)) {
                    return !encountered.compareAndExchange(false, true);
                } else {
                    return true;
                }
            }
        };
    }

    public static IntPredicate pickOneInt(final IntPredicate setPredicate) {
        final AtomicBoolean encountered = new AtomicBoolean();
        return record -> {
            if (!encountered.getAcquire()) {
                if (setPredicate.test(record)) {
                    return !encountered.compareAndExchange(false, true);
                }
            }
            return false;
        };
    }

    public static IntPredicate not(final IntPredicate original) {
        return x -> !original.test(x);
    }
}
