/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import static pacman.game.Constants.DELAY;
import static pacman.game.Constants.LEVEL_LIMIT;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;
import java.util.Random;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.game.Constants.GHOST;

/**
 *
 * @author Admin
 */
public class AlphaBetaAgent extends Controller<MOVE> {

    private static final int MIN_DISTANCE = 20;	//if a ghost is this close, run away
    //private final static float CONSISTENCY=0.1f;	//carry out intended move with this probability
    private Random rnd = new Random();
    //private MOVE[] moves=MOVE.values();

    public MOVE getMove(Game game, long timeDue) {

        //MOVE[] OO = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
        //if (OO.length == 1) {
        //    return OO[0];
        //}
        //System.out.println("ask");
        //for (MOVE ee : OO) {
        //    System.out.println(ee);
        //}
        MMNode nodeee = AlphaBetascore(new MMNode(MOVE.NEUTRAL, game.copy(), game.copy(), 0, 0), 5, timeDue);

        System.out.println("return");
        System.out.println(nodeee.move);

        return nodeee.move;

    }

    private void proceed(Game state, MOVE m, boolean dead, boolean victorious) {
        Game oldstate = state.copy();
        while (state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade()).length > 1 && !dead) {
            state.advanceGame(m, new StarterGhosts().getMove(state.copy(), System.currentTimeMillis() + DELAY));
            if (state.wasPacManEaten()) {
                dead = true;
            }
        }
        while (state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade()).length == 1 && !dead) {
            state.advanceGame(state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade())[0], new StarterGhosts().getMove(state.copy(), System.currentTimeMillis() + DELAY));
            if (state.wasPacManEaten()) {
                dead = true;
            }
            if (!dead && (oldstate.getCurrentLevel() < state.getCurrentLevel() || state.getNumberOfActivePills() == 1 || (oldstate.getCurrentLevelTime() > LEVEL_LIMIT - 500 && state.getCurrentLevelTime() == LEVEL_LIMIT - 1))) {
                System.out.println("I forsee victory!");
                victorious = true;
            }
        }
    }

    private double getGoodness(Game state) {
        double aa = -10000;
        if (state.wasPacManEaten()) {
            //System.out.println("Pacman eaten");
            return aa;

        } else {
            return (state.getNumberOfPills() - state.getNumberOfActivePills());
        }
    }

    private MMNode AlphaBetascore(MMNode node, int depthh, long time) {

        if (node.layer >= depthh || System.currentTimeMillis() >= time - 5) {
            MMNode mmn = new MMNode(node.move, node.state, node.oldstate, node.layer, getGoodness(node.state));
            return mmn;
        }

        if (node.state.wasPacManEaten()) {
            MMNode mmn = new MMNode(node.move, node.state, node.oldstate, node.layer, -10000);
            return mmn;
        }

        if (node.state.getCurrentLevel() > node.oldstate.getCurrentLevel()) {
            MMNode mmn = new MMNode(node.move, node.state, node.oldstate, node.layer, 10000);
            return mmn;
        }

        MOVE[] Options = node.state.getPossibleMoves(node.state.getPacmanCurrentNodeIndex(), node.state.getPacmanLastMoveMade());
        //MOVE[] Options = node.state.getPossibleMoves(node.state.getPacmanCurrentNodeIndex());
        EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
        ArrayList<MMNode> maxlist = new ArrayList<MMNode>();
        MMNode maxNode = new MMNode(node.move, node.state, node.oldstate, node.layer, Double.NEGATIVE_INFINITY);
        ArrayList<MMNode> minlist = new ArrayList<MMNode>();
        MMNode minNode = new MMNode(node.move, node.state, node.oldstate, node.layer, Double.POSITIVE_INFINITY);
        maxlist.clear();
        int strt2 = 0;

        for (MOVE m : Options) {
            MOVE[] mg1 = {MOVE.NEUTRAL};
            MOVE[] mg2 = {MOVE.NEUTRAL};
            MOVE[] mg3 = {MOVE.NEUTRAL};
            MOVE[] mg4 = {MOVE.NEUTRAL};
            minlist.clear();

            if (node.state.doesGhostRequireAction(GHOST.BLINKY)) //if it requires an action
            {
                mg1 = node.state.getPossibleMoves(node.state.getGhostCurrentNodeIndex(GHOST.BLINKY));

            }

            if (node.state.doesGhostRequireAction(GHOST.INKY)) //if it requires an action
            {
                mg2 = node.state.getPossibleMoves(node.state.getGhostCurrentNodeIndex(GHOST.INKY));

            }

            if (node.state.doesGhostRequireAction(GHOST.PINKY)) //if it requires an action
            {
                mg3 = node.state.getPossibleMoves(node.state.getGhostCurrentNodeIndex(GHOST.PINKY));

            }

            if (node.state.doesGhostRequireAction(GHOST.SUE)) //if it requires an action
            {
                mg4 = node.state.getPossibleMoves(node.state.getGhostCurrentNodeIndex(GHOST.SUE));

            }

            int strt = 0;

            outerloop:
            for (MOVE aa : mg1) {
                for (MOVE bb : mg2) {
                    for (MOVE cc : mg3) {
                        for (MOVE dd : mg4) {
                            myMoves.clear();
                            Game s = node.state.copy();

                            if (aa != MOVE.NEUTRAL) {
                                myMoves.put(GHOST.BLINKY, aa);
                            }
                            if (bb != MOVE.NEUTRAL) {
                                myMoves.put(GHOST.INKY, bb);
                            }
                            if (cc != MOVE.NEUTRAL) {
                                myMoves.put(GHOST.PINKY, cc);
                            }
                            if (dd != MOVE.NEUTRAL) {
                                myMoves.put(GHOST.SUE, dd);
                            }

                            s.advanceGame(m, myMoves);

                            while (!s.isJunction(s.getPacmanCurrentNodeIndex()) && !s.wasPacManEaten()) {
                                s.advanceGame(m, new AggressiveGhosts().getMove(s.copy(), System.currentTimeMillis() + DELAY));
                            }

                            MMNode scoree = AlphaBetascore(new MMNode(m, s.copy(), node.state.copy(), node.layer + 1, node.goodness), depthh, time);
                            //minlist.add(new MMNode(m, s.copy(), node.state.copy(), node.layer, scoree.goodness));

                            if (strt == 0) {
                                minNode = new MMNode(m, s.copy(), node.state.copy(), node.layer, scoree.goodness);
                                strt = strt + 1;
                            } else if (scoree.goodness < minNode.goodness) {
                                minNode = new MMNode(m, s.copy(), node.state.copy(), node.layer, scoree.goodness);

                            } //System.out.println(node.layer);

                            if (scoree.goodness < maxNode.goodness) {
                                break outerloop;
                            }
                        }
                    }
                }

            }

            //maxlist.add(minNode);

            if (strt2 == 0) {
                maxNode = minNode;
                strt2 = strt2 + 1;
            } else if (minNode.goodness > maxNode.goodness) {
                maxNode = minNode;
            }
        }

        //int strt2 = 0;
       

        return maxNode;
    }

    private static class MMNode {

        MOVE move;
        Game state;
        Game oldstate;
        int layer;
        double goodness;
//int donechild;

        public MMNode(MOVE m, Game g, Game os, int i, double good) {
            state = g;
            layer = i;
            goodness = good;
            move = m;
            oldstate = os;
            //donechild=done;
        }

    }

}
