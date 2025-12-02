package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daythirteen.Game;
import com.w_wins.common.Streams;
import com.w_wins.iostream.LineGroupEvaluator;
import com.w_wins.iostream.Utf8ResourceLines;

import java.util.Set;
import java.util.stream.Collectors;

public final class DayThirteenMain {
    void main() {
        final Set<Game> games = new Utf8ResourceLines(Game.class, "/input.txt").evaluate(new LineGroupEvaluator<>(Game::parse, Collectors.toSet()));
        System.out.println(Streams.presentLong(games.stream().map(Game::movesToWin)).sum());
        System.out.println(Streams.presentLong(games.stream().map(game->game.plus(10000000000000L)).map(Game::movesToWin)).sum());
    }
}
