/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cellularautomata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

/**
 *
 * @author Hodga
 */
public class AvgGenerationGenomeFound {

    public static float FindAverage(int complexity, int nbRuns, String absPath) throws Exception{

        int[] results = new int[nbRuns];
        int average = 0;
        for(int i = 0; i < nbRuns;i++) {

            if(!absPath.endsWith("\\"))absPath = absPath+"\\";

            BufferedReader br = new BufferedReader(new FileReader(absPath+"Complexity_"+complexity+"_RunNo_"+i+".gatest"));
            String s = br.readLine();
            String lastS = "100000   ";
            while(s != null && !s.isEmpty()) {
                if(s.charAt(0) == '#') {
                        s = br.readLine();
                        continue;
                    }
                lastS = s;
                s = br.readLine();
            }
            int endInd = lastS.indexOf("   ");
            int gen = Integer.parseInt(lastS.substring(0, endInd));
            results[i] = gen;
            average += gen;
        }

        Arrays.sort(results);
        System.out.println("median: "+results[nbRuns/2]);
        return (((float)average)/((float)nbRuns));
    }

}
