package com.w_wins.advent.twentytwentyfour.dayseventeen;

public enum Instruction {
    ADV(true), BXL(false), BST(true), JNZ(false), BXC(false), OUT(true), BDV(true), CDV(true);

    private final boolean isCombo;

    Instruction(final boolean setCombo) {
        isCombo = setCombo;
    }

    public boolean isCombo() {
        return isCombo;
    }

    public static Instruction fromOpcode(final int opcode) {
        return values()[opcode];
    }
}
