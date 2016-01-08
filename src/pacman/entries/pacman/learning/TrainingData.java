/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman.learning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import static pacman.game.Constants.DELAY;
import static pacman.game.Constants.LEVEL_LIMIT;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 *
 * @author Admin
 */

public class TrainingData implements Serializable {
    final int DISTMAX = 1000;
    final int PTMAX = 20; //This one I just kind of handwave guesstimate
    final int EMAX = 3;
    public MOVE decision;
    int nearestGhostDist;
    HashMap<MOVE, JuncData> jdats;
    public TrainingData(Game game, MOVE moveMade) {
        nearestGhostDist = DISTMAX;
        jdats = new HashMap<MOVE, JuncData>();
        decision = moveMade;
        MOVE mset[] = {MOVE.LEFT, MOVE.RIGHT, MOVE.UP, MOVE.DOWN};
        for(Constants.GHOST ghost : Constants.GHOST.values())
            if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
		if(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost))<nearestGhostDist)
                    nearestGhostDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost));
        for(MOVE m:mset){
            Game ostate = game.copy();
            int ending = proceed(ostate, m);
            
            int pval = game.getNumberOfActivePills() - ostate.getNumberOfActivePills();
            //System.out.println(pval + " = " + game.getNumberOfActivePills() + " - " + ostate.getNumberOfActivePills() + ", " + (game.getCurrentLevelTime() - ostate.getCurrentLevelTime()));
            int ngd = DISTMAX;
            for(Constants.GHOST ghost : Constants.GHOST.values())
                if(ostate.getGhostEdibleTime(ghost)==0 && ostate.getGhostLairTime(ghost)==0)
                    if(ostate.getShortestPathDistance(ostate.getPacmanCurrentNodeIndex(),ostate.getGhostCurrentNodeIndex(ghost))<ngd)
                        ngd = ostate.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), ostate.getGhostCurrentNodeIndex(ghost));
            JuncData jdump = new JuncData(pval, ngd, ending, m);
            jdats.put(m, jdump);
        }
    }
    @Override
    public String toString(){
        String s = "Current data: \n";
        s += "  Nearest Ghost: " + nearestGhostDist + "\n";
        for(Entry<MOVE, JuncData> e:jdats.entrySet()){
            s += "Junction " + e.getKey() + "\n";
            s += "  Point value: " + e.getValue().pointval + "\n";
            s += "  Nearest Ghost: " + e.getValue().nearestGhostDist + "\n";
            s += "  Ending: " + e.getValue().end + "\n";
            if(decision == e.getKey()){
                s += "  Note: this was the decision made\n";
            }
        }
        return s;
    }
    
    public int getSign(MOVE test){
        if(test.equals(decision)){
            return 1;
        }
        else{
            return -1;
        }
    }
    
    public double[] toDArray(){
        double[] arr = new double[13];
        arr[0] = (double)nearestGhostDist/DISTMAX;
        arr[1] = (double)jdats.get(MOVE.LEFT).pointval/PTMAX;
        arr[2] = (double)jdats.get(MOVE.LEFT).nearestGhostDist/DISTMAX;
        arr[3] = (double)jdats.get(MOVE.LEFT).end/EMAX;
        arr[4] = (double)jdats.get(MOVE.RIGHT).pointval/PTMAX;
        arr[5] = (double)jdats.get(MOVE.RIGHT).nearestGhostDist/DISTMAX;
        arr[6] = (double)jdats.get(MOVE.RIGHT).end/EMAX;
        arr[7] = (double)jdats.get(MOVE.UP).pointval/PTMAX;
        arr[8] = (double)jdats.get(MOVE.UP).nearestGhostDist/DISTMAX;
        arr[9] = (double)jdats.get(MOVE.UP).end/EMAX;
        arr[10] = (double)jdats.get(MOVE.DOWN).pointval/PTMAX;
        arr[11] = (double)jdats.get(MOVE.DOWN).nearestGhostDist/DISTMAX;
        arr[12] = (double)jdats.get(MOVE.DOWN).end/EMAX;
        return arr;
    }
    
    public double[] toUArray(){
        double[] arr = new double[13];
        arr[0] = (double)nearestGhostDist;
        arr[1] = (double)jdats.get(MOVE.LEFT).pointval;
        arr[2] = (double)jdats.get(MOVE.LEFT).nearestGhostDist;
        arr[3] = (double)jdats.get(MOVE.LEFT).end;
        arr[4] = (double)jdats.get(MOVE.RIGHT).pointval;
        arr[5] = (double)jdats.get(MOVE.RIGHT).nearestGhostDist;
        arr[6] = (double)jdats.get(MOVE.RIGHT).end;
        arr[7] = (double)jdats.get(MOVE.UP).pointval;
        arr[8] = (double)jdats.get(MOVE.UP).nearestGhostDist;
        arr[9] = (double)jdats.get(MOVE.UP).end;
        arr[10] = (double)jdats.get(MOVE.DOWN).pointval;
        arr[11] = (double)jdats.get(MOVE.DOWN).nearestGhostDist;
        arr[12] = (double)jdats.get(MOVE.DOWN).end;
        return arr;
    }
    
    
    public double distance(TrainingData other){
        double distance = 0;
        distance = Math.pow(other.nearestGhostDist - nearestGhostDist, 2);
        for(Entry<MOVE, JuncData> e:jdats.entrySet()){
            distance += Math.pow((other.jdats.get(e.getKey()).nearestGhostDist - jdats.get(e.getKey()).nearestGhostDist), 2);
            distance += Math.pow((other.jdats.get(e.getKey()).pointval - jdats.get(e.getKey()).pointval), 2);
            if(other.jdats.get(e.getKey()).end != other.jdats.get(e.getKey()).end){
                distance += 1000;
            }
        }
        return Math.sqrt(distance);
    }
    
    private int proceed(Game state, MOVE m){
            Game ostate = state.copy();
            //lose, neutral, feast, win
            int end = 1;
            while(state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade()).length > 1 && end != 0){
                        state.advanceGame(m, new StarterGhosts().getMove(state.copy(), System.currentTimeMillis()+DELAY));
                        if(state.wasPacManEaten()){
                            end = 0;
                }
            }
            while(state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade()).length == 1 && end != 0){
                state.advanceGame(state.getPossibleMoves(state.getPacmanCurrentNodeIndex(), state.getPacmanLastMoveMade())[0], new StarterGhosts().getMove(state.copy(), System.currentTimeMillis()+DELAY));
                if(state.wasPacManEaten()){
                    end = 0;
                }
                if(end != 0 && (ostate.getCurrentLevel() < state.getCurrentLevel() || state.getNumberOfActivePills() == 1  || (ostate.getCurrentLevelTime() > LEVEL_LIMIT-500 && state.getCurrentLevelTime() == LEVEL_LIMIT -1))){
                    //System.out.println("I forsee victory!");
                    end = 3;
                }
            }
            for(Constants.GHOST ghost : Constants.GHOST.values()){
                if(end == 0 && state.getGhostEdibleTime(ghost)!=0){
                    end = 2;
                }
            }
            return end;
        }
}
