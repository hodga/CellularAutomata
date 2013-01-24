/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import GeneticAlgorithm.GeneticAlgorithm;
import GeneticAlgorithm.GenomeData;
import engine.CellularAutomataEngine;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Hodga
 */
public class GeneticAlgorithmTest {
    
    /**
     * validates a trajectory length by running a cellular automata and saving every single result.
     * Then running a brute force comparison on all the states in the trajectory. 
     * If not exactly one pair is matching (or one state in the pair is not the last one) the validation fails
     * @return true if exactly one similar pair of states is created after trajectoryLength steps of the CA and one of the states is the last one
     */
    public static boolean validateGenomeTrajectoryLength(int[] genome, int trajectorylength) {
        CellularAutomataEngine ca = new CellularAutomataEngine(genome);
        
        ArrayList<int[]> trajectory = new ArrayList<int[]>();
        trajectory.add(ca.getState());
        for(int i = 0; i < trajectorylength;i++) {
            ca.step();
            trajectory.add(ca.getState());
        }
        
        int pairs = 0;
        int index1 = -1;
        int index2 = -1;
        
        for (int i = 0; i < trajectory.size()-1; i++) {
            for (int j = i; j < trajectory.size(); j++) {
                
                if(i == j) continue;
                
                if(Arrays.equals(trajectory.get(i), trajectory.get(j))) {
                    pairs++;
                    index1 = i;
                    index2 = j;
                }
                
            }
            
        }
        
        return pairs == 1 && (index1 == trajectorylength || index2 == trajectorylength);
    }
    
    public static void testFitness() throws Exception {
        boolean passed = true;
        //using 1d ca with wrap around. this should be a trajectory length of 10
        System.out.println("testing fitness on 1d CA with the wrap around genome from the CA tests");
        CellularAutomataTest.configureCA(3, 3, true, 10);
        int rules1[][] = {{1,0,0}, {2,0,0}};
        int results1[] = {2, 1};
        CellularAutomataEngine cE = new CellularAutomataEngine();
        CellularAutomataTest.addRules(cE, rules1, results1);
        int[] genome = cE.getGenome();
        
        GenomeData data1D = new GenomeData(genome);
        
        GeneticAlgorithm ga = new GeneticAlgorithm(0, 100000);
        
        ga.testFitness(data1D);
        
        boolean valid = validateGenomeTrajectoryLength(genome, data1D.getFitness());
        if(!valid)passed = false;
        System.out.println("trajectory length: "+data1D.getFitness()+" should be 10, valid: "+valid);
        
        if(validateGenomeTrajectoryLength(genome, data1D.getFitness()+1) || validateGenomeTrajectoryLength(genome, data1D.getFitness()-1)) {
            passed = false;
            System.out.println("Validation method validates unvalid trajectory lengths");
        }
        
        //using 2d ca with wrap around. this should be a trajectory length of 10
        System.out.println("testing fitness on 2d CA with the wrap around genome from the CA tests");
        System.out.println("5 neighborhood");
        CellularAutomataTest.configureCA(5, 3, true, 5);
        cE = new CellularAutomataEngine();
        
        int rules52[][] = {{0,  0,0,0,  1}, {0,  0,0,0,  2}};
        int results52[] = {2, 1};
        
        CellularAutomataTest.addRules(cE, rules52, results52);
        genome = cE.getGenome();
        
        GenomeData data2D5 = new GenomeData(genome);
        ga = new GeneticAlgorithm(0, 100000);
        
        ga.testFitness(data2D5);
        valid = validateGenomeTrajectoryLength(genome, data2D5.getFitness());
        if(!valid)passed = false;
        System.out.println("trajectory length: "+data2D5.getFitness()+" should be 10, valid: "+valid);
        
        if(validateGenomeTrajectoryLength(genome, data2D5.getFitness()+1) || validateGenomeTrajectoryLength(genome, data2D5.getFitness()-1)) {
            passed = false;
            System.out.println("Validation method validates unvalid trajectory lengths");
        }
        
        
        //9 neigborhood
        System.out.println("9 neighborhood");
        CellularAutomataTest.configureCA(9, 3, true, 5);
        cE = new CellularAutomataEngine();
        
        int rules92[][] = {
                            {
                            1,0,0,
                            0,0,0,
                            0,0,0
                            },
                            {
                            2,0,0,
                            0,0,0,
                            0,0,0
                            }
                        };
        int results92[] = {2, 1};
        CellularAutomataTest.addRules(cE, rules92, results92);
        genome = cE.getGenome();
        
        GenomeData data2D9 = new GenomeData(genome);
        ga = new GeneticAlgorithm(0, 100000);
        
        ga.testFitness(data2D9);
        valid = validateGenomeTrajectoryLength(genome, data2D9.getFitness());
        if(!valid)passed = false;
        System.out.println("trajectory length: "+data2D9.getFitness()+" should be 10, valid: "+valid);
        
        if(validateGenomeTrajectoryLength(genome, data2D9.getFitness()+1) || validateGenomeTrajectoryLength(genome, data2D9.getFitness()-1)) {
            passed = false;
            System.out.println("Validation method validates unvalid trajectory lengths");
        }
        
        //using 3d ca with wrap around and all neighbors. this should be a trajectory length of 2 and 10
        System.out.println("testing fitness on 3d CA with the all neighbors and wrap around genome from the CA tests");
        CellularAutomataTest.configureCA(7, 3, true, 5);
        
        //first, 2 steps
        cE = new CellularAutomataEngine();
        
        int rules71[][] = {{1, 0, 0,0,0, 0, 0}, {0, 0, 0,0,0, 0, 1}, {0, 1, 0,0,0, 0, 0},
                        {0, 0, 1,0,0, 0, 0}, {0, 0, 0,0,0, 1, 0}, {0, 0, 0,0,1, 0, 0},
                        {0, 0, 0,1,0, 0, 0}, {2, 2, 2,0,2, 2, 2}};
        int results71[] = {2, 2, 2, 2, 2, 2, 0, 1};
        CellularAutomataTest.addRules(cE, rules71, results71);
        
        genome = cE.getGenome();
        
        GenomeData data3D7 = new GenomeData(genome);
        ga = new GeneticAlgorithm(0, 100000);
        
        ga.testFitness(data3D7);
        valid = validateGenomeTrajectoryLength(genome, data3D7.getFitness());
        if(!valid)passed = false;
        System.out.println("trajectory length: "+data3D7.getFitness()+" should be 2, valid: "+valid);
        
        if(validateGenomeTrajectoryLength(genome, data3D7.getFitness()+1) || validateGenomeTrajectoryLength(genome, data3D7.getFitness()-1)) {
            passed = false;
            System.out.println("Validation method validates unvalid trajectory lengths");
        }
        
        //second, 10 steps
        cE = new CellularAutomataEngine();
        
        int rules72[][] = {{0, 0, 0,0,0, 0, 1}, {0, 0, 0,0,0, 0, 2}};
        int results72[] = {2, 1};
        CellularAutomataTest.addRules(cE, rules72, results72);
        
        genome = cE.getGenome();
        
        GenomeData data3D72 = new GenomeData(genome);
        ga = new GeneticAlgorithm(0, 100000);
        
        ga.testFitness(data3D72);
        valid = validateGenomeTrajectoryLength(genome, data3D72.getFitness());
        if(!valid)passed = false;
        System.out.println("trajectory length: "+data3D72.getFitness()+" should be 10, valid: "+valid);
        
        if(validateGenomeTrajectoryLength(genome, data3D72.getFitness()+1) || validateGenomeTrajectoryLength(genome, data3D72.getFitness()-1)) {
            passed = false;
            System.out.println("Validation method validates unvalid trajectory lengths");
        }
        
        if(passed)System.out.println("============Passed fitness calculation test============");
        else System.out.println("============Did not pass fitness calculation test============");
    }
    
