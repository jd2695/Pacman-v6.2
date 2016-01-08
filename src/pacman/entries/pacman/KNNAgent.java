/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import pacman.controllers.Controller;
import pacman.entries.pacman.learning.TrainingData;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 *
 * @author Admin
 */
public class KNNAgent extends Controller<MOVE> {
    ArrayList<TrainingData> td;
    int k = 80;
    public KNNAgent(String filename){
        super();
        try{
        FileInputStream fin = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fin);
        td = (ArrayList<TrainingData>) ois.readObject();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    @Override
    public MOVE getMove(Game game, long timeDue) {
        if(game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade()).length > 1){
            long startTime = System.currentTimeMillis();
            TrainingData currentPoint = new TrainingData(game, MOVE.NEUTRAL);
            DistanceComparator<TrainingData> comp = new DistanceComparator<TrainingData>(currentPoint);
            PriorityQueue<TrainingData> dset = new PriorityQueue<TrainingData>(comp);
            for(TrainingData t:td){            
                dset.add(t);
            }

            HashMap<MOVE, Integer> topk = new HashMap<MOVE, Integer>();
            for(int i = 0; i < k; i++){
                TrainingData top = dset.poll();
                if(!topk.containsKey(top.decision)){
                    topk.put(top.decision, 0);
                }
                topk.put(top.decision, topk.get(top.decision) + 1);
            }
            int topval = 0;
            MOVE topMove = MOVE.NEUTRAL;
            for(Entry<MOVE, Integer> e:topk.entrySet()){
                if(e.getValue() > topval){
                    topval = e.getValue();
                    topMove = e.getKey();
                }
            }
            System.out.println("This took " + (System.currentTimeMillis() - startTime) + "ms");
            return topMove;
        }
        else{
            return game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade())[0];
        }
    }
    
    public class DistanceComparator<T> implements Comparator<T>{
        TrainingData basic;
        public DistanceComparator(TrainingData base){
            basic = base;
        }
        @Override
        public int compare(T t, T t1) {
            TrainingData a = (TrainingData) t;
            TrainingData b = (TrainingData) t1;
            //System.out.println("Stuff!");
            if(basic.distance(a) < basic.distance(b)){
                return -1;
            }
            else{
                return 1;
            }
            
        }

    }
    
}
