/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman;

import java.util.ArrayList;
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
public class GeneticAgent extends Controller<MOVE>{
    private static final int MIN_DISTANCE=20;	//if a ghost is this close, run away
    private static final int geneDepth = 6;
    private static final int popsize = 10;
        boolean timedgame;
        public GeneticAgent(boolean timed){
            super();
            timedgame = timed;
        }
	public MOVE getMove(Game game,long timeDue){
            Chromosome bestchrom = null;
            int besteval = 0;
            if(game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade()).length > 1){
                int numgen = 0;
                ArrayList<Chromosome> genePool = new ArrayList<Chromosome>();
                for(int i = 0; i < popsize; i++){
                    genePool.add(new Chromosome(game, 1));
                    genePool.get(i).generatePath();
                    //System.out.println("Chromosome " + (i+1) + ": " + genePool.get(i).evaluate());
                    if(genePool.get(i).evaluate() > besteval){
                        bestchrom = genePool.get(i);
                        besteval = bestchrom.evaluate();
                    }
                }
                long startTime = System.currentTimeMillis();
                while((System.currentTimeMillis() < timeDue - 3 && timedgame) || (!timedgame && System.currentTimeMillis() < startTime + 4000)){
                    numgen++;
                    ArrayList<Chromosome> nGen = new ArrayList<Chromosome>();
                    int poolquality = 0;
                    ArrayList<Double> poolprobs = new ArrayList<Double>();
                    for(Chromosome c:genePool){
                        //System.out.println("Chromosome quality: " + c.evaluate());
                        poolquality += c.evaluate();
                    }
                    //System.out.println("Generation " + numgen + " quality : " + poolquality);
                    poolprobs.add((double) genePool.get(0).evaluate() / poolquality);
                    for(int i = 1; i < genePool.size(); i++){
                        poolprobs.add( poolprobs.get(i-1) + (double) genePool.get(i).evaluate() / poolquality);
                    }
                    for(int i = 0; i < genePool.size() / 2; i++){
                        int achrom = poolprobs.size();
                        double acram = Math.random();
                        for(int j = 0; j < poolprobs.size(); j++){
                            if(acram < poolprobs.get(j)){
                                achrom = j;
                                break;
                            }
                        }
                        int bchrom = poolprobs.size();
                        double bcram = Math.random();
                        for(int j = 0; j < poolprobs.size(); j++){
                            if(bcram < poolprobs.get(j)){
                                bchrom = j;
                                break;
                            }
                        }
                        ArrayList<Chromosome> babies = genePool.get(achrom).makeBabies(game, genePool.get(bchrom));
                        babies.get(0).enactGivenPath();
                        babies.get(1).enactGivenPath();
                        nGen.addAll(babies);
                        
                    }
                    genePool.clear();
                    
                    for(Chromosome c: nGen){
                        genePool.add(c);
                        if(c.evaluate() > bestchrom.evaluate()){
                            bestchrom = c;
                            besteval = bestchrom.evaluate();
                        }
                    }
                }
                System.out.println("Number of generations: " + numgen);
                return bestchrom.baseMove();
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
        
        public void generatePath(){
            genes.clear();
            for(int i = 0; i < geneDepth; i++){
                MOVE[] marray = state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade());
                MOVE m = marray[(int) (Math.random() * marray.length)];
                //MOVE m = mset[(int) (Math.random() * mset.length)];
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
        
        public ArrayList<Chromosome> makeBabies(Game state, Chromosome other){
            int crosspoint = (int) (Math.random() * geneDepth);
            ArrayList<MOVE> genea = new ArrayList<MOVE>();
            ArrayList<MOVE> geneb = new ArrayList<MOVE>();
            for(int i = 0; i < crosspoint; i++){
                genea.add(genes.get(i));
                geneb.add(other.genes.get(i));
            }
            for(int i = crosspoint; i < geneDepth; i++){
                genea.add(other.genes.get(i));
                geneb.add(genes.get(i));
            }
            ArrayList<Chromosome> ret = new ArrayList<Chromosome>();
            double mutationChance = 0.1;
            if(Math.random() < mutationChance){
                int targetGene = (int)(Math.random() * genea.size());
                genea.set(targetGene, mset[(int)(Math.random() * 4)]);
            }
            if(Math.random() < mutationChance){
                int targetGene = (int)(Math.random() * geneb.size());
                geneb.set(targetGene, mset[(int)(Math.random() * 4)]);
            }
            ret.add(new Chromosome(state, layer + 1, genea));
            ret.add(new Chromosome(state, layer + 1, geneb));
            return ret;
        }
        
        public int evaluate(){
            if(victorious){
                return oldstate.getNumberOfPills() + oldstate.getNumberOfPills();
            }
            else if(dead){
                return -state.getNumberOfActivePills()+oldstate.getNumberOfPills();
            }
            else{
                return oldstate.getNumberOfActivePills() - state.getNumberOfActivePills() + oldstate.getNumberOfPills();
            }
        }
        
        private void proceed(MOVE m){
            Game ostate = state.copy();
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
                    //System.out.println("I forsee victory!");
                    victorious = true;
                }
            }
        }
    }
}
