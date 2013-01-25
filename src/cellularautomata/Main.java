/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cellularautomata;

import tests.GeneticAlgorithmTest;

/**
 *
 * @author Hodga
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        //CellularAutomataTest.test();        
        //GeneticAlgorithmTest.test();
        
        int[] dim = {4, 4};
        int neighborhoodSize = 5;
        int nbStates = 3;
        int startRun = 10;
        int endRun = 20;
        int maxGenerations = 100000;
        String filePath = "D:\\Development\\Projects\\Cellular_Automata\\InitialTestsForMaster\\Test_with_lambda\\";
        int[]complexities = { 25000 };
        
        ExperimentMain.ComplexitySearch(dim, neighborhoodSize, nbStates, startRun, endRun, maxGenerations, filePath, complexities);
    }

}
