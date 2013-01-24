/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GeneticAlgorithm;

import engine.CellularAutomataEngine;
import java.util.Arrays;
import tests.CellularAutomataTest;

/**
 *
 * @author Hodga
 */
public class StateHashKey {

    private int[] state;
    private int hashKey;
    
    public StateHashKey(int[] state) {
        this.state = state;
        hashKey = Arrays.hashCode(state);
        
        //int base = CellularAutomataEngine.getNbStates();
        //hashKey = 0;
        //for(int i = 0; i < state.length;i++) hashKey += Math.pow(base, i) * state[i];
    }

    public int[] getState() {
        return state;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof StateHashKey)) return false;
        int[] comp = ((StateHashKey)obj).getState();        
        if(comp.length != state.length) return false;
        return Arrays.equals(state, comp);
        //for (int i = 0; i < state.length; i++) if(state[i] != comp[i]) return false;
        //return true;
    }

    @Override
    public int hashCode() {
        return hashKey;
    }

}
