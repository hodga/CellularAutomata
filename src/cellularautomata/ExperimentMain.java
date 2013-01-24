/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cellularautomata;

import engine.CellularAutomataEngine;

/**
 *
 * @author Hodga
 */
public class ExperimentMain {
    
    /**
     * s is the sides of the state space. all will be similar to make quadratic or cubic state space for 2d or 3d ca.
     * This is convenient for tests.
     */
    public static void configureCA(int neighborhoodSize, int nbStates, boolean wrapAround, int s) {
        CellularAutomataEngine.setNeighborhoodSize(neighborhoodSize);
        CellularAutomataEngine.setNbStates(nbStates);
        CellularAutomataEngine.setWrapAround(wrapAround);
        
        int[] dim;
        int[] intitalState;
        
        if(neighborhoodSize == 3) {
            dim = new int[]{s};
            intitalState = new int[s];
        }
        else if(neighborhoodSize == 5 || neighborhoodSize == 9) {
            dim = new int[]{s, s};
            intitalState = new int[s*s];
        }
        else {
            dim = new int[]{s, s, s};
            intitalState = new int[s*s*s];
        }
        
        intitalState[intitalState.length/2] = 1; //put it in center for easy viewing
        
        CellularAutomataEngine.setDim(dim);
        CellularAutomataEngine.setInitialState(intitalState);
    }
    public static void ComplexitySearch(int[] dim, int neighborhoodSize, int nbStates, int runs, int maxGenerations, String filePath, int[] complexities) {
        //initialize CA
        if(neighborhoodSize != 3 || neighborhoodSize != 5 || neighborhoodSize != 7 || neighborhoodSize != 9 || neighborhoodSize != 27) {
            System.out.println("wrong neighborhood size");
            return;
        }
        if(nbStates <= 1) {
            System.out.println("number of states must be a positive integer larger than 1");
            return;
        }
        CellularAutomataEngine.setNeighborhoodSize(neighborhoodSize);
        CellularAutomataEngine.setNbStates(nbStates);
        CellularAutomataEngine.setWrapAround(true);
        CellularAutomataEngine.setDim(dim);
        
        int stateSize  = 0;
        for (int i : dim) stateSize += i;
        int[] initialState = new int[stateSize];
        initialState[0] = 1;
        CellularAutomataEngine.setInitialState(initialState);
        if(!CellularAutomataEngine.verifyDim()) return;
        
        
        
        
        /*BufferedWriter bw = new BufferedWriter(new FileWriter(absPath+"Avg_stdDev_complexity_"+complexities[i]+"_runs_"+runs+".gatest"));
          bw.write("#generation\taverage fitness\tstandard deviation\thighest fitness\tlowest fitness\n");
          for (int j = 0; j < allRuns.size(); j++) {
              bw.write(j+"\t"+average.get(j)+"\t"+stdDeviation.get(j)+"\t"+highest.get(j)+"\t"+lowest.get(j)+"\n");
          }
          bw.close();*/
        
        
    }
}
