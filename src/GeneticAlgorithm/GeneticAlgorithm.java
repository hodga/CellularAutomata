/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GeneticAlgorithm;

import engine.CellularAutomataEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Hodga
 */
public class GeneticAlgorithm {
    
    //Static variables for configuring GA
    private static int initialPopulation = 24;
    
    private static int populationToPreserve = 16;
    
    private static float crossoverRate = 0.70f;
    
    private static float mutationRate = 0.02f; //2%
    
    private static int quiecentState = 0;
    
    private static boolean TrajectoryAttractorLength = true; //true is trajectory length and false is attractor length
    
    private static boolean useLambdaInFitness = false;
    
    private static boolean useLambdaToDiscard = false;
    
    private static float lambdaUpperLimit;
    private static float lambdaLimitFromBest = 0.05f;
    
    private static boolean useRandomInitialPopulation = false;

    private static Random randomGen = new Random();
    
    //Execution variables
    GenomeData[] population;
    int targetFitness;
    int maxGenerations;
    int generation;
    GenomeData best;
    
    //Statistics variables
    ArrayList<GenomeData> bestGenomePerGeneration = new ArrayList<GenomeData>();

    //getters and setters for static variables. shouldnot be used normally, only to tune the algorithm
    public static boolean isTrajectoryAttractorLength() {return TrajectoryAttractorLength;}
    public static float getCrossoverRate() {return crossoverRate;}
    public static int getInitialPopulation() {return initialPopulation;}
    public static float getLambdaLimitFromBest() {return lambdaLimitFromBest;}
    public static float getLambdaUpperLimit() {return lambdaUpperLimit;}
    public static float getMutationRate() {return mutationRate;}
    public static int getPopulationToPreserve() {return populationToPreserve;}
    public static int getQuiecentState() {return quiecentState;}
    public static boolean isUseLambdaInFitness() {return useLambdaInFitness;}
    public static boolean isUseLambdaToDiscard() {return useLambdaToDiscard;}
    public static boolean isUseRandomInitialPopulation() {return useRandomInitialPopulation;}

    public static void setTrajectoryAttractorLength(boolean TrajectoryAttractorLength) {GeneticAlgorithm.TrajectoryAttractorLength = TrajectoryAttractorLength;}
    public static void setCrossoverRate(float crossoverRate) {GeneticAlgorithm.crossoverRate = crossoverRate;}
    public static void setInitialPopulation(int initialPopulation) {GeneticAlgorithm.initialPopulation = initialPopulation;}
    public static void setLambdaLimitFromBest(float lambdaLimitFromBest) {GeneticAlgorithm.lambdaLimitFromBest = lambdaLimitFromBest;}
    public static void setLambdaUpperLimit(float lambdaUpperLimit) {GeneticAlgorithm.lambdaUpperLimit = lambdaUpperLimit;}
    public static void setMutationRate(float mutationRate) {GeneticAlgorithm.mutationRate = mutationRate;}
    public static void setPopulationToPreserve(int populationToPreserve) {GeneticAlgorithm.populationToPreserve = populationToPreserve;}
    public static void setQuiecentState(int quiecentState) {GeneticAlgorithm.quiecentState = quiecentState;}
    public static void setUseLambdaInFitness(boolean useLambdaInFitness) {GeneticAlgorithm.useLambdaInFitness = useLambdaInFitness;}
    public static void setUseLambdaToDiscard(boolean useLambdaToDiscard) {GeneticAlgorithm.useLambdaToDiscard = useLambdaToDiscard;}
    public static void setUseRandomInitialPopulation(boolean useRandomInitialPopulation) {GeneticAlgorithm.useRandomInitialPopulation = useRandomInitialPopulation;}

    public GeneticAlgorithm(int targetFitness, int maxGenerations) {
        this.targetFitness = targetFitness;
        this.maxGenerations = maxGenerations;
        
        population = new GenomeData[initialPopulation];
        createIntialPopulation();
    }
    
    public class Fitness extends Thread {
        
        
        private GenomeData genome;
        HashMap<StateHashKey, Integer> searchTable = new HashMap<StateHashKey, Integer>();

        public Fitness(GenomeData genome) {
            this.genome = genome;
        }
        
        /**
         * to reuse the same object just use this function
         * @param genome
         */
        public void reuse(GenomeData genome) {
            this.genome = genome;
            searchTable.clear();
        }

