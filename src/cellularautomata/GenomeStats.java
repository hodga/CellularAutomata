/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cellularautomata;

import GeneticAlgorithm.GeneticAlgorithm;
import GeneticAlgorithm.GenomeData;
import engine.CellularAutomataEngine;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Hodga
 */
public class GenomeStats {

    public static void runGenomeStats(int[] dim, int neighborhoodSize, int nbStates, String genomeString, int nbSteps, String outputPath, int nb) throws Exception{
        //initialize CA
        if(!ExperimentMain.configureCA(dim, neighborhoodSize, nbStates)) return;

        int[] genome = new int[CellularAutomataEngine.getGenomeSize()];
        char zeroRef = '0';
        for (int i= 0; i < genome.length; i++) {
            genome[i] = genomeString.charAt(i) - zeroRef;
        }
        runGenomeStats(genome, nbSteps, outputPath, nb);
    }

    public static int[] convertGenomeString(String genomeString) {
        int[] genome = new int[CellularAutomataEngine.getGenomeSize()];
        char zeroRef = '0';
        for (int i= 0; i < genome.length; i++) {
            genome[i] = genomeString.charAt(i) - zeroRef;
        }
        return genome;
    }

    public static void runGenomeStats(int[] genome, int nbSteps, String outputPath, int nb) throws Exception {
        if(genome.length != CellularAutomataEngine.getGenomeSize()) return;
        for (int i= 0; i < genome.length; i++) {
            if(genome[i] < 0 || genome[i] >= CellularAutomataEngine.getNbStates()) {
                System.out.println("unvalid genome");
                return;
            }
        }

        if(!outputPath.endsWith("\\")) outputPath+= "\\";
        File testDir = new File(outputPath);
        if (!testDir.exists()) {
            System.out.println("output path do not exsist");
            return;
        }

        CellularAutomataEngine ca = new CellularAutomataEngine(genome);

        ArrayList<int[]> allGDDStats = new ArrayList<int[]>();
        ArrayList<int[]> allGDDStatsCumulative = new ArrayList<int[]>();
        for (int i= 0; i < nbSteps; i++) {
            ca.step();
            int[] gddStats = ca.getGDDStats();

            int[] myGDDStats = Arrays.copyOf(gddStats, gddStats.length);
            allGDDStats.add(myGDDStats);

            int[] myCumulativeGDDStats = new int[gddStats.length];
            if(i  == 0) {
               for (int j= 0; j < gddStats.length; j++) {
                    myCumulativeGDDStats[j] = myGDDStats[j];
                    gddStats[j] = 0;
                }
            }
            else {
                for (int j= 0; j < gddStats.length; j++) {
                    myCumulativeGDDStats[j] = myGDDStats[j] + allGDDStatsCumulative.get(i-1)[j];
                    gddStats[j] = 0;
                }
            }
            allGDDStatsCumulative.add(myCumulativeGDDStats);
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath+"gddstats_steps_"+nbSteps+"_nb_"+nb+".genomeStats"));

        if(bw != null) {
            String s = "#Genome: ";
            for(int g : genome) s += g;
            s+= "\n";
            bw.write(s);
            bw.write("#step\tgrowth\tdifferentiation\tdeath\tnone\n");

            for(int k = 0; k < allGDDStats.size();k++)
                bw.write(k+"\t"+allGDDStats.get(k)[CellularAutomataEngine.GROWTH]+"\t"+allGDDStats.get(k)[CellularAutomataEngine.DIFF]+"\t"+allGDDStats.get(k)[CellularAutomataEngine.DEATH]+"\t"+allGDDStats.get(k)[CellularAutomataEngine.NONE]+"\n");

        }
        else System.out.println("could not write gddStats file");
        bw.close();

        bw = new BufferedWriter(new FileWriter(outputPath+"Cumulative_gddstats_steps_"+nbSteps+"_nb_"+nb+".genomeStats"));

