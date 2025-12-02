package com.w_wins.advent.twentytwentyfour.dayseventeen;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Streams;
import com.w_wins.common.Unsupported;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public record Computer(
        List<BigInteger> registers,
        List<Integer> program,
        boolean debug) {
    private static final Set<BigInteger> spy = new HashSet<>();
    private static final Map<Computer, BigInteger> memoO = new HashMap<>();
    private static final Map<Computer, Computer> memoC = new HashMap<>();

    public static Computer parse(Stream<String> lines) {
        return lines.gather(Gatherer.of(() -> new Computer(new ArrayList<>(), new ArrayList<>(), false), (Computer computer, String line, Gatherer.Downstream<? super Computer> downstream) -> {
            if (computer.registers().size() == 3) {
                if (!line.isBlank()) {
                    Streams.split(line.substring(line.indexOf(":") + 2), ",").map(Integer::parseInt).forEach(computer.program()::add);
                }
            } else {
                computer.registers().add(Streams.split(line, ": ").skip(1).map(BigInteger::new).collect(CollectorUtil.singleton()));
            }
            return true;
        }, Unsupported.binaryOperation(), (Computer a, Gatherer.Downstream<? super Computer> b) -> b.push(a))).collect(CollectorUtil.singleton());
    }

    public static BigInteger getFish(final List<Integer> joining) {
        final BigInteger fish = joining.reversed().stream().<BigInteger>gather(Gatherer.of(() -> new AtomicReference<>(BigInteger.ZERO), (sum, nextInt, downstream) -> {
            sum.set(sum.get().multiply(BigInteger.valueOf(8)).add(BigInteger.valueOf(nextInt)));
            return true;
        }, Unsupported.binaryOperation(), (x, a) -> a.push(x.get()))).collect(CollectorUtil.singleton());
        return fish;
    }

    public Execution newExecution() {
        return new Execution(this);
    }

    public BigInteger a() {
        return registers().get(0);
    }

    public BigInteger b() {
        return registers().get(1);
    }

    public BigInteger c() {
        return registers().get(2);
    }

    public void setA(final BigInteger value) {
        debug(() -> "A set to " + value);
        registers().set(0, value);
    }

    public void debug(final String output) {
        if (debug()) {
            System.err.println(output);
        }
    }

    public void debug(final Supplier<String> output) {
        if (debug()) {
            System.err.println(output.get());
        }
    }

    public void setB(final BigInteger value) {
        debug(() -> "B set to " + value);
        registers().set(1, value);
    }

    public void setC(final BigInteger value) {
        debug(() -> "C set to " + value);
        registers().set(2, value);
    }

    public Computer copyWithNewA(final BigInteger newA) {
        final Computer copy = copy();
        copy.setA(newA);
        return copy;
    }

    public Computer copy() {
        return new Computer(new ArrayList<>(registers()), new ArrayList<>(program()), false);
    }

    public Optional<BigInteger> findA() {
        final SortedSet<CombinedMask> maskTypes = IntStream.range(0, 7).mapToObj(lowestBits -> CombinedMask.of(AMask.of(lowestBits), BMask.of(lowestBits))).collect(Collectors.toCollection(TreeSet::new));
        final List<CombinedMask> maskMasterList = new ArrayList<>(maskTypes);
        final Map<Integer, List<CombinedMask>> choices = IntStream.range(0, program().size()).boxed().map(Functions.<Integer, List<CombinedMask>>entry(_ -> new ArrayList<>(maskMasterList))).collect(CollectorUtil.toMap());
        final Constraints totalConstraints = new Constraints(new HashMap<>(), new HashMap<>());
        return recurseFill(totalConstraints, 0, choices).orElseThrow().lowestA(program());
    }

    private Optional<Constraints> recurseFill(final Constraints sum, final int bIndex, final Map<Integer, List<CombinedMask>> choices) {
        for (final CombinedMask rule : choices.get(bIndex)) {
            Constraints mine = null;
            try {
                final Constraints choice = Constraints.fromMask(rule, bIndex);
                mine = sum.merge(choice);
            } catch (
                    ConstraintConflictException _) {
            }
            if (mine != null) {
                if (bIndex < program().size() - 1) {
                    final Optional<Constraints> subResult = recurseFill(mine, bIndex + 1, choices);
                    if (subResult.isPresent()) {
                        return subResult;
                    }
                } else {
                    final Constraints finalMine = mine;
                    final BigInteger newA = mine.lowestA(program()).orElseThrow(() -> new IllegalStateException("Got no number from " + finalMine));
                    final Computer innerVerify = copyWithNewA(newA);
                    final List<BigInteger> result2 = innerVerify.newExecution().runMemo();
                    System.err.println("Leaf " + newA + " rules:" + mine);
                    if (!spy.add(newA)) {
                        throw new IllegalStateException("An A was doubled; " + newA + " -- " + spy);
                    }
                    if (result2.equals(program().stream().map(BigInteger::valueOf).toList())) {
                        return Optional.of(mine);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public BigInteger findA2() {
        final List<Integer> joining = findA3();
        final BigInteger fish = getFish(joining);
        return fish;
    }

    public List<Integer> findA3() {
        final Map<Integer, Set<Integer>> productions = new HashMap<>();
        IntStream.range(0, 1024).forEach(a -> {
            final BigInteger bh = copyWithNewA(BigInteger.valueOf(a)).newExecution().runWithMemos(memoO, memoC).getFirst();
            productions.compute(bh.intValueExact(), Functions.createIfMissingAndModify(HashSet::new, Set::add, a));
        });
        final Map<Integer, Map<Integer, Set<Integer>>> bigMap = productions.entrySet().stream().map(e -> {
            return Map.entry(e.getKey(), e.getValue().stream().mapToInt(x -> x)
                    .map(x -> x & 7)
                    .distinct().boxed().map(Functions.entry(mod -> {
                        return e.getValue().stream()
                                .filter(x -> (x & 7) == mod)
                                .collect(Collectors.toSet());
                    })).collect(CollectorUtil.toMap()));
        }).collect(CollectorUtil.<Integer, Map<Integer, Set<Integer>>>toMap());
        final List<Map<Integer, Set<Integer>>> parts = StreamSupport.stream(((Collection<Integer>) this.program()).spliterator(), false).map(bigMap::get).toList();
        final List<Integer> joining = join(parts);
        System.err.println("Joining:" + joining);
        return joining;
    }

    private List<Integer> join(final List<Map<Integer, Set<Integer>>> parts) {
        final Set<Integer> all = Set.of(0, 1, 2, 3, 4, 5, 6, 7);
        final Set<Integer> doubleAll = StreamSupport.stream(all.spliterator(), false).flatMap(a -> {
            return StreamSupport.stream(all.spliterator(), false).map(b -> a * 8 + b);
        }).collect(Collectors.toSet());
        final Set<List<Integer>> result = recurse(parts, doubleAll, all, 0);
        final List<Integer> listToFix = StreamSupport.stream(result.spliterator(), false).findAny().orElse(new ArrayList<>(List.of(9999)));
        final SortedSet<BigInteger> matches = new TreeSet<>();
        StreamSupport.stream(result.spliterator(), false).map(Computer::getFish).forEach(a -> {
            final Computer computer = copyWithNewA(a);
            final List<BigInteger> vresult = computer.newExecution().runWithMemos(memoO, memoC);
            final List<BigInteger> two = StreamSupport.stream(((Collection<Integer>) this.program()).spliterator(), false).map(BigInteger::valueOf).toList();
            System.err.println("" + vresult + two + a);
            if (vresult.equals(two)) {
                System.out.println("WE GOT ONE" + a);
                matches.add(a);
            }
        });
        System.out.println(matches.first()+" is smallest of "+matches.size());
        //listToFix.removeFirst();
        /*
        if (!listToFix.getLast().equals(0)) {
            listToFix.addLast(0);
        }

        if (!listToFix.getFirst().equals(0)) {
            listToFix.addFirst(0);
        }
         */
        return listToFix;
    }

    private Set<List<Integer>> recurse(final List<Map<Integer, Set<Integer>>> parts, Set<Integer> pred, Set<Integer> grandPred, final int index) {
        if (parts.size() <= index) {
            return new HashSet<>(Set.<List<Integer>>of(new ArrayList<>()));
        }
        final Map<Integer, Set<Integer>> choices = parts.get(index);
        final HashSet<List<Integer>> returnValue = new HashSet<>();
        final Set<Integer> cleanPred = clean(pred);
        final Set<Integer> cleanGrandPred = clean(grandPred);
        final List<Integer> validChoices = StreamSupport.stream(((Collection<Integer>) choices.keySet()).spliterator(), false).filter(cleanPred::contains).filter(cleanGrandPred::contains).toList();
        System.err.println("\n" + index + "\n" + validChoices + "\n" + cleanPred + "\n" + grandPred);
        if (validChoices.isEmpty()) {
            return returnValue;
        }
        for (final int choice : validChoices) {
            Collection<Integer> integers = choices.get(choice);
            final Set<Integer> valid = StreamSupport.stream(integers.spliterator(), false).map(i -> (i / 8)).collect(Collectors.toSet());
            final Set<List<Integer>> result = recurse(parts, valid, StreamSupport.stream(pred.spliterator(), false).map(x -> x / 8).collect(Collectors.toSet()), index + 1);
            final Set<List<Integer>> cleanResult = StreamSupport.stream(result.spliterator(), false).map(list -> {
                final List<Integer> list2 = new ArrayList<>(list);
                list2.addFirst(choice);
                return list2;
            }).collect(Collectors.toSet());
            //result.forEach(System.err::println);
            returnValue.addAll(cleanResult);
            /*
            if (!returnValue.isEmpty()) {
                return returnValue; // short circuit instead of finding all
            }*/
        }
        return returnValue;
    }

    private Set<Integer> clean(final Set<Integer> pred) {
        return StreamSupport.stream(pred.spliterator(), false).map(x -> x % 8).collect(Collectors.toSet());
    }
}
