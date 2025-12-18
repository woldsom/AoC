package com.w_wins.advent.twentytwentyfive.mains;

import com.w_wins.advent.twentytwentyfive.dayten.MachineArguments;
import com.w_wins.advent.twentytwentyfive.dayten.MachineSpec;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.HashMap;
import java.util.List;
import java.util.OptionalInt;

public class DayTenMain {
    static void main() {
        final HashMap<MachineArguments, OptionalInt> memo = new HashMap<>();
        final List<MachineSpec> machineSpecs = new Utf8ResourceLines(MachineSpec.class, "/input.txt").evaluate(lines -> lines.map(line -> MachineSpec.parse(line, memo)).toList());
        IO.println(machineSpecs.stream().mapToInt(MachineSpec::pressesToLight).sum());
        IO.println(machineSpecs.stream().mapToInt(MachineSpec::pressesToJolt).sum());
    }
}

