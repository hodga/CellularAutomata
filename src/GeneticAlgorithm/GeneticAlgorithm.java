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
import tests.CellularAutomataTest;

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
    private GenomeData[] population;
    private int targetFitness;
    private int maxGenerations;
    private int generation;
    private float bestLambda = -1; //best lambda of previous generation
    private float bestUsage = -1; //best usage of previous generation
    private int crossoverPoint;
    private GenomeData best;
    
    //Statistics variables
    private ArrayList<GenomeData> bestGenomePerGeneration = new ArrayList<GenomeData>();

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

    //uncomment to enable changing of the properties of the genetic algorithm. Useful for tuning.
    /*
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
    */

    public GeneticAlgorithm(int targetFitness, int maxGenerations) {
        lambdaUpperLimit = 1.0f - 1.0f/(float)CellularAutomataEngine.getNbStates();
                
        this.targetFitness = targetFitness;
        this.maxGenerations = maxGenerations;
        
        crossoverPoint = CellularAutomataEngine.getGenomeSize()/2;
        
        population = new GenomeData[initialPopulation];
        createIntialPopulation();
    }

    public ArrayList<GenomeData> getBestGenomePerGeneration() {
        return bestGenomePerGeneration;
    }
    
    public void testFitness(GenomeData genome) throws Exception{
        FitnessThread ft = new FitnessThread(genome);
        ft.start();
        ft.join();
    }
    
    public class FitnessThread extends Thread {
        private GenomeData genome;
        HashMap<StateHashKey, Integer> searchTable = new HashMap<StateHashKey, Integer>();

        public FitnessThread(GenomeData genome) {
            this.genome = genome;
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
        if(genome.getLambda() != -1) return;
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
    void mutateGenome(GenomeData mutatingGenome) {
        for(int j = 0; j < mutatingGenome.getGenome().length;j++) {
            if(randomGen.nextDouble() <= mutationRate) {
               mutatingGenome.getGenome()[j] = (mutatingGenome.getGenome()[j]+1) % CellularAutomataEngine.getNbStates();
               mutatingGenome.setFitness(-1);
               mutatingGenome.setLambda(-1);
            }
        }
    }

    private boolean genomeEqual(int[] a, int[] b) {
        if(a.length != b.length) return false;
        return Arrays.equals(a, b);
    }
    
    private boolean genomeEqual(GenomeData a, GenomeData b) {
        return genomeEqual(a.getGenome(), b.getGenome());
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
    
    public int[] runGA() throws Exception {
                
        generation = 0;
        best = population[0];
        
        while(best.getFitness() != 0 && generation < maxGenerations) {
            GenomeData[] nextPopulation = new GenomeData[initialPopulation];
            FitnessThread[] fitnessChecks = new FitnessThread[initialPopulation];
            generation++;
            
            //Calculate fitnesses using threads.
            for (int i = 0; i < initialPopulation; i++) {
                FitnessThread check = new FitnessThread(population[i]);
                check.start();
                fitnessChecks[i] = check;
            }
            for (int i = 0; i < initialPopulation; i++) {
                fitnessChecks[i].join();
            }
            
            //Sorting the population by fitness
            sortPopulationByFitness(false);
            
            //Find adjusted fitness for roulette wheel
            int worstFitness = population[initialPopulation-1].getFitness();
            double totalFitness = 0;
            
            for(int i = 0; i < initialPopulation;i++) {
                GenomeData g = population[i];
                g.setAdjustedFitness(worstFitness - g.getFitness());
                totalFitness += g.getAdjustedFitness();
            }

            for(int i = 0; i < initialPopulation;i++) {
                GenomeData g = population[i];
                g.setAdjustedFitness(g.getAdjustedFitness()/totalFitness);
            }

            if(bestLambda < 0) {
                getLambda(population[0]);
                bestLambda = population[0].getLambda();
                bestUsage = population[0].getGenomeUsageFactor();
            }
            
            if(useLambdaInFitness) {
                double worstLambda = 0;
                for(int i = 0; i < initialPopulation;i++) {
                    GenomeData g = population[i];
                    getLambda(g);
                    g.setRelativeLambda(Math.abs(g.getLambda() - bestLambda));
                    if(g.getRelativeLambda() > worstLambda) worstLambda = g.getRelativeLambda();
                }
                double totalLambda = 0;
                for(int i = 0; i < initialPopulation;i++) {
                    GenomeData g = population[i];
                    g.setRelativeLambda(worstLambda - g.getRelativeLambda());
                    totalLambda += g.getRelativeLambda();
                }
                for(int i = 0; i < initialPopulation;i++)
                {
                    GenomeData g = population[i];
                    g.setRelativeLambda(g.getRelativeLambda() / totalLambda);
                }
                for(int i = 0; i < initialPopulation;i++)
                {
                    GenomeData g = population[i];
                    g.setAdjustedFitness(g.getAdjustedFitness()+g.getRelativeLambda());
                }
                //needs to sort the population again because lambda might change which genome is the most preferable
                sortPopulationByFitness(true);
            }//useLambdaInFitness
            
            best = population[0];
            
            //keep statistics, using the original object, so it is important to remember that new GenomeData objects need to be created for new genotypes
            bestGenomePerGeneration.add(best);
            
            int i;
            for(i = 0; i < populationToPreserve; i++) {
                nextPopulation[i] = population[i];
            }
            
            while(i < initialPopulation) {
                GenomeData parentA = rouletteWheelSelection();
                GenomeData parentB = rouletteWheelSelection();
                
                GenomeData childA = new GenomeData(new int[CellularAutomataEngine.getGenomeSize()]);
                GenomeData childB = new GenomeData(new int[CellularAutomataEngine.getGenomeSize()]);
                
                //Crossover
                if(randomGen.nextDouble() <= crossoverRate) {
                    System.arraycopy(parentA.getGenome(), 0, childA.getGenome(), 0, crossoverPoint);
                    System.arraycopy(parentB.getGenome(), 0, childB.getGenome(), 0, crossoverPoint);
                    System.arraycopy(parentB.getGenome(), crossoverPoint, childA.getGenome(), crossoverPoint, crossoverPoint);
                    System.arraycopy(parentA.getGenome(), crossoverPoint, childB.getGenome(), crossoverPoint, crossoverPoint);
                }
                else {
                    //Important to not jsut use the parents here, the childern are supposed to become unique by mutation this way
                    System.arraycopy(parentA.getGenome(), 0, childA.getGenome(), 0, CellularAutomataEngine.getGenomeSize());
                    System.arraycopy(parentB.getGenome(), 0, childB.getGenome(), 0, CellularAutomataEngine.getGenomeSize());
                    childA.setFitness(parentA.getFitness());
                    childB.setFitness(parentB.getFitness());
                    childA.setLambda(parentA.getLambda());
                    childB.setLambda(parentB.getLambda());
                }
                
                //Mutation
                mutateGenome(childA);
                mutateGenome(childB);
                
                //Assuring that all genomes in the new population are unique
                if(genomeEqual(childA, childB)) continue;
                
                boolean found = false;
                for (int j = 0; j < i && !found; j++) 
                    if(genomeEqual(nextPopulation[j], childA) || genomeEqual(nextPopulation[j], childB)) found = true;
                
                if(found) continue;
                
                if(useLambdaToDiscard) {
                    getLambda(childA);
                    if(childA.getLambda() > lambdaUpperLimit || (float)lambdaLimitFromBest < (1.0f - Math.abs((childA.getLambda() - bestLambda)/((bestLambda > childA.getLambda()) ? bestLambda : childA.getLambda())))) continue;
                    
                    getLambda(childB);
                    if(childB.getLambda() > lambdaUpperLimit || (float)lambdaLimitFromBest < (1.0f - Math.abs((childB.getLambda() - bestLambda)/((bestLambda > childB.getLambda()) ? bestLambda : childB.getLambda())))) continue;
                }//useLambdaToDiscard
                
                nextPopulation[i++] = childA;
                nextPopulation[i++] = childB;
            }
            
            getLambda(best);
            bestLambda = best.getLambda();
            
            population = nextPopulation;
        }
        return best.getGenome();
    }
    
    

}
