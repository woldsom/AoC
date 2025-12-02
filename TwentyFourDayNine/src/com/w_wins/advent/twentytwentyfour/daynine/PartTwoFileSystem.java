package com.w_wins.advent.twentytwentyfour.daynine;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Either;
import com.w_wins.common.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record PartTwoFileSystem(
        List<Either<File, Slot>> blocks) {
    public static PartTwoFileSystem parse(final Stream<String> lines) {
        return parse(lines.collect(CollectorUtil.singleton()));
    }

    public static PartTwoFileSystem parse(final String line) {
        return build(line.chars().map(x -> x - '0'));
    }

    private static PartTwoFileSystem build(final IntStream input) {
        final List<Either<File, Slot>> blocks = new ArrayList<>();
        final AtomicInteger sum = new AtomicInteger();
        Streams.asMap(input).forEachOrdered(entry -> {
            if (entry.getKey() % 2 == 0) {
                blocks.add(Either.a(new File(entry.getKey(), entry.getValue(), sum.get())));
            } else {
                blocks.add(Either.b(new Slot(entry.getKey(), entry.getValue(), sum.get())));
            }
            sum.addAndGet(entry.getValue());
        });
        return new PartTwoFileSystem(blocks);
    }

    public void deFragmentFiles() {
        final List<File> filesInReverseOrder = files().toList();
        filesInReverseOrder.forEach(file -> {
            slots().filter(slot1 -> slot1.size() >= file.size()).findFirst().ifPresent(slot -> {
                blocks().remove(Either.a(file));
                blocks().add(blocks().indexOf(Either.b(slot)), Either.a(file.movedTo(slot)));
                slot.minus(file).ifPresentOrElse(newSlot -> blocks().set(blocks().indexOf(Either.b(slot)), Either.b(newSlot)), () -> blocks().remove(Either.b(slot)));
            });
        });
        //System.err.println("After defrag, slots are"+slots().toList());
    }

    public Stream<File> files() {
        return blocks().reversed().stream().mapMulti((either, consumer) -> either.consume(consumer, _ -> {
        }));
    }

    public Stream<Slot> slots() {
        return blocks().stream().mapMulti((either, consumer) -> either.consume(_ -> {
        }, consumer));
    }

    public long checkSum() {
        return files().mapToLong(File::checkSum).sum();
    }
}
