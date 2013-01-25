/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cellularautomata;

import GeneticAlgorithm.GeneticAlgorithm;
import GeneticAlgorithm.GenomeData;
import engine.CellularAutomataEngine;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import tests.GeneticAlgorithmTest;

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
    public static void ComplexitySearch(int[] dim, int neighborhoodSize, int nbStates, int startRun, int endRun, int maxGenerations, String filePath, int[] complexities) throws Exception {
        //initialize CA
        if(neighborhoodSize != 3 && neighborhoodSize != 5 && neighborhoodSize != 7 && neighborhoodSize != 9 && neighborhoodSize != 27) {
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
        
        int stateSize  = 1;
        for (int i : dim) stateSize *= i;
        int[] initialState = new int[stateSize];
        initialState[0] = 1;
        CellularAutomataEngine.setInitialState(initialState);
        if(!CellularAutomataEngine.verifyDim()) return;
        
        for(int i = 0; i < complexities.length; i++) {
            ArrayList<int[]> genomesFound = new ArrayList<int[]>();
            for (int j = startRun; j < endRun; j++) {
                GeneticAlgorithm ga = new GeneticAlgorithm(complexities[i], maxGenerations);
                int[] genome = ga.runGA();
                
                ArrayList<GenomeData> statistics = ga.getBestGenomePerGeneration();
                
                
                boolean unique = true;
                for(int[] old : genomesFound) if(Arrays.equals(genome, old)) unique = false;
                genomesFound.add(genome);
                if(!unique)
                    System.out.println("================== not unique! ============================");
                if(!GeneticAlgorithmTest.validateGenomeTrajectoryLength(genome, complexities[i])) {
                    System.out.println("Not a valid trajectory length for the genome! Something wrong with the algorithm?");
                }
                
                if(!filePath.endsWith("\\")) filePath+= "\\";
                BufferedWriter bw = new BufferedWriter(new FileWriter(filePath+"Complexity_"+complexities[i]+"_RunNo_"+j+".gatest"));
                
                if(bw != null) {
                    //write the genome found
                    String s = "#Genome: ";
                    for(int g : genome) s += g;
                    s+= "\n";
                    bw.write(s);
                    bw.write("#generation   fitness  lambda  genome_usage\n");
                    
                    for(int k = 0; k < statistics.size();k++) 
                        bw.write(k+"   "+statistics.get(k).getFitness()+"  "+statistics.get(k).getLambda()+"  "+statistics.get(k).getGenomeUsageFactor()+"\n");
                    
                }
                else System.out.println("could not write file");
                bw.close();
            }
        }
        
        
        /*BufferedWriter bw = new BufferedWriter(new FileWriter(absPath+"Avg_stdDev_complexity_"+complexities[i]+"_runs_"+runs+".gatest"));
          bw.write("#generation\taverage fitness\tstandard deviation\thighest fitness\tlowest fitness\n");
          for (int j = 0; j < allRuns.size(); j++) {
              bw.write(j+"\t"+average.get(j)+"\t"+stdDeviation.get(j)+"\t"+highest.get(j)+"\t"+lowest.get(j)+"\n");
          }
          bw.close();*/
        
        
    }
}
