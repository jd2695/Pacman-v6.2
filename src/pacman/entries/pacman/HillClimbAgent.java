/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman;

import java.util.ArrayList;
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
public class HillClimbAgent extends Controller<MOVE>{
    private static final int MIN_DISTANCE=20;	//if a ghost is this close, run away
    private static final int geneDepth = 6;
    boolean timedgame;
        public HillClimbAgent(boolean timed){
            super();
            timedgame = timed;
        }
	public MOVE getMove(Game game,long timeDue){
            Chromosome candidate = new Chromosome(game, 1);
            candidate.generatePath();
            if(game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade()).length > 1){
                int numgen = 0;
                long startTime = System.currentTimeMillis();
                while((System.currentTimeMillis() < timeDue - 3 && timedgame) || (!timedgame && System.currentTimeMillis() < startTime + 4000)){
                    numgen++;
                    Chromosome neighbor = candidate.mutateOffspring();
                    neighbor.enactGivenPath();
                    if(neighbor.evaluate() > candidate.evaluate()){
                        candidate = neighbor;
                        System.out.println("Candidate quality: " + candidate.evaluate());
                    }
                }
                System.out.println("Number of generations: " + numgen + " (" + candidate.evaluate() + ")");
                return candidate.baseMove();
            }
            else{
                return game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade())[0];
            }
	}

    private static class Chromosome {
        MOVE mset[] = {MOVE.LEFT, MOVE.RIGHT, MOVE.UP, MOVE.DOWN};
        Game state;
        Game oldstate;
        int layer;
        boolean dead;
        boolean victorious;
        ArrayList<MOVE> genes;
        
        public Chromosome(Game g, int i) {
            state = g.copy();
            layer = i;
            dead = false;
            victorious = false;
            oldstate = g.copy();
            genes = new ArrayList<MOVE>();
        }
        
        public Chromosome(Game g, int i, ArrayList<MOVE> DNA){
            oldstate = g.copy();
            genes = (ArrayList<MOVE>) DNA.clone();
            state = g.copy();
            layer = i;
            dead = false;
            victorious = false;
        }
        
        //This is different from the genetic and evolutionary path generators by design.
        //Evo and Gen don't have enough generations to make viable paths by themselves, but hill climbing does.
        public void generatePath(){
            genes.clear();
            for(int i = 0; i < geneDepth; i++){
                MOVE[] marray = state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade());
                MOVE m = mset[(int) (Math.random() * mset.length)];
                proceed(m);
                genes.add(m);
            }
        }
        
        public void enactGivenPath(){
            for(int i = 0; i < genes.size(); i++){
                proceed(genes.get(i));
            }
        }
        
        public MOVE baseMove(){
            return genes.get(0);
        }
        
        public Chromosome mutateOffspring(){
            ArrayList<MOVE> offGene = new ArrayList<MOVE>();
        	for(int i = 0; i < genes.size(); i++){
        		offGene.add(genes.get(i));
        	}
        	int targetGene = (int)(Math.random() * offGene.size());
            offGene.set(targetGene, mset[(int)(Math.random() * 4)]);
            Chromosome c = new Chromosome(oldstate, layer + 1, offGene);
            return c;
        }
        
        public int evaluate(){
        	if(victorious){
                return (int) Math.pow(oldstate.getNumberOfPills() + oldstate.getNumberOfPills(), 2);
            }
            else if(dead){
                return (int) Math.pow(-state.getNumberOfActivePills()+oldstate.getNumberOfPills(), 2);
            }
            else{
                return (int) Math.pow(oldstate.getNumberOfActivePills() - state.getNumberOfActivePills() + oldstate.getNumberOfPills(), 2);
            }
        }
        
        private void proceed(MOVE m){
            while(state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade()).length > 1 && !dead){
                        state.advanceGame(m, new StarterGhosts().getMove(state.copy(), System.currentTimeMillis()+DELAY));
                        if(state.wasPacManEaten()){
                            dead = true;
                }
            }
            while(state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade()).length == 1 && !dead){
                state.advanceGame(state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade())[0], new StarterGhosts().getMove(state.copy(), System.currentTimeMillis()+DELAY));
                if(state.wasPacManEaten()){
                    dead = true;
                }
                if(!dead && (oldstate.getCurrentLevel() < state.getCurrentLevel() || state.getNumberOfActivePills() == 1  || (oldstate.getCurrentLevelTime() > LEVEL_LIMIT-500 && state.getCurrentLevelTime() == LEVEL_LIMIT -1))){
                    System.out.println("I forsee victory!");
                    victorious = true;
                }
            }
        }
    }
    
    private class DarwinianComparator<T> implements Comparator<T>{

        @Override
        public int compare(T t, T t1) {
            Chromosome a = (Chromosome) t;
            Chromosome b = (Chromosome) t1;
            //System.out.println("Stuff!");
            if(a.evaluate() > b.evaluate()){
                return -1;
            }
            else{
                return 1;
            }
        }

    }
}
