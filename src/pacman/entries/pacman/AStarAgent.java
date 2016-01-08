/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman;

import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;
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
public class AStarAgent extends Controller<MOVE>{
    private static final int MIN_DISTANCE=20;	//if a ghost is this close, run away
	boolean timedgame;
        public AStarAgent(boolean timed){
            super();
            timedgame = timed;
        }
        
	public MOVE getMove(Game game,long timeDue){
            //System.out.println("Decision making!");
            PriorityQueue<BFSNode> bfsQueue = new PriorityQueue<>(new HeuristicComparator());
            MOVE[] MoveArray = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
            MOVE bestMove = MoveArray[0];
            for(MOVE m:MoveArray){
                Game s = game.copy();
                s.advanceGame(m, new StarterGhosts().getMove(s.copy(), System.currentTimeMillis()+DELAY));
                bfsQueue.offer(new BFSNode(m, s, 1, 1, s.getNumberOfActivePills()));
            }
            long startTime = System.currentTimeMillis();
            while(bfsQueue.size() > 1 
                    && ((System.currentTimeMillis() < timeDue - 3 && timedgame)
                    || (System.currentTimeMillis() < startTime + 40000 && !timedgame))
                ){
                BFSNode current = bfsQueue.poll();
                
                //System.out.println("Layer: " + current.oldamt + ", " + current.state.getNumberOfActivePills());
                //System.out.println("Current choice goodness: " +  current.getGoodness());
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
                    }
                    if(!pacmanwaseaten && (cstate.getNumberOfActivePills() == 0 || cstate.getCurrentLevelTime() >= LEVEL_LIMIT) || cstate.getCurrentLevel() != game.getCurrentLevel()){
                        //System.out.println(current.layer + ", " +cstate.getNumberOfActivePills() + ", " + cstate.getPacmanCurrentNodeIndex() + ", " + cstate.getCurrentLevelTime() + ", " + cstate.getMazeIndex());
                        //System.out.println("I forsee victory!");
                        
                        //    GameView gv = new GameView(current.state).showGame();
                          //  gv.repaint();
                        return current.baseMove;
                    }
                    else if(cstate.getCurrentLevel() == game.getCurrentLevel() && !pacmanwaseaten){
                        BFSNode newadd = new BFSNode(current.baseMove, cstate, current.layer + 1, current.getGoodness(), current.state.getNumberOfActivePills());
                        bfsQueue.add(newadd);
                    }
                }
            }
            //System.out.println("Defaulting to : " + bfsQueue.peek().baseMove);
            return bfsQueue.poll().baseMove;
            
	}

    private static class BFSNode {
        MOVE baseMove;
        Game state;
        int layer;
        double oldgoodness;
        int oldamt;
        public BFSNode(MOVE m, Game g, int i, double good, int old) {
            baseMove = m;
            state = g;
            layer = i;
            oldgoodness = good;
            oldamt = old;
        }
        public double getGoodness(){
           /// System.out.println(oldamt);
           // System.out.println(state.getNumberOfActivePills());
            
            return (state.getNumberOfPills() - state.getNumberOfActivePills());
        }
    }
    
    private class HeuristicComparator<T> implements Comparator<T>{

        @Override
        public int compare(T t, T t1) {
            BFSNode a = (BFSNode) t;
            BFSNode b = (BFSNode) t1;
            //System.out.println("Stuff!");
            if(a.state.getCurrentLevel() > b.state.getCurrentLevel()){
                return -1;
            }
            else if(a.state.getCurrentLevel() < b.state.getCurrentLevel()){
                return 1;
            }
            if(a.getGoodness() > b.getGoodness()){
                return -1;
            }
            else{
                return 1;
            }
        }

    }
}
