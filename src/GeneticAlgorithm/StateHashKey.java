/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GeneticAlgorithm;

import engine.CellularAutomataEngine;
import java.util.Arrays;

/**
 *
 * @author Hodga
 */
public class StateHashKey {

    int[] state;
    int hashKey;
    
    public StateHashKey(int[] state) {
        this.state = state;
        hashKey = Arrays.hashCode(state);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof int[])) return false;
        int[] comp = (int[])obj;
        if(comp.length != state.length) return false;
        return Arrays.equals(state, comp);
    }

    @Override
    public int hashCode() {
        return hashKey;
    }

}