        @Override
        public void run() {
            if(genome.hasFitness()) return;
            
            int trajectoryLength = 0;
            CellularAutomataEngine ca = new CellularAutomataEngine(genome.getGenome());
            StateHashKey key = new StateHashKey(ca.getState());
            
            Integer hit = null;
            while(hit == null) {
                searchTable.put(key, trajectoryLength);
                
                trajectoryLength++;
                ca.step();
                
                key = new StateHashKey(ca.getState());
                hit = searchTable.get(key);
            }

            if(TrajectoryAttractorLength) {
                genome.setFitness(Math.abs(targetFitness - trajectoryLength));
            }
            else {
                genome.setFitness(Math.abs(targetFitness - (trajectoryLength - hit) ));
            }

            genome.setGenomeUsageStatistics(ca.getGenomeUsageStatistics());
            int usedGenes = 0;
            for(int i : genome.getGenomeUsageStatistics()) if(i != 0) usedGenes++;
            genome.setGenomeUsageFactor((float)usedGenes/(float)genome.getGenomeUsageStatistics().length);
        }
    }

    public void getLambda(GenomeData genome) {
        int n = 0;
        for(int i : genome.getGenome())
        {
            if(i == quiecentState) n++;
        }
        genome.setLambda((float)(genome.getGenome().length - n)/(float)(genome.getGenome().length));
    }

    private void sortPopulationByFitness(boolean adjustedFitness) {
        int swaps;
        do
        {
            swaps = 0;
            for(int i = 1; i < initialPopulation;i++)
            {
                GenomeData a = population[i-1];
                GenomeData b = population[i];

                if(adjustedFitness) {
                    if(a.getAdjustedFitness() < b.getAdjustedFitness())
                    {
                        population[i-1] = b;
                        population[i] = a;
                        swaps++;
                    }
                }
                else {
                    if(a.getFitness() > b.getFitness())
                    {
                        population[i-1] = b;
                        population[i] = a;
                        swaps++;
                    }
                }
            }
        }while(swaps != 0);
    }

    /*
        Roulette wheel selection from the current population. T
        he population should have a total adjusted fitness of 1, or 2 if lambda is used.
        The best fitnesses should be located first (i = 0 is the best) and their values should be the biggest.
    */
    private GenomeData rouletteWheelSelection() {

        double selectValue = randomGen.nextDouble();

            if(useLambdaInFitness)selectValue *= 2;

        GenomeData g = null;
        for(int j = 0; j < initialPopulation;j++) {
            g = population[j];
            if(selectValue > g.getAdjustedFitness()) {
                selectValue -= g.getAdjustedFitness();
            }
            else  break;
        }
        return g;
    }

    /*
        Mutate genome where each value in the genome has the probability found in mutate(L) to increment by one.
    */
    void mutate_genome(GenomeData mutatingGenome) {
        for(int j = 0; j < mutatingGenome.getGenome().length;j++) {
            if(randomGen.nextDouble() <= mutationRate) {
               mutatingGenome.getGenome()[j] = (mutatingGenome.getGenome()[j]+1) % CellularAutomataEngine.getNbStates();
               mutatingGenome.setFitness(-1);
            }
        }
    }

    private boolean genomeEqual(int[] a, int[] b) {
        if(a.length != b.length) return false;
        return Arrays.equals(a, b);
    }

    private int[] randomUniqueGenome() {
        int[] genome = new int[CellularAutomataEngine.getGenomeSize()];
        boolean unique = false;
        while(!unique) {
            unique = true;
            for (int i = 0; i < genome.length; i++) {
                genome[i] = randomGen.nextInt(CellularAutomataEngine.getNbStates());
            }

            for (int i = 0; i < population.length; i++) {
                if(population[i] == null) break;
                if(genomeEqual(genome, population[i].getGenome())) {
                    unique = false;
                    break;
                }
            }
        }
        return genome;
    }

    private void createIntialPopulation() {
        for (int i = 0; i < initialPopulation; i++) {
            if(useRandomInitialPopulation) {
                population[i] = new GenomeData(randomUniqueGenome());
                if(useLambdaToDiscard) {
                    getLambda(population[i]);
                    if(population[i].getLambda() > lambdaUpperLimit) i--;
                }
            }
            else {
                population[i] = new GenomeData(new int[CellularAutomataEngine.getGenomeSize()]);
            }
        }
    }
    
    public GenomeData runGA() {
        return null;
    }
    //Methods
    
    
    
    
    
    
    
    

}
