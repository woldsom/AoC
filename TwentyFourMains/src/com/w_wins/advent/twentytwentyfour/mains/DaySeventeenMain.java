package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.dayseventeen.Computer;
import com.w_wins.advent.twentytwentyfour.dayseventeen.Execution;
import com.w_wins.advent.twentytwentyfour.dayseventeen.LocalResult;
import com.w_wins.common.AtomicBest;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Predicates;
import com.w_wins.common.Streams;
import com.w_wins.common.Unsupported;
import com.w_wins.iostream.Utf8ResourceLines;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class DaySeventeenMain {
    private static final Map<Computer, Computer> memoC = new HashMap<>();
    private static final Map<Computer, BigInteger> memoO = new HashMap<>();

    private static List<BigInteger> checkEnd(final BigInteger newA, final Computer computer, final AtomicBest<List<BigInteger>> best) {
        //System.out.print(newA+" : ");
        final Computer butA = computer.copyWithNewA(newA);
        final List<Integer> program = butA.program();
        final List<BigInteger> candidate = run2(butA);
        //System.out.println(candidate);
        if (program.stream().map(BigInteger::valueOf).toList().equals(candidate)) {
            System.out.println(newA);
            System.exit(0);
        }
        final long count = Streams.zip(program.reversed().stream().map(BigInteger::valueOf), candidate.reversed().stream(), BigInteger::equals).filter(Predicates.allUntil(Boolean.FALSE::equals)).count();
        if (best.isBest(count, candidate)) {
            System.err.println("Best! " + count + " matches in A " + newA + " output was " + candidate + "  " + newA.toString(2));
        }
        return candidate.subList(candidate.size() - Math.toIntExact(count), candidate.size());
    }

    private static List<BigInteger> checkStart(final BigInteger newA, final Computer computer, final AtomicBest<List<BigInteger>> best) {
        //System.out.print(newA+" : ");
        final Computer butA = computer.copyWithNewA(newA);
        final List<Integer> program = butA.program();
        final List<BigInteger> candidate = run2(butA);
        //System.out.println(candidate);
        if (program.stream().map(BigInteger::valueOf).toList().equals(candidate)) {
            System.out.println(newA);
            System.exit(0);
        }
        final long count = Streams.zip(program.stream().map(BigInteger::valueOf), candidate.stream(), BigInteger::equals).filter(Predicates.allUntil(Boolean.FALSE::equals)).count();
        if (best.isBest(count, candidate)) {
            System.err.println("Best! " + count + " matches in A " + newA + " output was " + candidate + "  " + newA.toString(2));
        }
        return candidate.subList(0, Math.toIntExact(count));
    }

    private static List<BigInteger> run2(final Computer butA) {
        return butA.newExecution().runWithMemos(memoO, memoC);
    }

    private static Set<LocalResult> findPrefixes(final int exp, final Set<BigInteger> tails, final Computer computer, final AtomicBest<List<BigInteger>> best, final int goal) {
        System.err.println("Set of " + tails.size());
        final BigInteger pow = BigInteger.TWO.pow(exp);
        return Stream.iterate(BigInteger.ZERO, x -> x.add(BigInteger.ONE)).map(x -> x.multiply(pow)).flatMap(x -> {
            return tails.stream().map(x::add);
        }).map(Functions.entry(newA -> checkStart(newA, computer, best))).map(LocalResult::from).<Set<LocalResult>>gather(Gatherer.of(AtomicReference::new, (ref, val, _) -> {
            final Set<LocalResult> old = ref.get();
            if (old == null || old.isEmpty() || val.output().size() > old.stream().findAny().orElseThrow().output().size()) {
                if (val.output().size() > goal) {
                    return false;
                }
                ref.set(new HashSet<>(Set.of(val)));
            } else {
                if (val.output().size() == old.stream().findAny().orElseThrow().output().size()) {
                    old.add(val);
                }
            }
            return true;
        }, Unsupported.binaryOperation(), (AtomicReference<Set<LocalResult>> a, Gatherer.Downstream<? super Set<LocalResult>> b) -> b.push(a.get()))).collect(CollectorUtil.<Set<LocalResult>>singleton());
    }

    private static Set<LocalResult> findSuffixes(final Set<BigInteger> heads, final Computer computer, final AtomicBest<List<BigInteger>> best, final int goal) {
        System.err.println("Set of " + heads.size());
        return Stream.iterate(BigInteger.ZERO, x -> x.add(BigInteger.ONE)).flatMap(x -> {
            return heads.stream().map(head -> head.shiftLeft(x.bitLength()).add(x));
        }).map(Functions.entry(newA -> checkEnd(newA, computer, best))).map(LocalResult::from).<Set<LocalResult>>gather(Gatherer.of(AtomicReference::new, (ref, val, _) -> {
            final Set<LocalResult> old = ref.get();
            if (old == null || old.isEmpty() || val.output().size() > old.stream().findAny().orElseThrow().output().size()) {
                if (val.output().size() > goal) {
                    return false;
                }
                ref.set(new HashSet<>(Set.of(val)));
            } else {
                if (val.output().size() == old.stream().findAny().orElseThrow().output().size()) {
                    old.add(val);
                }
            }
            return true;
        }, Unsupported.binaryOperation(), (AtomicReference<Set<LocalResult>> a, Gatherer.Downstream<? super Set<LocalResult>> b) -> b.push(a.get()))).collect(CollectorUtil.<Set<LocalResult>>singleton());
    }

    private static void searchByFumble(final Computer computerTwo, AtomicBest<List<BigInteger>> best, final Computer computerOne) {
        int goal = 4;
        int exp = 0;
        Set<BigInteger> set = new HashSet<>(Set.of(BigInteger.ZERO));
        while (true) {
            Set<LocalResult> res = findPrefixes(exp, set, computerTwo, best, goal);
            System.out.println(res.size() + ":" + res);
            res.stream().map(LocalResult::a).map(x -> x.toString(2)).forEach(System.out::println);
            best = new AtomicBest<>();
            ++goal;
            set = res.stream().map(LocalResult::a).map(x -> x.and(BigInteger.TWO.pow(exp).subtract(BigInteger.ONE))).collect(Collectors.toSet());
            if (!computerOne.debug()) {
                break;
            }
        }
        while (true) {
            Set<LocalResult> res = findSuffixes(set, computerTwo, best, goal);
            System.out.println(res.size() + ":" + res);
            res.stream().map(LocalResult::a).map(x -> x.toString(2)).forEach(System.out::println);
            best = new AtomicBest<>();
            ++goal;
            Stream<LocalResult> result;
            Collection<LocalResult> localResults = res;
            //result = StreamSupport.stream(spliterator(), false);
            set = res.stream().map(LocalResult::a).map(x -> x.shiftRight(1)).collect(Collectors.toSet());
            throw new IllegalStateException();
        }
    }

    void main() {
        final Computer computerOne = new Utf8ResourceLines(Computer.class, "/example.txt").evaluate(Computer::parse);
        final Computer computerTwo = computerOne.copy();
        final Execution part1 = computerOne.newExecution();
        part1.dump();
        final List<BigInteger> output = part1.runMemo();
        System.out.println("Part1:" + StreamSupport.stream(output.spliterator(), false).map(Objects::toString).collect(Collectors.joining(",")));
        System.out.println(computerTwo.copyWithNewA(new BigInteger("202393820604970")).newExecution().runWithMemos(memoO,memoC));
        System.out.println(computerTwo.program());
        final List<Integer> a2 = computerTwo.findA3();
        final List<Integer> a3 = new ArrayList<>(a2);
/*
        IntStream.range(0, 8).forEach(z -> IntStream.range(0, 8).forEach(zz -> IntStream.range(0, 8).forEach(d -> IntStream.range(0, 8).forEach(e -> IntStream.range(0, 8).forEach(g -> IntStream.range(0, 8).forEach(h -> IntStream.range(0, 8).forEach(i -> IntStream.range(0,8).forEach(j->{
            //System.err.println(a3);
            final BigInteger fish = Computer.getFish(a3);
            //System.err.println(fish);
            final List<BigInteger> one = computerTwo.copyWithNewA(fish).newExecution().runWithMemos(memoO, memoC);
            //            System.err.println(one + " should match\n" + computerTwo.program());
            final List<BigInteger> two = computerTwo.program().stream().map(BigInteger::valueOf).toList();
            boolean match = one.equals(two);
            if (match) {
                throw new IllegalStateException("FOUND:" + fish);
            }*/
            /*
            if(one.subList(0,one.size()-1).equals(two.subList(0,two.size()-1))) {
                System.err.println("CLOSE "+fish+"\n "+a3+"\n  "+one);
            }




        })))))))); */
        /*
        final BigInteger part2Answer = computerTwo.findA2();
        System.out.println("Part 2:" + part2Answer);
        final Computer verify = computerTwo.copyWithNewA(part2Answer.add(BigInteger.valueOf(0)));
        final List<BigInteger> result = run2(verify);
        System.err.println("verification:" + result + " (should be " + verify.program() + ")");
        BigInteger current = part2Answer;//new BigInteger("88380610193962");
        while (true) {
            final List<BigInteger> one = computerTwo.copyWithNewA(current).newExecution().runWithMemos(memoO, memoC);
            final List<BigInteger> two = verify.program().stream().map(BigInteger::valueOf).toList();
            if (one.equals(two)) {
                break;
            }
            System.err.println(one);
            System.err.println(two);
            System.err.println();
            if (one.size() != two.size()) {
                break;
            }
            if (current.compareTo(new BigInteger("98380610193962")) > 0) {
                break;
            }
            current = current.add(new BigInteger("4398046511104"));
            System.err.println(current);
        }*/
    }

}
