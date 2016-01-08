/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman.learning;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import pacman.game.Constants.MOVE;

/**
 *
 * @author Admin
 */
public class Perceptron implements Serializable {
    //0 pointval, 1 ghostdist, 2 end
    //neutral, left, right, up, down in that order
    double[] weights = new double[13];
    public MOVE output;
    public Perceptron(MOVE m){
        for(int i = 0; i < 13; i++){
            weights[i] = (Math.random() * 2 - 1);
        }
        output = m;
    }
    
    boolean train(double learningFactor, TrainingData input){
        double signal = getSignal(input);
        double multiplier = learningFactor * input.getSign(output);
        double[] tDats = input.toDArray();
        //System.out.println(signal / Math.abs(signal));
        if(Math.signum(signal) != Math.signum(input.getSign(output))){
            for(int i = 0; i < weights.length; i++){
                weights[i] += multiplier * tDats[i];
            }
            return false;
        }        
        
        return true;
    }
    
    public double getSignal(TrainingData input){
        //System.out.println("Weight Trace");
        double[] tdarr = input.toDArray();
        double weightedVal = 0;
        for(int i = 0; i < tdarr.length;i++){
            weightedVal += tdarr[i] * weights[i];
        }
        /*
            System.out.println("Anomaly!");
            for(int i = 0; i < weights.length; i++){
                System.out.print(tdarr[i] + ", ");
            }
            
            System.out.println();
            for(int i = 0; i < weights.length; i++){
                System.out.print(weights[i] + ", ");
            }
            
            System.out.println();
            for(int i = 0; i < weights.length; i++){
                System.out.print(weights[i] * tdarr[i] + ", ");
            }
            System.out.println();
                */
        //System.out.println(weightedVal);
        return weightedVal;
    }
    
}
