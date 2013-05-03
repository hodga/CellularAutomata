/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cellularautomata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 *
 * @author Hodga
 */
public class GraphAvg {

    /**
     * @param args the command line arguments
     */
    public static void run(String absPath, int chkComplexity, int runs) throws Exception{
        
      //String absPath = "D:\\Master_Eksperimenter\\first_check_lambda_values_and_genome_usage\\lambda_to_discard\\";
      //int runs = 20;
      //int[] complexities = {10, 1000, 5000, 10000, 15000, 20000, 25000, 100000000};
      //int[] complexities = {1000, 10000, 20000, 25000};
      //int[] complexities = {1000,5000, 10000, 15000, 20000, 25000};
      int[] complexities = { chkComplexity };
      
      ArrayList<int[]> stats = new ArrayList<int[]>();
      ArrayList<String> names = new ArrayList<String>();
      for(int i = 0; i < complexities.length;i++) {
          
        ArrayList<int[]> allRuns = new ArrayList<int[]>();
        ArrayList<float[]> allRunsLambda = new ArrayList<float[]>();
        ArrayList<float[]> allRunsUsage = new ArrayList<float[]>();
          System.out.println("reading files for complexity: "+complexities[i]);
        for(int j = 0; j < runs; j++) {
            BufferedReader br = new BufferedReader(new FileReader(absPath+"Complexity_"+complexities[i]+"_RunNo_"+j+".gatest"));
            String s = br.readLine();
            int ctr = 0;
            while(s != null && !s.isEmpty()) {
                if(s.charAt(0) == '#') {
                    s = br.readLine();
                    continue;
                }
                if(allRuns.size() <= ctr) allRuns.add(new int[runs]);
                if(allRunsLambda.size() <= ctr) {
                    float[] thisRun = new float[runs];
                    for(int k = 0; k < runs;k++) thisRun[k] = -1;
                    allRunsLambda.add(thisRun);
                }
                if(allRunsUsage.size() <= ctr) {
                    float[] thisRun = new float[runs];
                    for(int k = 0; k < runs;k++) thisRun[k] = -1;
                    allRunsUsage.add(thisRun);
                }
                
                int startInd = s.indexOf("   ")+3;
                int endInd = s.indexOf("  ", startInd+1);
                int complexity;
                if(endInd != -1) complexity = Integer.parseInt(s.substring(startInd, endInd));
                else complexity = Integer.parseInt(s.substring(startInd));
                allRuns.get(ctr++)[j] = complexity;
                
                //adding lambda and usage rate of genome to a list
                if(endInd != -1) {
                    startInd = endInd+2;
                    endInd = s.indexOf("  ", startInd+1);
                    float lambda = Float.parseFloat(s.substring(startInd, endInd));
                    allRunsLambda.get(ctr-1)[j] = lambda;
                    
                    startInd = endInd+2;
                    float usage = Float.parseFloat(s.substring(startInd));
                    allRunsUsage.get(ctr-1)[j] = usage;
                    
                }
                
                s = br.readLine();
            }
            br.close();
        }
        
        //adjust all values for lambda and usage up to the lsat lambda value instead of 0
        float[] lastRun = allRunsLambda.get(0);
        for(int j = 1; j < allRunsLambda.size();j++) {
            float[] thisRun = allRunsLambda.get(j);
            for(int k = 0; k < thisRun.length;k++) {
                if(thisRun[k] == -1) thisRun[k] = lastRun[k];
            }
            lastRun = thisRun;
        }
        lastRun = allRunsUsage.get(0);
        for(int j = 1; j < allRunsUsage.size();j++) {
            float[] thisRun = allRunsUsage.get(j);
            for(int k = 0; k < thisRun.length;k++) {
                if(thisRun[k] == -1) thisRun[k] = lastRun[k];
            }
            lastRun = thisRun;
        }
            
          int firstFinished = 100000;
          int lastFinished = 100000;
          int numberFinished = 0;
          int longestUnchanged = 0;
          
          int[] unchanged = new int[runs];
          int[] lastFitness= new int[runs];
          for (int j = 0; j < runs; j++) lastFitness[j] = -1;
          for (int j = 0; j < allRuns.size(); j++) {
              int[] fitnesses = allRuns.get(j);
              for (int k = 0; k < runs; k++) {
                  int fitness = fitnesses[k];
                  
                  if(lastFitness[k] == 0) continue;
                  
                  if(fitness != lastFitness[k]) {
                      if(unchanged[k] > longestUnchanged) longestUnchanged = unchanged[k];
                      unchanged[k] = 1;
                      lastFitness[k] = fitness;
                      
                      if(fitness == 0) {
                          if(numberFinished == 0) {
                              firstFinished = j;
                          }
                          else if(numberFinished == runs-1) {
                              lastFinished = j;
                          }
                          numberFinished++;
                      }
                  }
                  else {
                      unchanged[k]++;
                  }
              }
          }
          stats.add(new int[]{firstFinished, lastFinished, numberFinished, longestUnchanged});
          names.add("Complexity_"+complexities[i]);
          
          
        System.out.println("Calculating average");
        ArrayList<Double> average = new ArrayList<Double>();
        ArrayList<Double> averageLambda = new ArrayList<Double>();
        ArrayList<Double> averageUsage = new ArrayList<Double>();
        
        ArrayList<Integer> lowest = new ArrayList<Integer>();
        ArrayList<Float> lowestLambda = new ArrayList<Float>();
        ArrayList<Float> lowestUsage = new ArrayList<Float>();
        
        ArrayList<Integer> highest = new ArrayList<Integer>();
        ArrayList<Float> highestLambda = new ArrayList<Float>();
        ArrayList<Float> highestUsage = new ArrayList<Float>();
        
        for(int j = 0; j < allRuns.size(); j++) {
            double avg = 0;
            double avgL = 0;
            double avgU = 0;
            
            int hi = 0, lo = 1000000;
            float hiL = 0, loL = 1000000;
            float hiU = 0, loU = 1000000;
            
            for(int k = 0; k < runs;k++) {
                avg += allRuns.get(j)[k];
                avgL += allRunsLambda.get(j)[k];
                avgU += allRunsUsage.get(j)[k];
                
                if(allRuns.get(j)[k] > hi) hi = allRuns.get(j)[k];
                else if(allRuns.get(j)[k] < lo)lo = allRuns.get(j)[k];
                
                if(allRunsLambda.get(j)[k] > hiL) hiL = allRunsLambda.get(j)[k];
                else if(allRunsLambda.get(j)[k] < loL)loL = allRunsLambda.get(j)[k];
                
                if(allRunsUsage.get(j)[k] > hiU) hiU = allRunsUsage.get(j)[k];
                else if(allRunsUsage.get(j)[k] < loU)loU = allRunsUsage.get(j)[k];
            }
            avg = avg / runs;
            average.add(avg);
            
            avgL = avgL / runs;
            averageLambda.add(avgL);
            
            avgU = avgU / runs;
            averageUsage.add(avgU);
            
            lowest.add(lo);
            highest.add(hi);
            
            lowestLambda.add(loL);
            highestLambda.add(hiL);
            
            lowestUsage.add(loU);
            highestUsage.add(hiU);
            
        }
          System.out.println("Calcualting standard deviation ");
        ArrayList<Double> stdDeviation = new ArrayList<Double>();
        ArrayList<Double> stdDeviationLambda = new ArrayList<Double>();
        ArrayList<Double> stdDeviationUsage = new ArrayList<Double>();
          for (int j = 0; j < allRuns.size(); j++) {
              for (int k = 0; k < runs; k++) {
                  double val = allRuns.get(j)[k] - average.get(j);
                  allRuns.get(j)[k] = (int)(val * val);
                  
                  double valL = allRunsLambda.get(j)[k] - averageLambda.get(j);
                  allRunsLambda.get(j)[k] = (float)(valL * valL);
                  
                  double valU = allRunsUsage.get(j)[k] - averageUsage.get(j);
                  allRunsUsage.get(j)[k] = (float)(valU * valU);
              }
              double stdDev = 0;
              double stdDevL = 0;
              double stdDevU = 0;
              for (int k = 0; k < runs; k++) {
                  stdDev += allRuns.get(j)[k];
                  stdDevL += allRunsLambda.get(j)[k];
                  stdDevU += allRunsUsage.get(j)[k];
              }
              stdDev = Math.sqrt(stdDev / runs);
              stdDeviation.add(stdDev);
              
              stdDevL = Math.sqrt(stdDevL / runs);
              stdDeviationLambda.add(stdDevL);
              
              stdDevU = Math.sqrt(stdDevU / runs);
              stdDeviationUsage.add(stdDevU);
          }
          
          System.out.println("Writing to file");
          
          BufferedWriter bw = new BufferedWriter(new FileWriter(absPath+"Avg_stdDev_complexity_"+complexities[i]+"_runs_"+runs+".gatest"));
          bw.write("#generation\taverage fitness\tstandard deviation\thighest fitness\tlowest fitness\n");
          for (int j = 0; j < allRuns.size(); j++) {
              bw.write(j+"\t"+average.get(j)+"\t"+stdDeviation.get(j)+"\t"+highest.get(j)+"\t"+lowest.get(j)+"\n");
          }
          bw.close();
          
          bw = new BufferedWriter(new FileWriter(absPath+"Avg_stdDev_lambda_"+complexities[i]+"_runs_"+runs+".gatest"));
          bw.write("#generation\taverage lambda\tstandard deviation\thighest lambda\tlowest lambda\n");
          for (int j = 0; j < allRunsLambda.size(); j++) {
              bw.write(j+"\t"+averageLambda.get(j)+"\t"+stdDeviationLambda.get(j)+"\t"+highestLambda.get(j)+"\t"+lowestLambda.get(j)+"\n");
          }
          bw.close();
          
          bw = new BufferedWriter(new FileWriter(absPath+"Avg_stdDev_genome_usage_"+complexities[i]+"_runs_"+runs+".gatest"));
          bw.write("#generation\tgenome usage\tstandard deviation\thighest usage\tlowest usage\n");
          for (int j = 0; j < allRunsUsage.size(); j++) {
              bw.write(j+"\t"+averageUsage.get(j)+"\t"+stdDeviationUsage.get(j)+"\t"+highestUsage.get(j)+"\t"+lowestUsage.get(j)+"\n");
          }
          bw.close();
      }
      BufferedWriter bw = new BufferedWriter(new FileWriter(absPath+"General_Statistics.gatest"));
      bw.write("#Complexity\tFirst found\tLast found\tNumber of genomes found\tlongest stuck\n");
      for (int j = 0; j < stats.size(); j++) {
          bw.write(names.get(j)+"\t"+stats.get(j)[0]+"\t"+stats.get(j)[1]+"\t"+stats.get(j)[2]+"\t"+stats.get(j)[3]+"\n");
      }
      bw.close();
      
      
    }
}