        if(bw != null) {
            String s = "#Genome: ";
            for(int g : genome) s += g;
            s+= "\n";
            bw.write(s);
            bw.write("#step\tgrowth\tdifferentiation\tdeath\tnone\n");

            for(int k = 0; k < allGDDStatsCumulative.size();k++)
                bw.write(k+"\t"+allGDDStatsCumulative.get(k)[CellularAutomataEngine.GROWTH]+"\t"+allGDDStatsCumulative.get(k)[CellularAutomataEngine.DIFF]+"\t"+allGDDStatsCumulative.get(k)[CellularAutomataEngine.DEATH]+"\t"+allGDDStatsCumulative.get(k)[CellularAutomataEngine.NONE]+"\n");

        }
        else System.out.println("could not write cumulative gddStats file");
        bw.close();

    }

    public static void patternSearchGDDStats(ArrayList<String> genomes, int nbSteps, int[] dim, int neighborhoodSize, int nbStates) {
        if(!ExperimentMain.configureCA(dim, neighborhoodSize, nbStates)) return;

        for(String genomeString : genomes) {
            int[] genome = new int[CellularAutomataEngine.getGenomeSize()];
            char zeroRef = '0';
            for (int i= 0; i < genome.length; i++) {
                genome[i] = genomeString.charAt(i) - zeroRef;
            }

            if(genome.length != CellularAutomataEngine.getGenomeSize()) return;
            for (int i= 0; i < genome.length; i++) {
                if(genome[i] < 0 || genome[i] >= CellularAutomataEngine.getNbStates()) {
                    System.out.println("unvalid genome");
                    return;
                }
            }

            CellularAutomataEngine ca = new CellularAutomataEngine(genome);

            //Slope for cumulative stats (all are linear it seems, lets see if the slope has any similarities
            //double[] averageSlope = new double[4];
            double[] totalSlope = new double[4];
            double[] genomeGDDRatio = new double[4];
            double[] slopeNormalized = new double[4];

            //allCorrelations between growth, differentiation, death, and no change genome rules.
            //double[] correlations = new double[4*3];

            ArrayList<int[]> allGDDStats = new ArrayList<int[]>();
            ArrayList<int[]> allGDDStatsCumulative = new ArrayList<int[]>();
            for (int i= 0; i < nbSteps; i++) {
                ca.step();
                int[] gddStats = ca.getGDDStats();

                int[] myGDDStats = Arrays.copyOf(gddStats, gddStats.length);
                allGDDStats.add(myGDDStats);

                int[] myCumulativeGDDStats = new int[gddStats.length];
                if(i  == 0) {
                   for (int j= 0; j < gddStats.length; j++) {
                        myCumulativeGDDStats[j] = myGDDStats[j];
                        gddStats[j] = 0;
                    }
                }
                else {
                    for (int j= 0; j < gddStats.length; j++) {
                        myCumulativeGDDStats[j] = myGDDStats[j] + allGDDStatsCumulative.get(i-1)[j];
                        gddStats[j] = 0;
                    }
                }
                allGDDStatsCumulative.add(myCumulativeGDDStats);
            }

            //time to find useful patterns... sukk
            for (int i= 0; i < totalSlope.length; i++) {
                totalSlope[i] = (double)allGDDStatsCumulative.get(allGDDStatsCumulative.size()-1)[i]/allGDDStatsCumulative.size();
            }

            /*for (int i= 0; i < totalSlope.length; i++) {
                System.out.println("totalSlope "+i+": "+totalSlope[i]);
            }*/
            System.out.println("-----------------------------------------------");
            //System.out.println("totalSlope "+0+": "+totalSlope[0]);

            for (int i= 0; i < genome.length; i++) {
                int center = getCenterCellAtInd(i);

                if(genome[i] == center) genomeGDDRatio[CellularAutomataEngine.NONE] += 1;
                else if(center == 0 && genome[i] != 0) genomeGDDRatio[CellularAutomataEngine.GROWTH] += 1; //GROWTH
                else if(center != 0) {
                    if(genome[i] != 0) genomeGDDRatio[CellularAutomataEngine.DIFF] += 1; //DIFF
                    else genomeGDDRatio[CellularAutomataEngine.DEATH] += 1; //DEATH
                }
            }

            double slopeTotal = 0;
            for (int i= 0; i < totalSlope.length; i++) {
                slopeTotal += totalSlope[i];
            }
            for (int i= 0; i < totalSlope.length; i++) {
                slopeNormalized[i] = totalSlope[i]/slopeTotal;
            }

            for (int i= 0; i < genomeGDDRatio.length; i++) {
                genomeGDDRatio[i] /= (double)CellularAutomataEngine.getGenomeSize();
            }

            for (int i= 0; i < genomeGDDRatio.length; i++) {
                //System.out.println("i: "+i+" normalizedSlope: "+slopeNormalized[i]+" genomeGDDRRatio: "+genomeGDDRatio[i]);
                System.out.println("i: "+i+" normalizedSlope - genomeGDDRRatio "+(slopeNormalized[i]-genomeGDDRatio[i]));
            }
            //same result as total slope
            /*for (int i = 0; i < averageSlope.length; i++) {
                for (int j = 1; j < allGDDStatsCumulative.size(); j++) {
                    averageSlope[i] += (double)(allGDDStatsCumulative.get(j)[i] - allGDDStatsCumulative.get(j-1)[i]);
                }
                averageSlope[i] /= (double)(allGDDStatsCumulative.size()-1);
            }

            for (int i= 0; i < averageSlope.length; i++) {
                System.out.println("averageSlope "+i+": "+averageSlope[i]);
            }*/


//no correlations at all
           /* ArrayList<Float>[] stats = new ArrayList[4];
            for (int i= 0; i < 4; i++) {
                stats[i] = new ArrayList<Float>();
                for (int j= 0; j < allGDDStatsCumulative.size(); j++) {
                    stats[i].add((float)allGDDStats.get(j)[i]);
                }
            }

            int ctr = 0;
            for (int i= 0; i < stats.length; i++) {
                for (int j= 0; j < stats.length; j++) {
                    if(i == j) continue;
                    correlations[ctr++] = Correlations.getPearsonCorrelation(stats[i], stats[j]);
                }
            }

            for (int i= 0; i < correlations.length; i++) {
                System.out.println("correlations "+i+": "+correlations[i]);
            }*/
        }

    }

    public static ArrayList<String> findGenomes(String[] fileFolders, int complexity, int runs) throws Exception {
        ArrayList<String> genomes = new ArrayList<String>();
        for (int i= 0; i < fileFolders.length; i++) {
            for (int j= 0; j < runs; j++) {
                String name = fileFolders[i];
                name += "Complexity_"+complexity+"_RunNo_"+j+".gatest";
                BufferedReader br  =new BufferedReader(new FileReader(name));
                String genome = br.readLine().substring(9);
                
                boolean ok = true;
                for (int k= 0; k < genomes.size() && ok; k++) {
                    if(genomes.get(k).equals(genome)) ok = false;
                }
                if(ok)genomes.add(genome);
            }
        }
        return genomes;
    }

    public static int getCenterCellAtInd(int ind) {
        int nb = 0;
        for(int i = 0; i <= (5/2);i++) {
            nb = ind % 3;
            ind = ind/3;
        }
        return nb;
    }

    public static void createGDDRStats(String outputFile, String outputFile2, int nbSamples, int[] dim, int neighborhoodSize, int nbStates) throws Exception{
        if(!ExperimentMain.configureCA(dim, neighborhoodSize, nbStates)) return;

        GeneticAlgorithm ga = new GeneticAlgorithm(0, 100000);

        ArrayList<Integer> fitnesses = new ArrayList<Integer>();
        ArrayList<double[]> gddStats = new ArrayList<double[]>();
        ArrayList<Double> lambdas = new ArrayList<Double>();
        ArrayList<double[]> transitionStats = new ArrayList<double[]>();
        double[] averageDifference = new double[4];
        Random randomGen = new Random();
        for (int i = 0; i < nbSamples; i++) {
            int[] genome = new int[CellularAutomataEngine.getGenomeSize()];
            for (int j = 0; j < genome.length; j++) {
                genome[j] = randomGen.nextInt(CellularAutomataEngine.getNbStates());
            }
            GenomeData gd = new GenomeData(genome);
            ga.testFitness(gd);
            ga.getLambda(gd);

            lambdas.add((double)gd.getLambda());
            fitnesses.add(gd.getFitness());

            double[] genomeGDDRatio = new double[4];
            double[] genomeTransitionStats = new double[nbStates*nbStates];

            for (int j= 0; j < genome.length; j++) {
                int center = getCenterCellAtInd(j);

                if(genome[j] == center) genomeGDDRatio[CellularAutomataEngine.NONE] += 1; //NONE
                else if(center == 0 && genome[j] != 0) genomeGDDRatio[CellularAutomataEngine.GROWTH] += 1; //GROWTH
                else if(center != 0) {
                    if(genome[j] != 0) genomeGDDRatio[CellularAutomataEngine.DIFF] += 1; //DIFF
                    else genomeGDDRatio[CellularAutomataEngine.DEATH] += 1; //DEATH
                }

                genomeTransitionStats[center+nbStates*genome[j]] += 1;
            }

            CellularAutomataEngine ca = new CellularAutomataEngine(genome);

            double[] slope = new double[4];
            for (int h= 0; h < gd.getFitness(); h++) {
                ca.step();
            }
            int[] gddStatsSingle = ca.getGDDStats();
            slope[0] = gddStatsSingle[0];
                slope[1] = gddStatsSingle[1];
                slope[2] = gddStatsSingle[2];
                slope[3] = gddStatsSingle[3];

            double totalSlope = 0;
            for (int j= 0; j < slope.length; j++) {
                slope[j] /= (double)gd.getFitness();
                totalSlope += slope[j];
            }

            for (int j= 0; j < slope.length; j++) {
                slope[j] /= totalSlope;
            }

            for (int j= 0; j < genomeGDDRatio.length; j++) {
                genomeGDDRatio[j] /= (double)CellularAutomataEngine.getGenomeSize();
                averageDifference[j] += Math.abs(genomeGDDRatio[j]-slope[j]);
            }

            for (int j= 0; j < genomeTransitionStats.length; j++) {
                genomeTransitionStats[j] /= (double)CellularAutomataEngine.getGenomeSize();
            }

            gddStats.add(slope);
            transitionStats.add(genomeTransitionStats);
        }

        for (int i= 0; i < averageDifference.length; i++) {
            averageDifference[i] /= (double)nbSamples;
            System.out.println(i+": averageDifference: "+averageDifference[i]);
        }
           BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

            if(bw != null) {

                bw.write("#fitness\tgrowth\tdifferentiation\tdeath\tnone\tlambda\n");

                for(int k = 0; k < fitnesses.size();k++)
                    bw.write(fitnesses.get(k)+"\t"+gddStats.get(k)[CellularAutomataEngine.GROWTH]+"\t"+gddStats.get(k)[CellularAutomataEngine.DIFF]+"\t"+gddStats.get(k)[CellularAutomataEngine.DEATH]+"\t"+gddStats.get(k)[CellularAutomataEngine.NONE]+"\t"+lambdas.get(k)+"\n");

            }
            else System.out.println("could not write gddStats file");
            bw.close();

            bw = new BufferedWriter(new FileWriter(outputFile2));

            if(bw != null) {

                bw.write("#fitness\t0to0\t0to1\t0to2\t1to0\t1to1\t1to2\t2to0\t2to1\t2to2\n");

                for(int k = 0; k < fitnesses.size();k++) {
                    String str = ""+fitnesses.get(k);
                    for (int i = 0; i < nbStates*nbStates; i++) {
                        str+= "\t"+ transitionStats.get(k)[i];
                    }
                    str+= "\n";
                    bw.write(str);
                }

            }
            else System.out.println("could not write gddStats file");
            bw.close();
    }

}
