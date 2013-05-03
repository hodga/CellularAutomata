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
public class spliceFiles {

    public static ArrayList<Float>[] getAllColumns(String absPath, String[] separators) throws Exception {

        ArrayList<Float>[] columns = new ArrayList[separators.length];
        for(int i = 0; i < columns.length;i++) {
            columns[i] = new ArrayList<Float>();
        }

        if(!absPath.endsWith("\\"))absPath = absPath+"\\";

        BufferedReader br = new BufferedReader(new FileReader(absPath));
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

    public static void spliceColumns(String[] files, int[] cols, String output) throws Exception {
        String[] separators = {"\t","\t","\t","\t"};
        ArrayList<Float>[] combinedCols = new ArrayList[files.length];
        int length = 0;
        for (int i= 0; i < files.length; i++) {
            ArrayList<Float>[] a = getAllColumns(files[i], separators);
            combinedCols[i] = a[cols[i]];
            if(a[cols[i]].size() > length) length = a[cols[i]].size();
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
          for (int j = 0; j < length; j++) {

              String s = "";
              for (int i= 0; i < combinedCols.length; i++) {
                  float val = -1;
                  if(j < combinedCols[i].size()) val = combinedCols[i].get(j);
                  s += val;
                  if(i != combinedCols.length-1)s+="\t";
              }
              s+= "\n";
              bw.write(s);
          }
          bw.close();
    }

}
