package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daytwenty.RaceMap;
import com.w_wins.advent.twentytwentyfour.daytwenty.RaceTrack;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.SimpleGridParser;
import com.w_wins.iostream.Utf8ResourceLines;

public final class DayTwentyMain {
    void main() {
        /*
        final RaceTrack track = new RaceTrack(new Utf8ResourceLines(RaceTrack.class, "/input.txt").evaluate(new SimpleGridParser(GridConfig.CHAR_GRID)));
        System.out.println(track.cheats().stream().filter(c->c.savings()>=100).count()); // Part 1
        System.out.println(track.heavyCheats(100));

         */
        final RaceMap map = new Utf8ResourceLines(RaceTrack.class, "/example.txt").evaluate(RaceMap::parse);
        System.err.println(map);
    }
}
