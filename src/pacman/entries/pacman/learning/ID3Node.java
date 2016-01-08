/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman.learning;

import java.util.ArrayList;
import java.util.Set;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;

/**
 *
 * @author Admin
 */
public class ID3Node {
    private double initcriteria;
    private int attIndex;
    Set<Integer> attributes;
    ArrayList<TrainingData> dataset;
    ID3Node low;
    ID3Node mid;
    ID3Node high;
    boolean bottom = true;
    Constants.MOVE mset[] = {Constants.MOVE.LEFT, Constants.MOVE.RIGHT, Constants.MOVE.UP, Constants.MOVE.DOWN};
    
    public ID3Node(double crit, int index, Set<Integer> datset, ArrayList<TrainingData> tset){
        initcriteria = crit;
        attIndex = index;
        attributes = datset;
        attributes.remove(index);
        dataset = tset;
    }
    
    public double getCriteria(){
        return initcriteria;
    }
    

    
    public void partition(){
        if(dataset.isEmpty() || attributes.isEmpty() || uniform(dataset)){
            bottom = true;
        }
        else{
            double maxinfo = 0;
            int bestatt = 0;
            for(Integer i:attributes){
                if(theoInfoGain(i) > maxinfo){
                    bestatt = i;
                    maxinfo = theoInfoGain(i);
                }
            }
            double maxcrit = getMaxofAtt(bestatt);
            double mincrit = maxcrit/3;
            double midcrit = mincrit * 2;
        }
    }
    
    private double getMaxofAtt(int i){
        double max = 0;
        for(TrainingData t: dataset){
            if(max < t.toUArray()[i]){
                max = t.toUArray()[i];
            }
        }
        return max;
    }
    
    private boolean uniform(ArrayList<TrainingData> input){
        MOVE init = input.get(0).decision;
        for(TrainingData t:input){
            if(t.decision.equals(init) == false){
                return false;
            }
        }
        return true;
    }
    
    public double theoInfoGain(int att){
        ArrayList<TrainingData> lowset = new ArrayList<>();
        ArrayList<TrainingData> midset = new ArrayList<>();
        ArrayList<TrainingData> hiset = new ArrayList<>();
        double maxval = 0;
        
        for(TrainingData t:dataset){
            if(maxval < t.toUArray()[att]){
                maxval = t.toUArray()[att];
            }
        }
        for(int i = 0; i < dataset.size(); i++){
            double value = dataset.get(i).toUArray()[attIndex];
            if(value < maxval / 3){
                lowset.add(dataset.get(i));
            }
            else if(value < maxval * 2 / 3){
                midset.add(dataset.get(i));
            }
            else{
                hiset.add(dataset.get(i));
            }
        }
        
        return subInfo(dataset) - subInfo(lowset) - subInfo(midset) - subInfo(hiset);
        
    }
    
    private double subInfo(ArrayList<TrainingData> input){
        int[] counts = {0, 0, 0, 0};
        for(int i = 0; i < input.size(); i++){
            TrainingData td = input.get(i);
            if(td.decision.equals(MOVE.LEFT)){
                counts[0]++;
            }
            else if(td.decision.equals(MOVE.RIGHT)){
                counts[1]++;
            }
            else if(td.decision.equals(MOVE.UP)){
                counts[2]++;
            }
            else if(td.decision.equals(MOVE.DOWN)){
                counts[3]++;
            }
        }
        double totalinfo = 0.0;
        for(int i = 0; i < counts.length; i++){
            double p = (double)counts[i]/input.size();
            totalinfo += p * Math.log(p) / Math.log(2);
        }
        totalinfo *= -1;
        return totalinfo;        
    }
    
    
}
