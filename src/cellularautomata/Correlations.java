/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cellularautomata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author Hodga
 */
public class Correlations {
    
    public static ArrayList<Float>[] getAllColumns(String absPath, int complexitySearched, int run, String[] separators) throws Exception {

        ArrayList<Float>[] columns = new ArrayList[separators.length];
        for(int i = 0; i < columns.length;i++) {
            columns[i] = new ArrayList<Float>();
        }

        if(!absPath.endsWith("\\"))absPath = absPath+"\\";

        BufferedReader br = new BufferedReader(new FileReader(absPath+"Complexity_"+complexitySearched+"_RunNo_"+run+".gatest"));
        String s = br.readLine();
        while(s != null && !s.isEmpty()) {
            if(s.charAt(0) == '#') {
                    s = br.readLine();
                    continue;
                }
            int startInd = s.indexOf(separators[0])+separators[0].length();
            for(int k = 1; k < separators.length;k++) {
                int endInd = s.indexOf(separators[k], startInd+1);

                float complexity = Float.parseFloat(s.substring(startInd, endInd));
                columns[k-1].add(complexity);

                startInd = endInd+separators[k].length();
            }
            float complexity = Float.parseFloat(s.substring(startInd));
            columns[columns.length-1].add(complexity);
            
            s = br.readLine();
        }
        return columns;
    }

    public static void calculateCorrelations(String[] absPaths, String writePath, int[] complexities, int nbRuns, String[] separators, int column, int[] correlatingColumns) throws Exception{

        /*ArrayList<Float>[] correlations = new ArrayList[correlatingColumns.length];
        for (int i= 0; i < correlations.length; i++) {
            correlations[i] = new ArrayList<Float>();
        }*/
        double[] averageCorrelation = new double[correlatingColumns.length];
        double[] highestCorrelation = new double[correlatingColumns.length];
        double[] lowestCorrelation = new double[correlatingColumns.length];
        for (int i = 0; i < lowestCorrelation.length; i++) {
            lowestCorrelation[i] = 2.0f;
            highestCorrelation[i] = -2.0f;
        }

        ArrayList<Float>[] columns = null;
        ArrayList<Float>[] diffColumns = null;
        int ctr = 0;
        for(int x = 0; x < absPaths.length;x++) {
            System.out.println("reading files in: "+absPaths[x]);
            for(int i = 0; i < complexities.length;i++) {
                double[] localAverageCorrelation = new double[correlatingColumns.length];
                for(int j = 0; j < nbRuns; j++) {
                    columns = getAllColumns(absPaths[x], complexities[i], j, separators);

                    //System.out.println("Complexity_"+complexities[i]+"_RunNo_"+j);
                    ctr++;
                    for(int l = 0; l < correlatingColumns.length; l++) {
                        double corr = getPearsonCorrelation(columns[column], columns[correlatingColumns[l]]);
                        //System.out.println(l+": "+corr);
                        averageCorrelation[l] += corr;
                        localAverageCorrelation[l] += corr;
                        if(corr < lowestCorrelation[l])lowestCorrelation[l] = corr;
                        if(corr > highestCorrelation[l])highestCorrelation[l] = corr;
                    }

                    /*diffColumns = new ArrayList[correlatingColumns.length];
                    for (int k = 0; k < diffColumns.length; k++) {
                        diffColumns[k] = new ArrayList<Float>();
                    }

                    for(int l = 0; l < correlatingColumns.length; l++) {
                        //averageDifference[l] = 0;
                        for(int k = 0; k < columns[0].size();k++) {
                            diffColumns[l].add(Math.abs(columns[column].get(k) - columns[correlatingColumns[l]].get(k)));
                        }
                    }*/

                    /*BufferedWriter bw = new BufferedWriter(new FileWriter(writePath+"Complexity_"+complexities[i]+"_RunNo_"+j+".gatest"));

                    if(bw != null) {
                        //write the genome found
                        String s = "#averages";
                        s+= "\n";
                        bw.write(s);
                        bw.write("#generation   columns.....\n");

                        for(int k = 0; k < diffColumns[0].size();k++){
                            String data = k+"  ";
                            for (int l= 0; l < diffColumns.length; l++) {
                                data += diffColumns[l].get(k)+"  ";
                            }
                            bw.write(data+"\n");
                        }
                    }
                    else System.out.println("could not write file");
                    bw.close();*/
                }
                System.out.println("ComplexitySearch: "+complexities[i]);
                for(int l = 0; l < localAverageCorrelation.length;l++) {
                    localAverageCorrelation[l] /= (double)nbRuns;
                    System.out.println(((correlatingColumns[l] == 0) ? " complexity" : " lambda")+" average correlation: "+localAverageCorrelation[l]);
                }
                System.out.println("");
            }
        }
        System.out.println("Total: ");
        for(int l = 0; l < correlatingColumns.length; l++) {
            averageCorrelation[l] /= (double)(ctr);
            System.out.println(((correlatingColumns[l] == 0) ? " complexity" : " lambda")+" average correlation: "+averageCorrelation[l]);
            System.out.println(((correlatingColumns[l] == 0) ? " complexity" : " lambda")+" lowest correlation: "+lowestCorrelation[l]);
            System.out.println(((correlatingColumns[l] == 0) ? " complexity" : " lambda")+" highest correlation: "+highestCorrelation[l]);
        }
    }

     public static double getPearsonCorrelation(ArrayList<Float> scores1,ArrayList<Float> scores2){
        double result = 0;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = scores1.get(0);
        double mean_y = scores2.get(0);
        for(int i=2;i<scores1.size()+1 && i < 1000;i+=1){
            double sweep =Double.valueOf(i-1)/i;
            double delta_x = scores1.get(i-1)-mean_x;
            double delta_y = scores2.get(i-1)-mean_y;
            sum_sq_x += delta_x * delta_x * sweep;
            sum_sq_y += delta_y * delta_y * sweep;
            sum_coproduct += delta_x * delta_y * sweep;
            mean_x += delta_x / i;
            mean_y += delta_y / i;
        }
        double pop_sd_x = (double) Math.sqrt(sum_sq_x/scores1.size());
        double pop_sd_y = (double) Math.sqrt(sum_sq_y/scores1.size());
        double cov_x_y = sum_coproduct / scores1.size();
        result = cov_x_y / (pop_sd_x*pop_sd_y);
        return result;
    }

}
