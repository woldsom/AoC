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
        machineSpecs.forEach(spec->IO.println(spec.stats()+"            for "+spec));
        IO.println(machineSpecs.stream().mapToInt(MachineSpec::pressesToLight).sum());
        IO.println(machineSpecs.stream().peek(obj -> IO.println(obj.toPretty())).mapToInt(MachineSpec::pressesToJolt).peek(IO::println).sum());
    }
}
/*
[
[0,0,0,0,1,1,0,0,0,17],
[0,1,0,1,0,1,1,1,0,44],
[1,0,0,1,0,1,0,0,1,39],
[1,1,1,0,0,0,0,1,1,44],
[1,1,0,1,0,1,0,0,1,46],
[0,1,0,0,1,0,0,1,0,23],
[0,1,0,0,1,1,0,1,1,41],
[0,0,1,1,0,1,0,1,1.43],
[0,1,1,1,0,0,1,0,1,65],
[0,1,0,0,1,0,0,1,0,23]
]
 */
/*
[
[0,0,0,0,0,0,0,1,1,17],
[0,0,0,0,1,0,0,1,0,16],
[0,0,0,0,1,1,0,0,0,17],
[0,0,0,1,0,1,1,1,0,37],
[0,0,1,1,0,0,1,0,1,58],
[0,0,1,1,0,1,0,0,0.26],
[0,1,0,0,0,0,0,0,0,7],
[1,0,0,1,0,1,0,0,1,39],
[1,0,1,0,0,0,0,0,0,20],
]
 */

