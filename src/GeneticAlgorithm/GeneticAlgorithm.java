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
    
    private static float mutationRate = 0.05f; //5%
    
    private static int quiecentState = 0;

    private static float lambdaFitnessRatio = 20.0f;
    
    private static boolean TrajectoryAttractorLength = true; //true is trajectory length and false is attractor length
    
    private static boolean useLambdaInFitness = false;
    
    private static boolean useLambdaToDiscard = false;

    private static boolean useGenomeUsageInMutation = false;

    private static boolean useGDInFitness = false;
    
    private static float lambdaUpperLimit;
    private static float lambdaLimitFromBest = 0.1f;
    
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
    private GenomeData best = null;
    
    //Statistics variables
    private ArrayList<GenomeData> bestGenomePerGeneration = new ArrayList<GenomeData>();

   /* private HashMap<StateHashKey, Integer> similarTableCheck = new HashMap<StateHashKey, Integer>();
    private synchronized boolean genomeRepeated(int[] genome) {
        StateHashKey key = new StateHashKey(genome);
        Integer value = similarTableCheck.get(key);
        
        if(value == null) {
            value = 1;
            similarTableCheck.put(key, value);
            return false;
        }
        else {
            value++;
            similarTableCheck.put(key, value);
            return true;
        }
    }

    private ArrayList<Integer> repeatedGenomesPerGenerationCumulative = new ArrayList<Integer>();

    public ArrayList<Integer> getRepeatedGenomesPerGenerationCumulative() {
        return repeatedGenomesPerGenerationCumulative;
    }

    int noRepeats = 0;
    private synchronized void incrementRepeats() {
        noRepeats++;
    }*/

    /*private int lambdaCounter = 0;
    private double avgLambdaDifferecnce = 0;
    public double getAvgLambdaDifference() {
        return avgLambdaDifferecnce;
    }
    private int[] lambdaDifferenceCount = new int[100];

    public int[] getLambdaDifferenceCount() {
        return lambdaDifferenceCount;
    }*/

    //getters and setters for static variables. shouldnot be used normally, only to tune the algorithm
    public static boolean isTrajectoryAttractorLength() {return TrajectoryAttractorLength;}
    public static float getCrossoverRate() {return crossoverRate;}
    public static int getInitialPopulation() {return initialPopulation;}
    public static float getLambdaLimitFromBest() {return lambdaLimitFromBest;}
    public static float getLambdaUpperLimit() {return lambdaUpperLimit;}
    public static float getMutationRate() {return mutationRate;}
    public static float getLambdaFitnessRatio() {return lambdaFitnessRatio;}
    public static int getPopulationToPreserve() {return populationToPreserve;}
    public static int getQuiecentState() {return quiecentState;}
    public static boolean isUseLambdaInFitness() {return useLambdaInFitness;}
    public static boolean isUseLambdaToDiscard() {return useLambdaToDiscard;}
    public static boolean isUseGenomeUsageInMutation() { return useGenomeUsageInMutation; }
    public static boolean isUseGDInFitness() {return useGDInFitness;}
    public static boolean isUseRandomInitialPopulation() {return useRandomInitialPopulation;}

    //uncomment to enable changing of the properties of the genetic algorithm. Useful for tuning.
    public static void setTrajectoryAttractorLength(boolean TrajectoryAttractorLength) {GeneticAlgorithm.TrajectoryAttractorLength = TrajectoryAttractorLength;}
    public static void setCrossoverRate(float crossoverRate) {GeneticAlgorithm.crossoverRate = crossoverRate;}
    public static void setInitialPopulation(int initialPopulation) {GeneticAlgorithm.initialPopulation = initialPopulation;}
    public static void setLambdaLimitFromBest(float lambdaLimitFromBest) {GeneticAlgorithm.lambdaLimitFromBest = lambdaLimitFromBest;}
    public static void setLambdaUpperLimit(float lambdaUpperLimit) {GeneticAlgorithm.lambdaUpperLimit = lambdaUpperLimit;}
    public static void setMutationRate(float mutationRate) {GeneticAlgorithm.mutationRate = mutationRate;}
    public static void setLambdaFitnessRatio(float lambdaFitnessRatio) { GeneticAlgorithm.lambdaFitnessRatio = lambdaFitnessRatio;}
    public static void setPopulationToPreserve(int populationToPreserve) {GeneticAlgorithm.populationToPreserve = populationToPreserve;}
    public static void setQuiecentState(int quiecentState) {GeneticAlgorithm.quiecentState = quiecentState;}
    public static void setUseLambdaInFitness(boolean useLambdaInFitness) {GeneticAlgorithm.useLambdaInFitness = useLambdaInFitness;}
    public static void setUseLambdaToDiscard(boolean useLambdaToDiscard) {GeneticAlgorithm.useLambdaToDiscard = useLambdaToDiscard;}
    public static void setUseGenomeUsageInMutation(boolean useGenomeUsageInMutation) {GeneticAlgorithm.useGenomeUsageInMutation = useGenomeUsageInMutation;}
    public static void setUseGDInFitness(boolean useGDInFitness) {GeneticAlgorithm.useGDInFitness = useGDInFitness;}
    public static void setUseRandomInitialPopulation(boolean useRandomInitialPopulation) {GeneticAlgorithm.useRandomInitialPopulation = useRandomInitialPopulation;}


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
    //private static float lambdaInFitnessLimit = 0.05f;
    private static double gdRatio = 20.0;
    public class FitnessThread extends Thread {
        private GenomeData genome;
        HashMap<StateHashKey, Integer> searchTable = new HashMap<StateHashKey, Integer>();

        public FitnessThread(GenomeData genome) {
            this.genome = genome;
        }

        @Override
        public void run() {
            if(genome.hasFitness()) {
                genome.setAdjustedFitness((double)genome.getFitness());
                if(useLambdaInFitness && best != null) {
                    getLambda(genome);
                    //genome.setRelativeLambda(Math.abs(Math.abs(bestLambda - genome.getLambda() )- lambdaInFitnessLimit));
                    genome.setRelativeLambda(Math.abs(lambdaUpperLimit - genome.getLambda())/lambdaUpperLimit);
                    genome.setAdjustedFitness(genome.getAdjustedFitness() + (genome.getAdjustedFitness()*genome.getRelativeLambda() * lambdaFitnessRatio));
                    //genome.setAdjustedFitness(genome.getAdjustedFitness() + (targetFitness*genome.getRelativeLambda() * lambdaFitnessRatio));
                }
                if(useGDInFitness) {
                    genome.setAdjustedFitness(genome.getAdjustedFitness() + ((double)genome.getAdjustedFitness()*genome.getGDDifference()*gdRatio) );
            }
                return;
            }
            
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
                //if((trajectoryLength - hit) != 10) genome.setFitness(genome.getFitness() + (targetFitness/Math.abs((trajectoryLength - hit) - 10)));
                //if((trajectoryLength - hit) != 1) genome.setFitness(targetFitness);
                genome.setAdjustedFitness((double)genome.getFitness());
            }
            else {
                genome.setFitness(Math.abs(targetFitness - (trajectoryLength - hit) ));
                genome.setAdjustedFitness((double)genome.getFitness());
            }

            if(useGDInFitness) {
                int[] gddStats = ca.getGDDStats();
                double totalGDDStats = gddStats[0]+gddStats[1]+gddStats[2]+gddStats[3];
                //genome.setGDDifference(Math.abs((double)gddStats[ca.DEATH]/totalGDDStats - (double)gddStats[ca.GROWTH]/totalGDDStats));
                double death = gddStats[ca.DEATH]/totalGDDStats, growth = gddStats[ca.GROWTH]/totalGDDStats, diff = gddStats[ca.DIFF]/totalGDDStats;
                double avgGDDStats = (Math.abs(death-0.222)+Math.abs(growth-0.222)+Math.abs(diff-0.222))/3.0;
                genome.setGDDifference(avgGDDStats);
                genome.setAdjustedFitness(genome.getAdjustedFitness() + ((double)genome.getAdjustedFitness()*genome.getGDDifference()*gdRatio) );
            }

            if(useLambdaInFitness && best != null) {
                getLambda(genome);
                //genome.setRelativeLambda(Math.abs(Math.abs(bestLambda - genome.getLambda() )- lambdaInFitnessLimit));
                genome.setRelativeLambda(Math.abs(lambdaUpperLimit - genome.getLambda())/lambdaUpperLimit);
                genome.setAdjustedFitness(genome.getAdjustedFitness() + (genome.getAdjustedFitness()*genome.getRelativeLambda() * lambdaFitnessRatio));
                //genome.setAdjustedFitness(genome.getAdjustedFitness() + (targetFitness*genome.getRelativeLambda() * lambdaFitnessRatio));
            }
            genome.setGenomeUsageStatistics(ca.getGenomeUsageStatistics());
            int usedGenes = 0;
            for(int i : genome.getGenomeUsageStatistics()) if(i != 0) usedGenes++;
            genome.setGenomeUsageFactor((float)usedGenes/(float)genome.getGenomeUsageStatistics().length);
            // check if genomes are repeated if(genomeRepeated(genome.getGenome())) incrementRepeats();
        }
    }

    public void getLambda(GenomeData genome) {
        if(genome.getLambda() != -1) return;
        //else getTransitionParameter(genome);
        
        int n = 0;
        for(int i : genome.getGenome())
        {
            if(i == quiecentState) n++;
        }
        genome.setLambda((float)(genome.getGenome().length - n)/(float)(genome.getGenome().length));
    }

    public void getTransitionParameter(GenomeData genome) {
        int n = 0;
        for(int i = 0; i < genome.getGenome().length;i++) {
            int center = genome.getCenterCellAtInd(i);
            int result = genome.getGenome()[i];
            if(center == 0 && result == 1 ) n++;
        }
        genome.setLambda((float)(genome.getGenome().length - n)/(float)(genome.getGenome().length));
    }

    public void getDeathParameter(GenomeData genome) {
        int n = 0;
        for(int i = 0; i < genome.getGenome().length;i++) {
            int center = genome.getCenterCellAtInd(i);
            int result = genome.getGenome()[i];
            if(center != 0 && result == 0 ) n++;
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
                    if(a.getAdjustedFitness() > b.getAdjustedFitness())
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

    /*
        Mutate genome where each value in the genome has the probability found in mutate(L) to increment by one.
    */
    void mutateGenomeWithGenomeUsage(GenomeData mutatingGenome) {
        float adjMutationRate = 1.0f - mutatingGenome.getGenomeUsageFactor();
        if(adjMutationRate < 0.00001f) return;
        for(int j = 0; j < mutatingGenome.getGenome().length;j++) {
            if(mutatingGenome.getGenomeUsageStatistics()[j] != 0 && randomGen.nextDouble() <= adjMutationRate) {
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
        // lambdaCounter = 0;
        //avgLambdaDifferecnce = 0;
        /*System.out.println("mutation rate: "+mutationRate);
            System.out.println("lambda to discard: "+useLambdaToDiscard);
            System.out.println("lambda in fitness: "+useLambdaInFitness);
            System.out.println("lambdalimit from best: "+lambdaLimitFromBest);*/
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
            sortPopulationByFitness(true);
            /*for (int i = 0; i < 5; i++) {
                GenomeData g = population[i];
                System.out.println(i+": fitness: "+g.getFitness()+" adj fitness: "+ g.getAdjustedFitness()+" lambda: "+g.getLambda());
            }*/
            //Find adjusted fitness for roulette wheel
            double worstFitness = population[initialPopulation-1].getAdjustedFitness();
            double totalFitness = 0;

            for(int i = 0; i < initialPopulation;i++) {
                GenomeData g = population[i];
                g.setAdjustedFitness(worstFitness - g.getAdjustedFitness());
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

            //if(best.getFitness() > population[0].getFitness())
             //   System.out.println("generation: "+ generation + " complexity: "+population[0].getFitness());
            
            best = population[0];
            
            //keep statistics, using the original object, so it is important to remember that new GenomeData objects need to be created for new genotypes
            bestGenomePerGeneration.add(best);
            //repeatedGenomesPerGenerationCumulative.add(noRepeats);
            
            int i;
            for(i = 0; i < populationToPreserve; i++) {
                nextPopulation[i] = population[i];
            }

            int max = 100000;
            int eternalLoopCheck = 0;
            while(i < initialPopulation ) {//&& max != (eternalLoopCheck++)
                eternalLoopCheck++;
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
                    if(useGenomeUsageInMutation) {
                        childA.setGenomeUsageFactor(parentA.getGenomeUsageFactor());
                        childB.setGenomeUsageFactor(parentB.getGenomeUsageFactor());
                        childA.setGenomeUsageStatistics(new int[CellularAutomataEngine.getGenomeSize()]);
                        childB.setGenomeUsageStatistics(new int[CellularAutomataEngine.getGenomeSize()]);
                        System.arraycopy(parentA.getGenomeUsageStatistics(), 0, childA.getGenomeUsageStatistics(), 0, CellularAutomataEngine.getGenomeSize());
                        System.arraycopy(parentB.getGenomeUsageStatistics(), 0, childB.getGenomeUsageStatistics(), 0, CellularAutomataEngine.getGenomeSize());
                        mutateGenomeWithGenomeUsage(childA);
                        mutateGenomeWithGenomeUsage(childB);
                    }
                    if(useGDInFitness) {
                        childA.setGDDifference(parentA.getGDDifference());
                        childB.setGDDifference(parentB.getGDDifference());
                    }
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
                /*getLambda(childA);
                getLambda(childB);
                double aDiff = Math.abs(childA.getLambda() - bestLambda);
                double bDiff = Math.abs(childB.getLambda() - bestLambda);
                avgLambdaDifferecnce += aDiff;
                avgLambdaDifferecnce += bDiff;
                lambdaCounter += 2;

                lambdaDifferenceCount[((int)(aDiff*100.0))] += 1;
                lambdaDifferenceCount[((int)(bDiff*100.0))] += 1;*/

                if(useLambdaToDiscard && eternalLoopCheck <= max) {
                    getLambda(childA);
                    if(childA.getLambda() > lambdaUpperLimit || lambdaLimitFromBest < Math.abs(childA.getLambda() - bestLambda)) continue;
                    
                    getLambda(childB);
                    if(childB.getLambda() > lambdaUpperLimit || lambdaLimitFromBest < Math.abs(childA.getLambda() - bestLambda)) continue;
                }//useLambdaToDiscard
                
                nextPopulation[i++] = childA;
                nextPopulation[i++] = childB;
                eternalLoopCheck = 0;
            }

            /*if(max <= eternalLoopCheck) { //sneaking inn to easily detect infinite loops in the graphs
                for(i = generation; i < maxGenerations;i++) {
                    bestGenomePerGeneration.add(best);
                }
                return best.getGenome();
            }*/
            
            getLambda(best);
            bestLambda = best.getLambda();
            
            population = nextPopulation;
        }

       // avgLambdaDifferecnce /= (float)lambdaCounter;
        return best.getGenome();
    }
    
    

}

/*
 * if(useLambdaInFitness) {
                /*double worstLambda = 0;
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
                }*

                //alternative lambda calc
                double totalLambda = 0;
                for(int i = 0; i < initialPopulation;i++) {
                    GenomeData g = population[i];
                    getLambda(g);
                    g.setRelativeLambda((double)g.getLambda());
                    totalLambda += g.getRelativeLambda();
                }
                //-----------------------

                for(int i = 0; i < initialPopulation;i++)
                {
                    GenomeData g = population[i];
                    g.setRelativeLambda(g.getRelativeLambda() / totalLambda);
                }
                for(int i = 0; i < initialPopulation;i++)
                {
                    GenomeData g = population[i];
                    g.setAdjustedFitness( ( complexityRatio * g.getAdjustedFitness())+(lambdaFitnessRatio * g.getRelativeLambda()));
                }
                //needs to sort the population again because lambda might change which genome is the most preferable
                sortPopulationByFitness(true);
            }//useLambdaInFitness
 */
