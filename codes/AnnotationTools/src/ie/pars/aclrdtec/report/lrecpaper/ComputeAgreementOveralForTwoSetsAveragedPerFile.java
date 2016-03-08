/* 
 * Copyright (C) 2016 Behrang QasemiZadeh <zadeh at phil.hhu.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ie.pars.aclrdtec.report.lrecpaper;

import info.atmykitchen.common.BasicStat;
import info.atmykitchen.common.IOMethods;
import info.atmykitchen.objects.Annotation;
import info.atmykitchen.objects.AnnotationFile;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author bq
 */
public class ComputeAgreementOveralForTwoSetsAveragedPerFile {
    

    public static void main(String[] ss) throws IOException, ParserConfigurationException, Exception {

           List<File> bqFiles = IOMethods.getAllFilesRecursive(new File(ss[0]));
        List<File> akFiles = IOMethods.getAllFilesRecursive(new File(ss[1]));


        TreeMap<String, AnnotationFile> mapAnnotationAK = new TreeMap();
        TreeMap<String, AnnotationFile> mapAnnotationBQ = new TreeMap();
        for (File ak : akFiles) {
            AnnotationFile antFile = IOMethods.loadAnnotationFile(ak);
            mapAnnotationAK.put(antFile.getPublicationYear() + "---" + antFile.getAclid(), antFile);
        }

        for (File bq : bqFiles) {
            AnnotationFile antFile = IOMethods.loadAnnotationFile(bq);
            mapAnnotationBQ.put(antFile.getPublicationYear() + "---" + antFile.getAclid(), antFile);

        }
        
        
        NumberFormat nf = new DecimalFormat("#.###");
        List<Double> boundaryList = new ArrayList();
        List<Double> overalList = new ArrayList();
        
        for (String f : mapAnnotationBQ.keySet()) {
            if (mapAnnotationAK.containsKey(f)) {
                AnnotationFile akAnnotation = mapAnnotationAK.get(f);
                double boundary = mapAnnotationBQ.get(f).computeFMScoreTermBoundary(akAnnotation);
                boundaryList.add(boundary);
                double overall = mapAnnotationBQ.get(f).computeFMScoreAllClassAssignment(akAnnotation);
                overalList.add(overall);
                
            }
        }
        
        Double[] toArrayBundary = boundaryList.toArray(new Double[boundaryList.size()]);
        Double[] toArrayOveral = overalList.toArray(new Double[overalList.size()]);

        double boundaryMea = BasicStat.mean(toArrayBundary);
        double boudnaryVar = BasicStat.var(toArrayBundary);

        double overalMea = BasicStat.mean(toArrayOveral);
        double overalVar = BasicStat.var(toArrayOveral);

        System.out.println("{\\bf X} & " + nf.format(boundaryMea)+"  &  " + nf.format(boudnaryVar)+" & " + nf.format(overalMea) +  " & " + nf.format(overalVar)+" \\\\");

    }
    
  
   
    
  
    
}
