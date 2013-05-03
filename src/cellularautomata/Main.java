/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cellularautomata;

import GeneticAlgorithm.GeneticAlgorithm;
import java.io.File;

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
        int maxGenerations = 100000;

        int startRun = 0 ;
        int endRun = 20;
        int[]complexities = { 25000 };
        String filePath = "D:\\Master_Eksperimenter\\LambdaInFitnessLowMutationRate\\";//manyPlotGDDAndFitness10000_usage.gatest";

        //lambda to discard--------------------------------------
        //GeneticAlgorithm.setUseLambdaToDiscard(true);

        //lambda in fitness--------------------------------------
        GeneticAlgorithm.setUseLambdaInFitness(true);
        GeneticAlgorithm.setLambdaFitnessRatio(20.0f);

        //genome usage-------------------------------------------
        //GeneticAlgorithm.setUseGenomeUsageInMutation(true);

        //growth death difference--------------------------------
        //GeneticAlgorithm.setUseGDInFitness(true);

        //other parameters for ga---------------------------------
        //GeneticAlgorithm.setUseRandomInitialPopulation(true);
        GeneticAlgorithm.setLambdaUpperLimit((1.0f - 1.0f/3.0f));
        //GeneticAlgorithm.setLambdaUpperLimit((1.0f - 1.0f/9.0f));
        GeneticAlgorithm.setMutationRate(0.02f);

        //run ga----------------------------------------------------
        ExperimentMain.ComplexitySearch(dim, neighborhoodSize, nbStates, startRun, endRun, maxGenerations, filePath, complexities);

        //run ga multiple
        /*float[] values = { 30.0f, 50.0f};
        for (int i = 0; i < values.length; i++) {
            GeneticAlgorithm.setLambdaFitnessRatio(values[i]);
            String dirName = String.valueOf(values[i]);
            if(GeneticAlgorithm.isUseRandomInitialPopulation()) dirName = "rand_"+dirName;
            System.out.println("value run: "+dirName);

            dirName = filePath+dirName+"\\";
            File theDir = new File(dirName);
            if (!theDir.exists())theDir.mkdir();

            ExperimentMain.ComplexitySearch(dim, neighborhoodSize, nbStates, startRun, endRun, maxGenerations, dirName, complexities);
            float avg = AvgGenerationGenomeFound.FindAverage(complexities[0], endRun, dirName);
            System.out.println(" average: "+avg);

            if(GeneticAlgorithm.isUseRandomInitialPopulation() && i == values.length-1 ) {
                GeneticAlgorithm.setUseRandomInitialPopulation(false);
                i = -1;
            }
        }*/

        //String filePath2 = "D:\\Master_Eksperimenter\\genomeStats\\manyPlotTransitionStatsAndFitness10000_2.gatest";

        //get stats on growth differentiation and death

        //String genomeString = "020200012101120101111201111210220211001221001102011200010210100022001102110121101102220221211020010212010012002102010020120211021011211012201012202000020220201021010201110021121101102212222200022012122112110101010100110202201010011022000202120";
        //String[] fileFolders = { "D:\\Master_Eksperimenter\\12LambdaInFitness\\REF_RAND_INIT\\"};
        //ArrayList<String> genomes = GenomeStats.findGenomes(fileFolders, 25000, 10);
        //more advanced search on several genomes
        //GenomeStats.patternSearchGDDStats(genomes, 25000, dim, neighborhoodSize, nbStates);
        //create plotable data of one genome run
        //GenomeStats.runGenomeStats(dim, neighborhoodSize, nbStates, genomeString, 250000, filePath, 55);
        //now using actually measured gdd, creates many random genomes and measures fitness and usage/parameters
        //GenomeStats.createGDDRStats(filePath, filePath2, 10000, dim, neighborhoodSize, nbStates);
        
        //find fintess
        /*ExperimentMain.configureCA(dim, neighborhoodSize, nbStates);
        GeneticAlgorithm ga = new GeneticAlgorithm(0, 100000);
        int[] genome = GenomeStats.convertGenomeString(genomeString);
        GenomeData gd= new GenomeData(genome);
        ga.testFitness(gd);
        System.out.println("fitness: "+gd.getFitness());*/

        //test genome
        //ExperimentMain.configureCA(dim, neighborhoodSize, nbStates);
        //int[] genome = GenomeStats.convertGenomeString(genomeString);
        //System.out.println("valid? "+GeneticAlgorithmTest.validateGenomeTrajectoryLength(genome, 120));


        //averages
        /*String[] filePaths = {filePath, "D:\\Master_Eksperimenter\\first_check_lambda_values_and_genome_usage\\lambda_to_discard",
        "D:\\Master_Eksperimenter\\first_check_lambda_values_and_genome_usage\\lambda_in_fitness"};
        String[] separators = {"   ", "  ", "  "};
        String writePath = "D:\\Master_Eksperimenter\\DifferenceGraphsGenomeUsage_LambdaAndGenomeUsage_Complexity\\normal\\";
        Correlations.calculateCorrelations(filePaths, writePath, complexities, endRun, separators, 1, new int[]{0, 2});*/
        
        //find average
        /*float avg = AvgGenerationGenomeFound.FindAverage(1000, 1000, filePath);
        System.out.println("average: "+avg);*/

        //combine columns of different files
        /*String folderPalth = "lambdaInFitnessHighestLambda";
        String folder = "rand_0.0010";
        String[] files = {
            "D:\\Master_Eksperimenter\\"+folderPalth+"\\"+folder+"\\Avg_stdDev_complexity_1000_runs_1000.gatest",
            "D:\\Master_Eksperimenter\\"+folderPalth+"\\"+folder+"\\Avg_stdDev_lambda_1000_runs_1000.gatest",
            "D:\\Master_Eksperimenter\\"+folderPalth+"\\"+folder+"\\Avg_stdDev_genome_usage_1000_runs_1000.gatest",
        };
        int[] cols = {0, 0, 0};
        String output = "D:\\Master_Eksperimenter\\"+folderPalth+"\\"+folder+"\\Avg_all_1000_runs_1000.gatest";
        spliceFiles.spliceColumns(files, cols, output);*/
    }

    /* BufferedWriter bw = new BufferedWriter(new FileWriter(filePath+"plotAvgAllComplexity15000script.plt"));

       if(bw != null) {
            String s = "clear\n"+
                       "reset\n"+
                       "set term png size 1280, 720\n"+
                       "set output \"Avg_stdDev_complexity_15000_runs_20.png\"\n"+
                       "set style fill transparent pattern 4 bo\n"+
                       "plot    ";
            bw.write(s);

            for(int i = 1; i < lambdaLimits.length;i++) {
                //float mutationRate = 0.25f * (float)i/mutationRateChecks;
                bw.write("\""+lambdaLimits[i]+"\\\\Avg_stdDev_complexity_15000_runs_20.gatest\" using 1:2 with lines linewidth 2 title \" mutation rate: "+lambdaLimits[i]+"\""+((i != lambdaLimits.length-1) ? ", \\" : "")+"\n");
            }
                s = "set term windows\n"+
                    "set output\n";
            bw.write(s);
            bw.close();
        }*/

}
