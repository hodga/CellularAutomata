/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GeneticAlgorithm;

/**
 *
 * @author Hodga
 */
public class GenomeData {
    private int[] genome;

    private float lambda = -1;
    private double relativeLambda;

    private int fitness = -1;
    private double adjustedFitness;

    private int[] genomeUsageStatistics;
    private float genomeUsageFactor;

    public GenomeData(int[] genome) {
        this.genome = genome;
    }

    public double getAdjustedFitness() {
        return adjustedFitness;
    }

    public boolean hasLambda() {
        return lambda != -1;
    }

    public void setAdjustedFitness(double adjustedFitness) {
        this.adjustedFitness = adjustedFitness;
    }

    public boolean hasFitness() {
        return fitness != -1;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public int[] getGenome() {
        return genome;
    }

    public float getGenomeUsageFactor() {
        return genomeUsageFactor;
    }

    public void setGenomeUsageFactor(float genomeUsageFactor) {
        this.genomeUsageFactor = genomeUsageFactor;
    }

    public int[] getGenomeUsageStatistics() {
        return genomeUsageStatistics;
    }

    public void setGenomeUsageStatistics(int[] genomeUsageStatistics) {
        this.genomeUsageStatistics = genomeUsageStatistics;
    }

    public float getLambda() {
        return lambda;
    }

    public void setLambda(float lambda) {
        this.lambda = lambda;
    }

    public double getRelativeLambda() {
        return relativeLambda;
    }

    public void setRelativeLambda(double relativeLambda) {
        this.relativeLambda = relativeLambda;
    }
}
