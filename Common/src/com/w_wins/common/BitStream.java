package com.w_wins.common;

import java.util.stream.IntStream;

public final class BitStream {
    public static IntStream decode(final int packedInt) {
        if (packedInt < 0) {
            return IntStream.concat(IntStream.of(1), decodeRecurse(packedInt & Integer.MAX_VALUE, 32).skip(1));
        } else {
            return decodeRecurse(packedInt,32);
        }
    }

    public static IntStream decodeRecurse(final int packedInt, final int bits) {
        if(packedInt==0){
            return IntStream.range(0,bits).map(x->0);
        } else if(bits==1) {
            return IntStream.of(1);
        } else {
            final int maskBits = bits >> 1;
            return IntStream.concat(decodeRecurse(packedInt >> maskBits, maskBits),decodeRecurse(packedInt ^ (packedInt >> maskBits << maskBits), maskBits));
        }
    }

    public static IntStream fromHex(final String hexPackets) {
        final IntStream[] builder = new IntStream[]{IntStream.range(42, 42)};
        hexPackets.chars().map(c -> c >= '0' && c <= '9' ? c - '0' : c + 10 - 'A').forEach(x -> builder[0] = IntStream.concat(builder[0], decodeRecurse(x, 4)));
        return builder[0];
    }
}
