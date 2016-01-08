/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman.learning;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.entries.pacman.AStarAgent;
import pacman.entries.pacman.BFSAgent;
import pacman.entries.pacman.GeneticAgent;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 *
 * @author Admin
 */
public class AIWrapper extends Controller<MOVE> {
    ArrayList<TrainingData> tdat = new ArrayList<TrainingData>();
    Controller<MOVE> agent;
    public AIWrapper(Controller<MOVE> a){
        agent = a;
    }
    public Constants.MOVE getMove(Game game,long dueTime)
    {
        Constants.MOVE rmove = Constants.MOVE.NEUTRAL;
        rmove = agent.getMove(game, dueTime);
        if(tdat.size() > 50 || game.wasPacManEaten() || game.gameOver()){
                
                File f = new File("trainingData.txt");
                if(!f.exists() && !f.isDirectory()){
                    try{
                        FileOutputStream fout = new FileOutputStream(f);
                        ObjectOutputStream oos = new ObjectOutputStream(fout);
                        oos.writeObject(tdat);
                    }
                    catch(Exception e){
                        System.out.println("oops something went wrong");
                    }
                }
                else if(f.exists() && !f.isDirectory()){
                    try{
                        FileInputStream fin = new FileInputStream(f);
                        ObjectInputStream ois = new ObjectInputStream(fin);
                        ArrayList<TrainingData> biglist = (ArrayList<TrainingData>) ois.readObject();
                        biglist.addAll(tdat);
                        //System.out.println("Writing to file!  Current size: " + biglist.size());
                        FileOutputStream fout = new FileOutputStream(f);
                        ObjectOutputStream oos = new ObjectOutputStream(fout);
                        oos.writeObject(biglist);
                        tdat.clear();
                    }
                    catch(Exception e){
                        System.out.println("It's really hard to read files");
                    }
                }
                else{
                    System.out.println("File saving failed");
                }
            }
        if(game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade()).length > 1){
            
            tdat.add(new TrainingData(game, rmove));
            
            //System.out.println(tdat);
        }
    	return rmove;
    }
}
