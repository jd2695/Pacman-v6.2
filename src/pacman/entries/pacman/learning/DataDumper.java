package pacman.entries.pacman.learning;

import pacman.controllers.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/*
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public class DataDumper extends HumanController
{
    public KeyBoardInput input;
    ArrayList<TrainingData> tdat = new ArrayList<TrainingData>();
    public DataDumper(KeyBoardInput input)
    {
        super(input);
    	this.input=input;
    }
    
    public KeyBoardInput getKeyboardInput()
    {
    	return input;
    }

    public MOVE getMove(Game game,long dueTime)
    {
        int key = input.getKey();
        //System.out.println(input.getKey());
        /*
        if(game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade()).length > 1){
            
            MOVE rmove = MOVE.NEUTRAL;
            
            switch(input.getKey())
            {
	    	case KeyEvent.VK_UP:    rmove =  MOVE.UP;
	    	case KeyEvent.VK_RIGHT: rmove =  MOVE.RIGHT;
	    	case KeyEvent.VK_DOWN: 	rmove =  MOVE.DOWN;
	    	case KeyEvent.VK_LEFT: 	rmove =  MOVE.LEFT;
	    	//default: 				rmove =  MOVE.NEUTRAL;
            }
            //tdat.add(new TrainingData(game, rmove));
            //System.out.println(tdat);
            return rmove;
        }
         */
        if(game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade()).length > 1){
            MOVE rmove = MOVE.NEUTRAL;
            
            switch(key)
            {
	    	case KeyEvent.VK_UP:    rmove =  MOVE.UP;
	    	case KeyEvent.VK_RIGHT: rmove =  MOVE.RIGHT;
	    	case KeyEvent.VK_DOWN: 	rmove =  MOVE.DOWN;
	    	case KeyEvent.VK_LEFT: 	rmove =  MOVE.LEFT;
	    	//default: 				rmove =  MOVE.NEUTRAL;
            }
            tdat.add(new TrainingData(game, rmove));
            if(tdat.size() > 50){
                
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
                        System.out.println("Writing to file!  Current size: " + biglist.size());
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
            //System.out.println(tdat);
        }
    	switch(key)
    	{
	    	case KeyEvent.VK_UP: 	return MOVE.UP;
	    	case KeyEvent.VK_RIGHT: return MOVE.RIGHT;
	    	case KeyEvent.VK_DOWN: 	return MOVE.DOWN;
	    	case KeyEvent.VK_LEFT: 	return MOVE.LEFT;
	    	default: 				return MOVE.NEUTRAL;
    	}
    }
}