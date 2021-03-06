/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman;

import java.util.Date;
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

/**
 *
 * @author Admin
 */
public class BFSAgent extends Controller<MOVE>{
    private static final int MIN_DISTANCE=20;	//if a ghost is this close, run away
	boolean timedgame;
        public BFSAgent(boolean timed){
            super();
            timedgame = timed;
        }
        
	public MOVE getMove(Game game,long timeDue){
            int bestdots = 500;
            MOVE bestMove = null;
            int besttime = 0;
            LinkedBlockingQueue<BFSNode> bfsQueue = new LinkedBlockingQueue<BFSNode>();
            MOVE[] MoveArray = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
            for(MOVE m:MoveArray){
                Game s = game.copy();
                s.advanceGame(m, new StarterGhosts().getMove(s.copy(), System.currentTimeMillis()+DELAY));
                bfsQueue.offer(new BFSNode(m, s, 0));
            }
            long startTime = System.currentTimeMillis();
            while(bfsQueue.size() > 1 && 
                    (
                        (System.currentTimeMillis() < timeDue - 3 && timedgame)
                        || 
                        ((System.currentTimeMillis() < startTime + 1000) && !timedgame)
                    )
                 ){
                BFSNode current = bfsQueue.poll();
                MOVE[] choices = current.state.getPossibleMoves(current.state.getPacmanCurrentNodeIndex(), current.state.getPacmanLastMoveMade());
                
                //GameView gv = new GameView(current.state).showGame();
                //gv.repaint();
                for(MOVE m:choices){
                    boolean pacmanwaseaten = false;
                    Game cstate = current.state.copy();
                    cstate.advanceGame(m, new StarterGhosts().getMove(cstate.copy(), System.currentTimeMillis()+DELAY));
                    //System.out.println(bfsQueue.size());
                    while(cstate.getPossibleMoves(cstate.getPacmanCurrentNodeIndex(), cstate.getPacmanLastMoveMade()).length > 1 && !pacmanwaseaten){
                        cstate.advanceGame(m, new StarterGhosts().getMove(cstate.copy(), System.currentTimeMillis()+DELAY));
                        if(cstate.wasPacManEaten()){
                            pacmanwaseaten = true;
                        }
                    }
                    while(cstate.getPossibleMoves(cstate.getPacmanCurrentNodeIndex(), cstate.getPacmanLastMoveMade()).length == 1 && !pacmanwaseaten){
                        cstate.advanceGame(cstate.getPossibleMoves(cstate.getPacmanCurrentNodeIndex(), cstate.getPacmanLastMoveMade())[0], new StarterGhosts().getMove(cstate.copy(), System.currentTimeMillis()+DELAY));
                        if(cstate.wasPacManEaten()){
                            pacmanwaseaten = true;
                        }
                        if(!pacmanwaseaten && (cstate.getNumberOfActivePills() == 1  || (current.state.getCurrentLevelTime() > LEVEL_LIMIT-500 && cstate.getCurrentLevelTime() == LEVEL_LIMIT -1))){
                            //System.out.println(current.layer + ", " +cstate.getNumberOfActivePills() + ", " + cstate.getPacmanCurrentNodeIndex() + ", " + cstate.getCurrentLevelTime() + ", " + cstate.getMazeIndex());
                            //System.out.println("I forsee victory!");
                              //GameView gv = new GameView(current.state).showGame();
                              //gv.repaint();
                            return current.baseMove;
                        }
                    }
                    if(!pacmanwaseaten && (cstate.getNumberOfActivePills() == 0 || cstate.getCurrentLevelTime() >= LEVEL_LIMIT) || cstate.getCurrentLevel() != game.getCurrentLevel()){
                        System.out.println(current.layer + ", " +cstate.getNumberOfActivePills() + ", " + cstate.getPacmanCurrentNodeIndex() + ", " + cstate.getCurrentLevelTime() + ", " + cstate.getMazeIndex());
                        System.out.println("I forsee victory!");
                        
                        //    GameView gv = new GameView(current.state).showGame();
                          //  gv.repaint();
                        return current.baseMove;
                    }
                    if(!pacmanwaseaten){
                        //System.out.println(current.layer + ", " +cstate.getNumberOfActivePills() + ", " + cstate.getPacmanCurrentNodeIndex() + ", " + cstate.getCurrentLevelTime() + ", " + cstate.getMazeIndex() + ", " + cstate.getCurrentLevel());
                        /*
                        
                        if(current.layer == 40 || cstate.getCurrentLevel() == 2){
                            GameView gv = new GameView(current.state).showGame();
                            gv.repaint();
                        }*/
                        if(cstate.getNumberOfActivePills()<bestdots){
                            bestdots = cstate.getNumberOfActivePills();
                            bestMove = current.baseMove;
                        }
                        //System.out.println(current.state.getPacmanLastMoveMade() + " / " + m);
                        bfsQueue.add(new BFSNode(current.baseMove, cstate, current.layer + 1));
                    }
                }
            }
            //System.out.println("Defaulting to : " + bfsQueue.peek().baseMove);
            if(bestMove == null){
                return bfsQueue.poll().baseMove;
            }
            else return bestMove;
            
	}

    private static class BFSNode {
        MOVE baseMove;
        Game state;
        int layer;
        public BFSNode(MOVE m, Game g, int i) {
            baseMove = m;
            state = g;
            layer = i;
        }
    }
}
