package com.w_wins.advent.twentytwentyfour.daytwentyfour;

import com.w_wins.common.Either;

import java.util.Map;

public record SymbolicEquation(
        BooleanOperator operation,
        Either<SymbolicEquation, BitOperand> first,
        Either<SymbolicEquation, BitOperand> second) {
    private static String pretty(final Either<SymbolicEquation, BitOperand> operand) {
        return operand.map(a -> "(" + a.pretty() + ")", BitOperand::pretty);
    }

    public String pretty() {
        return pretty(first) + operation().pretty() + pretty(second);
    }

    public int lowest() {
        return Math.min(first().map(SymbolicEquation::lowest, BitOperand::bit), second().map(SymbolicEquation::lowest, BitOperand::bit));
    }

    public int highest() {
        return Math.max(first().map(SymbolicEquation::highest, BitOperand::bit), second().map(SymbolicEquation::highest, BitOperand::bit));
    }

    public SymbolicEquation byDepth() {
        if (depth(this.second()) > depth(this.first()) || (depth(this.second()) == depth(this.first())) && (first.isA() || this.second().isA() || first.b().orElseThrow().x() || !this.second().b().orElseThrow().x())) {
            return new SymbolicEquation(operation(), first().mapSame(SymbolicEquation::byDepth, x -> x), second().mapSame(SymbolicEquation::byDepth, x -> x));
        } else {
            return new SymbolicEquation(operation(), second().mapSame(SymbolicEquation::byDepth, x -> x), first().mapSame(SymbolicEquation::byDepth, x -> x));
        }
    }

    private int depth(final Either<SymbolicEquation, BitOperand> operand) {
        return operand.map(SymbolicEquation::myDepth, _ -> 1);
    }

    private int myDepth() {
        return Math.max(depth(first()), depth(second())) + 1;
    }

    public boolean evaluate(final Map<String, Boolean> inputs) {
        return operation().evaluate(evaluateOperand(first(), inputs), evaluateOperand(second(), inputs));
    }

    private boolean evaluateOperand(final Either<SymbolicEquation, BitOperand> operand, final Map<String, Boolean> inputs) {
        return operand.map(s -> s.evaluate(inputs), b -> inputs.get(b.key()));
    }
}
