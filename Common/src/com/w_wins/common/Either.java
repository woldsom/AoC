package com.w_wins.common;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record Either<A, B>(Optional<A> a, Optional<B> b) {
    public Either {
        if ((a.isEmpty() && b.isEmpty()) || (a.isPresent() && b.isPresent())) {
            throw new IllegalArgumentException("Either type can only hold either a or b (and you should use the static factory methods)");
        }
    }

    public static <A, B, C> Function<C, Either<A, B>> eitherOr(final Function<C, Optional<A>> tryA, final Function<C, B> orGetB) {
        return Functions.sequence(tryA.andThen(Functions.onOptionals(Either::a)), Functions.<Function<C, Either<A, B>>, C, Supplier<Either<A, B>>>bindLeft(Functions::bind, orGetB.andThen(Either::b)).andThen(Functions.<BiFunction<Optional<Either<A, B>>, Supplier<Either<A, B>>, Either<A, B>>, Supplier<Either<A, B>>, Function<Optional<Either<A, B>>, Either<A, B>>>bindLeft(Functions::bindRight, Optional::orElseGet)));
    }

    public static <A, B> Either<A, B> a(final A a) {
        return new Either<>(Optional.of(a), Optional.empty());
    }

    public static <A, B> Either<A, B> b(final B b) {
        return new Either<>(Optional.empty(), Optional.of(b));
    }

    public static <A extends Comparable<? super A>, B extends Comparable<? super B>> Comparator<Either<A, B>> compareAsA(final Function<B, A> converter) {
        return (first, second) -> first.map(a -> second.map(a::compareTo, b -> a.compareTo(converter.apply(b))), b -> second.map(converter.apply(b)::compareTo, b::compareTo));
    }

    public <C> C map(final Function<A, C> aMap, final Function<B, C> bMap) {
        return a.map(aMap).orElseGet(() -> b.map(bMap).orElseThrow());
    }

    public static <A extends Comparable<? super A>, B extends Comparable<? super B>> Comparator<Either<A, B>> compareAsB(final Function<A, B> converter) {
        return (first, second) -> first.map(a -> second.map(a::compareTo, converter.apply(a)::compareTo), b -> second.map(a -> b.compareTo(converter.apply(a)), b::compareTo));
    }

    public void consume(Consumer<A> aConsumer, Consumer<B> bConsumer) {
        a().ifPresentOrElse(aConsumer, () -> bConsumer.accept(b().orElseThrow()));
    }

    public boolean isA() {
        return a.isPresent();
    }

    public boolean isB() {
        return b.isPresent();
    }

    public Either<A, B> mapSame(final Function<A, A> aMap, final Function<B, B> bMap) {
        return map(aMap.andThen(Either::a), bMap.andThen(Either::b));
    }

    public A getA(){
        return a.orElseThrow();
    }

    public B getB(){
        return b.orElseThrow();
    }
}
