/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman.learning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class DataConverter {
    public void convertData(String file){
        try{
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fin);
            ArrayList<TrainingData> biglist = (ArrayList<TrainingData>) ois.readObject();
            String s = "";
            for(TrainingData t:biglist){
                s += t.toString();
            }
            FileOutputStream fout = new FileOutputStream("HRData.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(s);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
