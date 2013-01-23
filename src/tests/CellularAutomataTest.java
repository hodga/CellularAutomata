/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import engine.CellularAutomataEngine;

/**
 *
 * @author Hodga
 */
public class CellularAutomataTest {
    
    public static void test1d() {
        System.out.println("Testing 2 dimensional CAs");
        //testing rule 30;
        configureCA(3, 2, true, 51);
        
        CellularAutomataEngine cE = new CellularAutomataEngine();

        int[][] rule_30 = {{1,1,1},{1,1,0}, {1,0,1}, {1,0,0}, {0,1,1}, {0,1,0}, {0,0,1}, {0,0,0} };
        int[] res_30 = {0,0,0,1,1,1,1,0};
        addRules(cE, rule_30, res_30);
        
        System.out.println("rule 30...");
        printState1d(cE.getState());
        for(int i = 0; i < 20; i++) {
            cE.step();
            printState1d(cE.getState());
        }
        
        //testing wrap around
        configureCA(3, 3, true, 10);
        
        int rules1[][] = {{1,0,0}, {2,0,0}};
        int results1[] = {2, 1};
        
        
        cE = new CellularAutomataEngine();
        addRules(cE, rules1, results1);
        
        System.out.println("wrap around1, shouldwrap around");
        printState1d(cE.getState());
        for(int i = 0; i < 10; i++) {
            cE.step();
            printState1d(cE.getState());
        }
        
        CellularAutomataEngine.setWrapAround(false);
        cE = new CellularAutomataEngine();
        addRules(cE, rules1, results1);
        
        System.out.println("wrap around2... should not wrap around");
        printState1d(cE.getState());
        for(int i = 0; i < 10; i++) {
            cE.step();
            printState1d(cE.getState());
        }
        
        //testing all neighbors;
        configureCA(3, 3, true, 5);
        
        
        cE = new CellularAutomataEngine();
        int rules2[][] = {{1,0,0}, {0,0,1}, {0,1,0}, {2, 0, 2}};
        int results2[] = {2, 2, 0, 1};
        addRules(cE, rules2, results2);
        
        System.out.println("testing all neighbors...");
        printState1d(cE.getState());
        for(int i = 0; i < 2; i++) {
            cE.step();
            printState1d(cE.getState());
        }
        System.out.println("done testing 1d CA.....");
    }
    
    public static void test2d() {
        System.out.println("testing 2 dimensional CAs");
        
        //5 neighborhood
        configureCA(5, 3, true, 5);
        
        CellularAutomataEngine cE = new CellularAutomataEngine();

        int rules51[][] = {{0,  0,0,0,  1}, {1,  0,0,0,  0}, {0,  1,0,0,  0}, {0,  0,0,1,  0}, {0,  0,1,0,  0}, {2,  2,0,2,  2}};
        int results51[] = {2, 2, 2, 2, 0, 1};
        addRules(cE, rules51, results51);
        
        System.out.println("testing 5 neighborhood all neighbors...");
        printState2d(cE.getState(), 5);
        for(int i = 0; i < 2; i++) {
            cE.step();
            printState2d(cE.getState(), 5);
        }
        
        cE = new CellularAutomataEngine();
        
        int rules52[][] = {{0,  0,0,0,  1}, {0,  0,0,0,  2}};
        int results52[] = {2, 1};
        
        addRules(cE, rules52, results52);
        
        System.out.println("testing 5 neighborhood wrap around... should wrap around");
        printState2d(cE.getState(), 5);
        for(int i = 0; i < 5; i++) {
            cE.step();
            printState2d(cE.getState(), 5);
        }
        
        CellularAutomataEngine.setWrapAround(false);
        cE = new CellularAutomataEngine();
        
        addRules(cE, rules52, results52);
        
        System.out.println("testing 5 neighborhood wrap around... should not wrap around");
        printState2d(cE.getState(), 5);
        for(int i = 0; i < 5; i++) {
            cE.step();
            printState2d(cE.getState(), 5);
        }
        
        // 9 neighborhood
        configureCA(9, 3, true, 5);
        cE = new CellularAutomataEngine();
        int rules91[][] = {    {
                            1,0,0,
                            0,0,0,
                            0,0,0
                            },
                            {
                            0,1,0,
                            0,0,0,
                            0,0,0
                            },
                            {
                            0,0,1,
                            0,0,0,
                            0,0,0
                            },
                            {
                            0,0,0,
                            1,0,0,
                            0,0,0
                            },
                            {
                            0,0,0,
                            0,0,1,
                            0,0,0
                            },
                            {
                            0,0,0,
                            0,0,0,
                            1,0,0
                            },
                            {
                            0,0,0,
                            0,0,0,
                            0,1,0
                            },
                            {
                            0,0,0,
                            0,0,0,
                            0,0,1
                            },
                            {
                            0,0,0,
                            0,1,0,
                            0,0,0
                            },
                            {
                            2,2,2,
                            2,0,2,
                            2,2,2
                            }

                        };
        int results91[] = {2, 2, 2, 2, 2, 2, 2, 2, 0, 1};
        addRules(cE, rules91, results91);
        
        System.out.println("testing 9 neighborhood all neighbors...");
        printState2d(cE.getState(), 5);
        for(int i = 0; i < 2; i++) {
            cE.step();
            printState2d(cE.getState(), 5);
        }
        
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
        
        addRules(cE, rules92, results92);
        
        System.out.println("testing 9 neighborhood wrap around... should wrap around");
        printState2d(cE.getState(), 5);
        for(int i = 0; i < 5; i++) {
            cE.step();
            printState2d(cE.getState(), 5);
        }
        
        CellularAutomataEngine.setWrapAround(false);
        cE = new CellularAutomataEngine();
        
        addRules(cE, rules92, results92);
        
        System.out.println("testing 9 neighborhood wrap around... should not wrap around");
        printState2d(cE.getState(), 5);
        for(int i = 0; i < 5; i++) {
            cE.step();
            printState2d(cE.getState(), 5);
        }
        System.out.println("done testing 2d CA");
    }
    
