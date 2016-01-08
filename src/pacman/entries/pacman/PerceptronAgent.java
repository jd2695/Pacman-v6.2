/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.entries.pacman.learning.Perceptron;
import pacman.entries.pacman.learning.TrainingData;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 *
 * @author Admin
 */
public class PerceptronAgent extends Controller<MOVE> {
    ArrayList<Perceptron> daemons;
    public PerceptronAgent(String filename){
        try{
            FileInputStream fin = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fin);
            daemons = (ArrayList<Perceptron>) ois.readObject();
        }
        catch(Exception e){
           daemons = new ArrayList<Perceptron>(); 
        }
    }
    
    @Override
    public MOVE getMove(Game game, long timeDue) {
        if(game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade()).length > 1){
            TrainingData currentPoint = new TrainingData(game, MOVE.NEUTRAL);
            double maxSig = Double.MIN_VALUE;
            MOVE bestM = MOVE.NEUTRAL;
            for(Perceptron p:daemons){
                if(p.getSignal(currentPoint) > maxSig){
                    maxSig = p.getSignal(currentPoint);
                    bestM = p.output;
                }
            }
            return bestM;
        }
        else{
            return game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade())[0];
        }
    }
    
}
