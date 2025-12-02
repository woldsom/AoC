package com.w_wins.advent.twentytwentyfour.dayseventeen;

import com.w_wins.common.CollectorUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;

public final class Execution {
    public static final BigInteger EIGHT = BigInteger.valueOf(8);
    private Computer computer;
    private int instructionPointer;

    public Execution(final Computer setComputer) {
        computer = setComputer;
        instructionPointer = 0;
    }

    public BigInteger runBackwards() {
        final Computer reverse = new Computer(new ArrayList<>(List.of(BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO)),computer.program(),false);
        computer.program().reversed().stream().map(BigInteger::valueOf).forEach(targetNumber ->{
            reverse.setB(targetNumber);
            reverse.setA(reverse.b());
        });
        return BigInteger.ZERO;
    }

    public List<BigInteger> runMemo() {
        final Map<Computer,BigInteger> memoO = new HashMap<>();
        final Map<Computer,Computer> memoC = new HashMap<>();
        return runWithMemos(memoO, memoC);
    }

    public List<BigInteger> runWithMemos(final Map<Computer, BigInteger> memoO, final Map<Computer, Computer> memoC) {
        final List<BigInteger> outputs = new ArrayList<>();
        do {
            final BigInteger output = memoO.get(computer);
            if(output==null) {
                final Computer initial = computer.copy();
                final BigInteger newOutput = runOnce();
                outputs.add(newOutput);
                memoO.put(initial,newOutput);
                memoC.put(initial,computer.copy());
            } else {
                computer = memoC.get(computer);
                outputs.add(output);
            }
        }while(!computer.a().equals(BigInteger.ZERO));
        return outputs;
    }

    public BigInteger runOnce() {
        final List<BigInteger> output = new ArrayList<>();
        int count = 0;
        instructionPointer = 0;
        while (instructionPointer < computer.program().size()) {
            final Instruction instruction = Instruction.fromOpcode(computer.program().get(instructionPointer));
            final int originalOperand = computer.program().get(instructionPointer + 1);
            final BigInteger operand = instruction.isCombo() ? combo(originalOperand) : BigInteger.valueOf(originalOperand);
            computer.debug(()->instruction.name() + " " + operand + (instruction.isCombo() ? (" (" + originalOperand + ")") : ""));
            try {
                switch (instruction) {
                    case ADV:
                        computer.setA(computer.a().divide(BigInteger.TWO.pow(operand.intValueExact())));
                        break;
                    case BXL:
                        computer.setB(computer.b().xor(operand));
                        break;
                    case BST:
                        computer.setB(operand.mod(EIGHT));
                        break;
                    case JNZ:
                        return output.stream().collect(CollectorUtil.singleton());
                    case BXC:
                        computer.setB(computer.b().xor(computer.c()));
                        break;
                    case OUT:
                        output.add(operand.mod(EIGHT));
                        break;
                    case BDV:
                        computer.setB(computer.a().divide(BigInteger.TWO.pow(operand.intValueExact())));
                        break;
                    case CDV:
                        computer.setC(computer.a().divide(BigInteger.TWO.pow(operand.intValueExact())));
                        break;
                    default:
                        throw new UnsupportedOperationException("Instruction: " + instruction + " (at " + instructionPointer + ")");
                }
            } catch (final RuntimeException re) {
                System.err.println("When at "+instructionPointer+" "+instruction+" operand "+operand+" computer "+computer);
                throw re;
            }
            instructionPointer += 2;
            if (++count > 100 && computer.debug()) {
                throw new IllegalStateException("Run time exceeded");
            }
        }
        return BigInteger.ZERO.subtract(BigInteger.ONE);
    }

    public List<BigInteger> runToHalt() {
        final List<BigInteger> output = new ArrayList<>();
        int count = 0;
        while (instructionPointer < computer.program().size()) {
            final Instruction instruction = Instruction.fromOpcode(computer.program().get(instructionPointer));
            final int originalOperand = computer.program().get(instructionPointer + 1);
            final BigInteger operand = instruction.isCombo() ? combo(originalOperand) : BigInteger.valueOf(originalOperand);
            computer.debug(()->instruction.name() + " " + operand + (instruction.isCombo() ? (" (" + originalOperand + ")") : ""));
            try {
                switch (instruction) {
                    case ADV:
                        computer.setA(computer.a().divide(BigInteger.TWO.pow(operand.intValueExact())));
                        break;
                    case BXL:
                        computer.setB(computer.b().xor(operand));
                        break;
                    case BST:
                        computer.setB(operand.mod(EIGHT));
                        break;
                    case JNZ:
                        if (!BigInteger.ZERO.equals(computer.a())) {
                            instructionPointer = operand.subtract(BigInteger.TWO).intValueExact();
                        }
                        break;
                    case BXC:
                        computer.setB(computer.b().xor(computer.c()));
                        break;
                    case OUT:
                        output.add(operand.mod(EIGHT));
                        break;
                    case BDV:
                        computer.setB(computer.a().divide(BigInteger.TWO.pow(operand.intValueExact())));
                        break;
                    case CDV:
                        computer.setC(computer.a().divide(BigInteger.TWO.pow(operand.intValueExact())));
                        break;
                    default:
                        throw new UnsupportedOperationException("Instruction: " + instruction + " (at " + instructionPointer + ")");
                }
            } catch (final RuntimeException re) {
                System.err.println("When at "+instructionPointer+" "+instruction+" operand "+operand+" computer "+computer);
                throw re;
            }
            instructionPointer += 2;
            if (++count > 100 && computer.debug()) {
                throw new IllegalStateException("Run time exceeded");
            }
        }
        return output;
    }

    private BigInteger combo(final int operand) {
        if (operand <= 3) {
            return BigInteger.valueOf(operand);
        } else {
            return computer.registers().get(operand - 4);
        }
    }

    private String comboDump(final int operand) {
        if (operand <= 3) {
            return BigInteger.valueOf(operand).toString();
        } else {
            final char register = (char) ('A' + (operand - 4));
            return register + " initially " + computer.registers().get(operand - 4);
        }
    }

    public void dump() {
        System.err.println("Program listing at ip " + instructionPointer);
        IntStream.range(0, computer.program().size() / 2).map(x -> x * 2).forEach(ip2 -> {
            final Instruction instruction = Instruction.fromOpcode(computer.program().get(ip2));
            final int originalOperand = computer.program().get(ip2 + 1);
            final String operand = comboDump(originalOperand);
            computer.debug(instruction.name() +(Instruction.BXC.equals(instruction)?"":( " " + operand + (instruction.isCombo() ? (" (" + originalOperand + ")") : ""))));
        });
        System.err.println();
        System.err.println("--- END ---");
        System.err.println();
    }
}
