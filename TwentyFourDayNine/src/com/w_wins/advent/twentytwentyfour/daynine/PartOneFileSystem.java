package com.w_wins.advent.twentytwentyfour.daynine;

import com.w_wins.common.CollectionUtil;
import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Maps;
import com.w_wins.common.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record PartOneFileSystem(
        List<Optional<Integer>> blocks) {
    public static PartOneFileSystem parse(final Stream<String> lines) {
        final String line = lines.collect(CollectorUtil.singleton());
        return new PartOneFileSystem(
                new ArrayList<>(
                        Streams.zip(
                                line.chars().map(x -> x - '0').boxed(),
                                Streams.zip(IntStream.iterate(0, x -> x + 1).boxed(), Stream.generate(Optional::<Integer>empty), Functions.entry()).flatMap(entry -> Stream.of(Optional.of(entry.getKey()), entry.getValue())),
                                Functions.entry()
                        ).flatMap(entry -> Stream.of(entry.getValue()).flatMap(value -> IntStream.range(0, entry.getKey()).mapToObj(_ -> value))).toList()
                )
        );
    }

    public long checkSum() {
        return Streams.asMap(Streams.presentInt(blocks.stream().map(element -> element.map(OptionalInt::of)).map(wrap -> wrap.orElse(OptionalInt.of(0))))).map(entry -> entry.getKey() * entry.getValue()).mapToLong(x -> x).sum();
    }

    public void deFragmentBlocks() {
        final Map<Boolean, List<Integer>> partitioning = IntStream.range(0, blocks.size()).boxed().collect(Collectors.partitioningBy(index -> blocks.get(index).isEmpty(), Collectors.toList()));
        Streams.zip(partitioning.get(true).stream(), partitioning.get(false).reversed().stream(), Functions.entry()).forEach(entry -> {
            if (entry.getKey() < entry.getValue()) {
                swapBlock(entry.getKey(), entry.getValue());
            }
        });
    }

    public void deFragmentFiles() {
        final Map<Boolean, List<Integer>> separate = IntStream.range(0, blocks.size()).boxed().collect(Collectors.partitioningBy(index -> blocks.get(index).isEmpty(), Collectors.toList()));
        final List<Integer> gaps = separate.get(true);
        final List<Integer> files = separate.get(false);
        final SortedMap<Integer, Integer> slotsAndSpace = CollectionUtil.runs(gaps);
        final SortedMap<Integer, List<Integer>> fileIdToPositions = files.stream().map(Functions.entry(blocks()::get)).map(Functions.onValue(Optional::orElseThrow)).map(entry -> Maps.entry(entry.getValue(), entry.getKey())).collect(Collectors.groupingBy(Map.Entry::getKey, TreeMap::new, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        fileIdToPositions.reversed().forEach((_, fileIndexes) -> {
            final Optional<Map.Entry<Integer, Integer>> target = slotsAndSpace.entrySet().stream().filter(entry -> entry.getValue() >= fileIndexes.size()).findFirst();
            if (target.isPresent()) {
                final Map.Entry<Integer, Integer> slot = target.orElseThrow();
                final int slotPosition = slot.getKey();
                if (slotPosition < fileIndexes.getFirst()) {
                    swapToSlot(fileIndexes, slotPosition);
                    final int newSize = slot.getValue() - fileIndexes.size();
                    final int newPosition = slotPosition + fileIndexes.size();
                    slotsAndSpace.remove(slotPosition);
                    slotsAndSpace.put(newPosition, newSize);
                }
            }
        });
    }

    private void swapToSlot(final List<Integer> fileIndexes, final int slotPosition) {
        IntStream.range(0, fileIndexes.size()).forEach(offset -> {
            swapBlock(slotPosition + offset, fileIndexes.get(offset));
        });
    }

    private void swapBlock(final int blockTo, final int blockFrom) {
        blocks.set(blockTo, blocks.get(blockFrom));
        blocks.set(blockFrom, Optional.empty());
    }
}