    public static void test3d() {
        System.out.println("testing 3 dimensional CAs");
        
        //5 neighborhood
        configureCA(7, 3, true, 5);
        
        CellularAutomataEngine cE = new CellularAutomataEngine();

        int rules71[][] = {{1, 0, 0,0,0, 0, 0}, {0, 0, 0,0,0, 0, 1}, {0, 1, 0,0,0, 0, 0},
                        {0, 0, 1,0,0, 0, 0}, {0, 0, 0,0,0, 1, 0}, {0, 0, 0,0,1, 0, 0},
                        {0, 0, 0,1,0, 0, 0}, {2, 2, 2,0,2, 2, 2}};
        int results71[] = {2, 2, 2, 2, 2, 2, 0, 1};
        addRules(cE, rules71, results71);
        
        System.out.println("testing 7 neighborhood all neighbors...");
        printState3d(cE.getState(), 5, 5, 5);
        for(int i = 0; i < 2; i++) {
            cE.step();
            printState3d(cE.getState(), 5, 5, 5);
        }
        
        cE = new CellularAutomataEngine();
        
        int rules72[][] = {{0, 0, 0,0,0, 0, 1}, {0, 0, 0,0,0, 0, 2}};
        int results72[] = {2, 1};
        
        addRules(cE, rules72, results72);
        
        System.out.println("testing 7 neighborhood wrap around... should wrap around");
        printState3d(cE.getState(), 5, 5, 5);
        for(int i = 0; i < 5; i++) {
            cE.step();
            printState3d(cE.getState(), 5, 5, 5);
        }
        
        CellularAutomataEngine.setWrapAround(false);
        cE = new CellularAutomataEngine();
        
        addRules(cE, rules72, results72);
        
        System.out.println("testing 7 neighborhood wrap around... should not wrap around");
        printState3d(cE.getState(), 5, 5, 5);
        for(int i = 0; i < 5; i++) {
            cE.step();
            printState3d(cE.getState(), 5, 5, 5);
        }        
        
        System.out.println("done testing 3d CA");
    }
    
    public static void test() {
        test1d();
        test2d();
        test3d();
    }
    
    /**
     * configurations are the neighborhood configurations
     * genes are the resulting values from the given configurations.
     * @param configurations 
     * @param genes 
     */
    public static void addRules(CellularAutomataEngine cE,int[][] configurations, int[] genes) {
        for(int i = 0; i < configurations.length;i++) {
            cE.putGene(configurations[i], genes[i]);
        }
    }
    
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
    
    
    public static void printState1d(int[] state) {
        for(int ind = 0; ind < state.length;ind++) {
            System.out.print(state[ind]);
        }
        System.out.print("\n");
    }
    
    public static void printState2d(int[] state, int xDim) {
        for(int ind = 0; ind < state.length;ind++) {
            if(ind % xDim == 0) System.out.print("\n");
            System.out.print(state[ind]);
        }
        System.out.print("\n");
    }
    
    public static void printState3d(int[] state, int xDim, int yDim, int zDim) {
        for(int y = 0; y < yDim;y++) {
            for(int z = 0; z < zDim; z++) {
                for(int x = 0; x < xDim; x++) {
                    int ind = x + (y*xDim)+ (z*xDim*yDim);
                    System.out.print(state[ind]);
                }
                System.out.print("\t");
            }
            System.out.print("\n");
            
        }
        System.out.print("\n");
    }
    
}