    /**
     * Testing if the ga actually finds genomes with the given targets
     */
    public static boolean testGASingle(int target) throws Exception {
        boolean passed = true;
        
        
        
        ArrayList<int[]> genomesFound = new ArrayList<int[]>();
        for(int i = 0; i < 10; i++) {
            GeneticAlgorithm ga = new GeneticAlgorithm(target, 100000);
            int[] genome = ga.runGA();
            
            boolean unique = true;
            for(int[] old : genomesFound) if(Arrays.equals(genome, old)) unique = false;
            genomesFound.add(genome);
            
            boolean valid = validateGenomeTrajectoryLength(genome, target);
            if(!valid) passed = false;
            
            System.out.println("run: " +i+" found one! Valid: "+valid+" Unique: "+unique);
        }
        
        return passed;     
    }
    
    public static void testGA() throws Exception{
        boolean passed = true;
        
        System.out.println("test 5 neighborhood 3 states");
        //fitst testing with a 4x4 size state 2d cellular automata with 3 states to find genomes of trajectory length 100 and validating the results
        CellularAutomataTest.configureCA(5, 3, true, 4);
        if(!testGASingle(100)) passed = false;
        
        System.out.println("test 3 neighborhood 2 states");
        //fitst testing with a 10 size state 1d cellular automata with 2 states to find genomes of trajectory length 2 and validating the results
        CellularAutomataTest.configureCA(3, 2, true, 10);
        if(!testGASingle(10)) passed = false;
        
        System.out.println("test 9 neighborhood 3 states");
        //fitst testing with a 3x3 size state 2d cellular automata with 4 states to find genomes of trajectory length 10 and validating the results
        CellularAutomataTest.configureCA(9, 3, true, 3);
        if(!testGASingle(20)) passed = false;
        
        System.out.println("test 7 neighborhood 4 states");
        //fitst testing with a 3x3x3 size state 3d cellular automata with 4 states to find genomes of trajectory length 1000 and validating the results
        CellularAutomataTest.configureCA(7, 4, true, 3);
        if(!testGASingle(100)) passed = false;
        
        if(passed)System.out.println("============Passed genetic algorithm search test============");
        else System.out.println("============Did not pass genetic algorithm search test============");
    }
    
    public static void test() throws Exception{
        testFitness();
        testGA();
    }
    
}
