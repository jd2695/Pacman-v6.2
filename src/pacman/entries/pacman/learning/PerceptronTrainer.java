/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman.learning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import pacman.game.Constants.MOVE;

/**
 *
 * @author Admin
 */
public class PerceptronTrainer {
    ArrayList<Perceptron> students;
    int acceptableError = 0;
    public PerceptronTrainer(File f){
        students = new ArrayList<Perceptron>();
        students.add(new Perceptron(MOVE.UP));
        students.add(new Perceptron(MOVE.DOWN));
        students.add(new Perceptron(MOVE.LEFT));
        students.add(new Perceptron(MOVE.RIGHT));
        try{
            FileInputStream fin = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fin);
            ArrayList<TrainingData> data = (ArrayList<TrainingData>) ois.readObject();
            System.out.println("Data size: " + data.size());
            for(Perceptron p:students){
                double errorForgive = acceptableError;
                int errorcount = 1000;
                System.out.println("Training perceptron " + p.output);
                
                errorcount = 0;
                    for(TrainingData t:data){
                        if(Math.signum(p.getSignal(t)) != Math.signum(t.getSign(p.output))){
                            errorcount++;
                        }
                        
                    }
                System.out.println("Initial error: " + errorcount);
                while(errorcount > errorForgive){
                    errorcount = 0;
                    for(TrainingData t:data){
                        if(!p.train(0.01, t)){
                            errorcount++;
                        }
                        
                    }
                    //System.out.println("Currenly training: " + errorcount);
                    errorForgive+= 0.1;
                }
                System.out.println("Final error: " + errorcount);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    public void writeToFile(File f){
        try{
            FileOutputStream fout = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(students);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
}
