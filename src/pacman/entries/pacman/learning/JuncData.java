/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.entries.pacman.learning;

import java.io.Serializable;
import pacman.game.Constants.MOVE;

/**
 *
 * @author Admin
 */
public class JuncData implements Serializable{
        public JuncData(int points, int gdist, int endstatus, MOVE dir){
            pointval = points;
            nearestGhostDist = gdist;
            end = endstatus;
        }
        MOVE direction;
        int pointval;
        int nearestGhostDist;
        int end;
    }