/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cellularautomata;

import GeneticAlgorithm.GeneticAlgorithm;
import GeneticAlgorithm.GenomeData;
import engine.CellularAutomataEngine;
import java.io.BufferedWriter;
import java.io.File;
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

    public static boolean configureCA(int[] dim, int neighborhoodSize, int nbStates) {
        if(neighborhoodSize != 3 && neighborhoodSize != 5 && neighborhoodSize != 7 && neighborhoodSize != 9 && neighborhoodSize != 27) {
            System.out.println("wrong neighborhood size");
            return false;
        }
        if(nbStates <= 1) {
            System.out.println("number of states must be a positive integer larger than 1");
            return false ;
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
        if(!CellularAutomataEngine.verifyDim()) return false;

        return true;
    }
    public static void ComplexitySearch(int[] dim, int neighborhoodSize, int nbStates, int startRun, int endRun, int maxGenerations, String filePath, int[] complexities) throws Exception {
        //initialize CA
        if(!configureCA(dim, neighborhoodSize, nbStates)) return;
        if(!filePath.endsWith("\\")) filePath+= "\\";

            for(int i = 0; i < complexities.length; i++) {
                ArrayList<int[]> genomesFound = new ArrayList<int[]>();
                for (int j = startRun; j < endRun; j++) {
                    GeneticAlgorithm ga = new GeneticAlgorithm(complexities[i], maxGenerations);
                    int[] genome = ga.runGA();

                    ArrayList<GenomeData> statistics = ga.getBestGenomePerGeneration();
                    //ArrayList<Integer> repeatedGenomes = ga.getRepeatedGenomesPerGenerationCumulative();

                    boolean unique = true;
                    for(int[] old : genomesFound) if(Arrays.equals(genome, old)) unique = false;
                    genomesFound.add(genome);
                    if(!unique)
                        System.out.println("================== not unique! ============================");
                    if(!GeneticAlgorithmTest.validateGenomeTrajectoryLength(genome, complexities[i]) && statistics.size() != maxGenerations) {
                        System.out.println("Not a valid trajectory length for the genome! Something wrong with the algorithm?");
                    }
                    BufferedWriter bw = new BufferedWriter(new FileWriter(filePath+"Complexity_"+complexities[i]+"_RunNo_"+j+".gatest"));

                    if(bw != null) {
                        //write the genome found
                        String s = "#Genome: ";
                        for(int g : genome) s += g;
                        s+= "\n";
                        bw.write(s);
                        //String avgDiff = "#Average lambda difference = "+ ga.getAvgLambdaDifference();
                       // bw.write(avgDiff);
                        bw.write("#generation   fitness  lambda  genome_usage\n");

                        for(int k = 0; k < statistics.size();k++)
                            bw.write(k+"   "+statistics.get(k).getFitness()+"  "+statistics.get(k).getLambda()+"  "+statistics.get(k).getGenomeUsageFactor()+"\n");

                    }
                    else System.out.println("could not write file");
                    bw.close();

                    /*bw = new BufferedWriter(new FileWriter(filePath+"Genome_Evolution_Complexity_"+complexities[i]+"_RunNo_"+j+".gatest"));

                    if(bw != null) {
                        //write the genome found
                        String s = "#Genome: ";
                        for(int g : genome) s += g;
                        s+= "\n";
                        bw.write(s);
                        //String avgDiff = "#Average lambda difference = "+ ga.getAvgLambdaDifference();
                       // bw.write(avgDiff);
                        bw.write("#genomeNumber\tGeneIndex\tstateColor\tChangedStateColor\tgddColor\n");
                        GenomeData oldData = null;
                        int genomeCtr = 0;
                        for(int k = 0; k < statistics.size();k++) {
                            GenomeData newData = statistics.get(k);
                            if(oldData == null) {
                                int[] a = newData.getGenomeUsageStatistics();
                               for (int l= 0; l < a.length; l++) {
                                    if(a[l] != 0 ) {
                                        bw.write(genomeCtr+"\t"+l+"\t"+"0x000000"+"\t"+"0xFF0000"+"\t"+"0x000000"+"\n");
                                    }
                               }
                            }
                            else if(newData != oldData) {
                                genomeCtr++;
                               int[] aS = newData.getGenomeUsageStatistics();
                               int[] a = newData.getGenome();
                               int[] b = oldData.getGenome();
                               for (int l= 0; l < aS.length; l++) {
                                    if(aS[l] != 0 ) {
                                        String colorGenome;
                                        String colorChange;
                                        String colorGDD;
                                        if(a[l] == 0 ) colorGenome = "0x000000";
                                        else if (a[l] == 1) colorGenome = "0xFF0000";
                                        else if (a[l] == 2) colorGenome = "0x0000FF";
                                        else colorGenome = "0x00FF00";

                                        if(a[l] == b[l]) colorChange = "0x0000FF";
                                        else colorChange = "0xFF0000";

                                        int center = newData.getCenterCellAtInd(l);
                                        if(a[l] == center) colorGDD = "0x0000FF"; //NONE
                                        else if(center == 0 && a[l] != 0) colorGDD = "0xFF0000"; //GROWTH
                                        else if(center != 0) {
                                            if(a[l] != 0) colorGDD = "0x00FF00"; //DIFF
                                            else colorGDD = "0x000000"; //DEATH
                                        }
                                        else colorGDD = "0xFFFF00"; //SOMETHING WRONG

                                        bw.write(genomeCtr+"\t"+l+"\t"+colorGenome+"\t"+colorChange+"\t"+colorGDD+"\n");
                                    }
                               }
                           }
                           oldData = newData;
                        }
                    }
                    else System.out.println("could not write file 2");
                    bw.close();*/

                    /*int[] lambdaDifferenceCount = ga.getLambdaDifferenceCount();
                    for (int k= 0; k < totalLambdaDifferenceCount.length; k++) {
                        totalLambdaDifferenceCount[k] += lambdaDifferenceCount[k];
                    }

                    bw = new BufferedWriter(new FileWriter(filePath+dirName+"\\LambdaDifference_"+complexities[i]+"_Run_"+j+".gatest"));

                    if(bw != null) {
                        bw.write("#lambdaDifferenceGroup    count\n");

                        for(int k = 0; k < lambdaDifferenceCount.length;k++) {
                            float diffGroup = ((float)(k+1)/100.0f);
                            bw.write(diffGroup+"\t"+lambdaDifferenceCount[k]+"\n");
                        }

                    }
                    else System.out.println("could not write file");
                    bw.close();*/

                }
                //Writing lambda difference counter statistics
                /*BufferedWriter bw = new BufferedWriter(new FileWriter(filePath+dirName+"\\LambdaDifference_"+complexities[i]+"_Runs_"+endRun+".gatest"));

                if(bw != null) {
                    int[] lambdaDifferenceCount = totalLambdaDifferenceCount;
                    bw.write("#lambdaDifferenceGroup    count\n");

                    for(int k = 0; k < lambdaDifferenceCount.length;k++) {
                        float diffGroup = ((float)(k+1)/100.0f);
                        bw.write(diffGroup+"\t"+lambdaDifferenceCount[k]+"\n");
                    }

                }
                else System.out.println("could not write file");
                bw.close();*/
                GraphAvg.run(filePath, complexities[i], endRun);
            }
        
        
        /*BufferedWriter bw = new BufferedWriter(new FileWriter(absPath+"Avg_stdDev_complexity_"+complexities[i]+"_runs_"+runs+".gatest"));
          bw.write("#generation\taverage fitness\tstandard deviation\thighest fitness\tlowest fitness\n");
          for (int j = 0; j < allRuns.size(); j++) {
              bw.write(j+"\t"+average.get(j)+"\t"+stdDeviation.get(j)+"\t"+highest.get(j)+"\t"+lowest.get(j)+"\n");
          }
          bw.close();*/
        
        
    }
}
