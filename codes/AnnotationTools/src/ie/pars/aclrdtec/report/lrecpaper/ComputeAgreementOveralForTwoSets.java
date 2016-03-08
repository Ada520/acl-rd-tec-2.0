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

import ie.pars.aclrdtec.fileutils.Settings;
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
public class ComputeAgreementOveralForTwoSets {
    

    public static void main(String[] ss) throws IOException, ParserConfigurationException, Exception {

        int count = 0;
       
        
               
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
        
        
        int both=0;
        double sumBound = 0.0;
        double sumClassBased = 0.0;
        NumberFormat nf = new DecimalFormat("#.###");
        int overallSentCounter = 0;
        List<Annotation> annotationAllBQ = new ArrayList();
        List<Annotation> annotationAllAK = new ArrayList();
        for (String f : mapAnnotationBQ.keySet()) {
            if (mapAnnotationAK.containsKey(f)) {
                
                creatUniversalList(mapAnnotationAK.get(f), overallSentCounter, annotationAllAK);
                creatUniversalList(mapAnnotationBQ.get(f), overallSentCounter, annotationAllBQ);
                overallSentCounter += mapAnnotationAK.get(f).getSentences().size();
            }
        }
        
        System.out.println("Annotation size universal BQ : " + annotationAllBQ.size());
        System.out.println("Annotation size universal AK : " +annotationAllAK.size());
        
        AnnotationFile aAK = new AnnotationFile("overall", "overall", null, annotationAllAK);
        AnnotationFile aBQ = new AnnotationFile("overall", "overall", null, annotationAllBQ);
        
        System.out.println("Term boundary assignments " + nf.format(aAK.computeFMScoreTermBoundary(aBQ)));
        System.out.println("Class assignments " + nf.format(aAK.computeFMScoreAllClassAssignment(aBQ)));
        
        System.out.println("{\\bf X} & " + nf.format(aAK.computeFMScoreTermBoundary(aBQ))+ " &  " + nf.format(aAK.computeFMScoreAllClassAssignment(aBQ))+"  & " + annotationAllBQ.size() +"& " +annotationAllAK.size() + "\\\\");
        
       
        for (String t : Settings.termClasses) {
            System.out.println(t + "\t:\t"
                    + nf.format(
                            aAK.computeFMScoreBoundaryPerClass(aBQ, t)
                    ) + "\t"
                    + nf.format(aAK.computeFMScorePerClassAssignment(aBQ, t))
            );
        }
        
     

    }
    
    private static void creatUniversalList( AnnotationFile aFile, int overallSentCounter, List<Annotation>  annotationAll){
            
        Map<Integer, List<Annotation>> annotationMapSentence = aFile.getAnnotationMapSentence();
        
      
        for (Integer sent : annotationMapSentence.keySet()) {
            List<Annotation> annotations = annotationMapSentence.get(sent);
           
            for (Annotation an : annotations) {
                an.updateSentenceNumber(an.getSentenceNumber() + overallSentCounter);
                annotationAll.add(an);
            }
        }

    }
   
    
   
    
}
