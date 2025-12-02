package com.w_wins.advent.twentytwentyfour.mains;

import com.w_wins.advent.twentytwentyfour.daysixteen.DijkstraNode;
import com.w_wins.advent.twentytwentyfour.daysixteen.DijkstraPath;
import com.w_wins.advent.twentytwentyfour.daysixteen.DijkstraResult;
import com.w_wins.advent.twentytwentyfour.daysixteen.DijkstraScore;
import com.w_wins.advent.twentytwentyfour.daysixteen.HorizontalVerticalPathElement;
import com.w_wins.advent.twentytwentyfour.daysixteen.MazeTile;
import com.w_wins.adventcommon.Coordinate;
import com.w_wins.common.CollectorUtil;
import com.w_wins.fixedwidth.GridConfig;
import com.w_wins.fixedwidth.GridParser;
import com.w_wins.fixedwidth.Grid;
import com.w_wins.fixedwidth.LabelledGrid;
import com.w_wins.iostream.Utf8ResourceLines;
import com.w_wins.pathfinding.AStar;
import com.w_wins.pathfinding.ScoredPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class DaySixteenMain {
    public static Coordinate find(final Grid<MazeTile> grid, final MazeTile type) {
        return Coordinate.coordinatesOf(grid).filter(c -> Coordinate.gridGetter(grid).apply(c).equals(type)).collect(CollectorUtil.singleton());
    }

    private static void partOne(final LabelledGrid<MazeTile> grid) {
        final AStar<Long, HorizontalVerticalPathElement> aStar = new AStar<>(Math::addExact, Long::compareTo, () -> 0L);
        final HorizontalVerticalPathElement startElement = new HorizontalVerticalPathElement(find(grid, MazeTile.START), true, true, false);
        HorizontalVerticalPathElement endElement = new HorizontalVerticalPathElement(find(grid, MazeTile.END), true, false, true);
        final Optional<ScoredPath<Long, HorizontalVerticalPathElement>> result = aStar.shortest(startElement, endElement, p -> {
            final Set<HorizontalVerticalPathElement> newElements = p.nextPathsOn(grid);
            //System.err.println("Walked from "+p+": "+newElements);
            return newElements;
        }, HorizontalVerticalPathElement::score, p -> p.heuristicOn(grid));
        if (result.isEmpty()) {
            System.err.println("EMPTY");
            System.err.println("End is " + endElement);
            System.exit(-1);
        } else {
            System.out.println(result.orElseThrow().cost());
            result.orElseThrow().path().stream().map(HorizontalVerticalPathElement::where).map(c -> c.x() + "," + c.y() + " ").forEach(System.err::print);
            System.err.println();
        }
    }

    void main() {
        partTwo(new Utf8ResourceLines(MazeTile.class, "/inte.txt").evaluate(new GridParser<>(GridConfig.CHAR_GRID, s -> MazeTile.getTile(s.charAt(0)), null)));
    }

    private void partTwo(final Grid<MazeTile> grid) {
        final Map<DijkstraNode, Long> distances = new HashMap<>();
        final Map<DijkstraNode, Set<DijkstraPath>> paths = new HashMap<>();
        final Set<DijkstraNode> done = new HashSet<>();
        final PriorityQueue<DijkstraScore> queue = new PriorityQueue<>(Comparator.comparing(DijkstraScore::score));
        final Coordinate start = find(grid, MazeTile.START);
        final Coordinate end = find(grid, MazeTile.END);
        distances.put(new DijkstraNode(start, true), -1000L);
        distances.put(new DijkstraNode(start, false), 0L);
        distances.keySet().forEach(node -> paths.put(node, new HashSet<>(Set.of(new DijkstraPath(node, Optional.empty(), 1)))));
        distances.forEach((node, score) -> queue.add(new DijkstraScore(node, score)));
        final Coordinate right = new Coordinate(1, 0);
        final Coordinate down = new Coordinate(0, 1);
        while (!queue.isEmpty()) {
            //System.err.println(queue.size()+" has "+queue);
            final DijkstraScore current = queue.poll();
            if (!done.contains(current.node())) {
                if(current.node().where().y()==1) {
                    //System.err.println("Updating " + current);
                }
                final Coordinate positiveDirection = current.node().horizontalNext() ? right : down;
                Stream.of(positiveDirection, Coordinate.ORIGO.minus(positiveDirection)).forEach(direction -> {
                    Coordinate currentNeighbour = current.node().where().plus(direction);
                    long score = current.score() + 1001L;
                    while (Coordinate.gridGetter(grid).apply(currentNeighbour).walkable()) {
                        if (currentNeighbour.y() == 1) {
                            //System.err.println("Walked to " + currentNeighbour + " from " + current.node().where() + " Score is now " + score);
                        }
                        final DijkstraScore candidate = new DijkstraScore(new DijkstraNode(currentNeighbour, !current.node().horizontalNext()), score);
                        if(done.contains(candidate.node())) {
                            //System.err.println("Ignoring "+candidate+" it is already done from lower paths");
                            if(current.node().where().x()==13) {
                                //System.err.println("Got to neighbour"+currentNeighbour+" from "+current);
                                if(currentNeighbour.y()==1) {
                                    //throw new IllegalStateException("GOT HERE, new score is "+score+" old distance "+distances.get(candidate.node()));
                                }
                            }
                        } else {
                            final Set<DijkstraPath> currentPaths = paths.get(current.node());
                            final Set<DijkstraPath> newPaths = currentPaths.stream().map(path -> path.append(candidate)).collect(Collectors.toSet());
                            if(current.node().where().x()==13 && currentNeighbour.y()==1) {
                                System.err.println("BOGUS 2: current:"+current+" 13,1 score is "+distances.get(new DijkstraNode(new Coordinate(13,1),true))+" 13,13 score is "+distances.get(new DijkstraNode(new Coordinate(13,13),false)));
                            }
                            if (candidate.score() < distances.getOrDefault(candidate.node(), Long.MAX_VALUE)) {
                                if(candidate.node().where().x()==13 && candidate.node().where().y()==1) {
                                    System.err.println("Updating from " + distances.getOrDefault(candidate.node(), Long.MAX_VALUE) + " to " + candidate);
                                }
                                distances.put(candidate.node(), candidate.score());
                                if (candidate.node().where().x()==13 && candidate.node().where().y()==1) {
                                    System.err.println("New set at " + candidate + " : " + newPaths + " old was " + paths.get(candidate.node()));
                                }
                                paths.put(candidate.node(), newPaths);
                                queue.add(candidate);
                                if(current.node().where().x()==13) {
                                    //System.err.println("Got to neighbour"+currentNeighbour+" from "+current);
                                    if(currentNeighbour.y()==1 && score<7036) {
                                        //throw new IllegalStateException("GOT HERE, new score is "+score+" old distance "+distances.get(candidate.node()));
                                    }
                                }
                            } else if (candidate.score().equals(distances.get(candidate.node()))) {
                                final Set<DijkstraPath> set = new HashSet<>(paths.getOrDefault(candidate.node(), new HashSet<>()));
                                if (candidate.node().where().x()==13 && candidate.node().where().y()==13) {
                                    System.err.println("Reused set at " + candidate + " : " + set+" adding in "+newPaths);
                                }
                                set.addAll(newPaths);
                                paths.put(candidate.node(), set);
                                if(current.node().where().x()==13) {
                                    //System.err.println("Got to neighbour"+currentNeighbour+" from "+current);
                                    if(currentNeighbour.y()==1) {
                                        throw new IllegalStateException("GOT HERE, new score is "+score+" old distance "+distances.get(candidate.node()));
                                    }
                                }
                            } else {
                                if(candidate.node().where().x()==13 && candidate.node().where().y()==1) {
                                    System.err.println("Got to neighbour"+currentNeighbour+" from "+current);
                                    if(currentNeighbour.y()==1) {
                                        throw new IllegalStateException("GOT HERE, new score is "+score+" old distance "+distances.get(candidate.node()));
                                    }
                                }
                            }
                            if (candidate.node().where().x()==13 && candidate.node().where().y()==1) {
                                System.err.println(" Updated neigbour " + candidate + " its paths is now " + paths.get(candidate.node()));
                            }
                        }
                        ++score;
                        currentNeighbour = currentNeighbour.plus(direction);
                    }
                });
                done.add(current.node());
            }
        }
        final DijkstraResult result = leastScore(distances, paths, end);
        System.err.println(paths.get(new DijkstraNode(end, true)));
        System.err.println(paths.get(new DijkstraNode(end, false)));
        final Set<Set<DijkstraNode>> pathComponents = new HashSet<>();
        final Optional<Set<DijkstraPath>> a = Optional.ofNullable(paths.get(new DijkstraNode(end, true)));
        final Optional<Set<DijkstraPath>> b = Optional.ofNullable(paths.get(new DijkstraNode(end, false)));
        final Set<Coordinate> spectators = new HashSet<>(findSpectator(StreamSupport.stream(((Collection<DijkstraPath>) result.path()).spliterator(), false).map(DijkstraPath::asList).collect(Collectors.toSet())));
        spectators.add(start);
        System.out.println("spectators " + spectators.size());


        /*
        while(!toVisit.isEmpty()) {
            final Set<HorizontalVerticalPathElement> nextToVisit = toVisit.stream().flatMap(current -> {
                final Set<HorizontalVerticalPathElement> next = current.nextPathsOn(grid);
                next.removeAll(visited);
                return next.stream();
            }).collect(Collectors.toSet());
            toVisit.clear();
            toVisit.addAll(nextToVisit);
        }*/
    }

    private DijkstraResult leastScore(final Map<DijkstraNode, Long> distances, final Map<DijkstraNode, Set<DijkstraPath>> paths, final Coordinate end) {
        final DijkstraResult a = new DijkstraResult(distances.getOrDefault(new DijkstraNode(end, false),Long.MAX_VALUE), paths.get(new DijkstraNode(end, false)));
        final DijkstraResult b = new DijkstraResult(distances.getOrDefault(new DijkstraNode(end, true),Long.MAX_VALUE), paths.get(new DijkstraNode(end, true)));
        System.err.println("Comparing "+a+" and \n         "+b);
        return a.score() < b.score() ? a : b;
    }

    private Set<Coordinate> findSpectator(final Set<List<Coordinate>> paths) {
        System.err.println("All paths: "+paths);
        final Set<Coordinate> all = StreamSupport.stream(paths.spliterator(), false).flatMap(coordinates -> StreamSupport.stream(coordinates.spliterator(), false)).collect(Collectors.toSet());
        //all.removeIf(node -> !paths.stream().allMatch(s -> s.contains(node)));
        //final List<Coordinate> path = paths.stream().findAny().orElseThrow();
        return all;
    }

}
