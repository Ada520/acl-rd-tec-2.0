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
import org.javatuples.Triplet;

/**
 *
 * @author bq
 */
public class GetStatForClassDistForCorpusCommon {

    public static void main(String[] ss) throws IOException, ParserConfigurationException, Exception {
        int count = 0;

        String[] lines = new String[Settings.termClasses.length + 1];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = "";

        }


        TreeMap<String, AnnotationFile> mapAnnotationAK = 
                ReportCommonMethods.loadAnntoationFileToMapByFileName(ss[0]);
        TreeMap<String, AnnotationFile> mapAnnotationBQ = 
                ReportCommonMethods.loadAnntoationFileToMapByFileName(ss[1]);
        NumberFormat nf = new DecimalFormat("#.###");
        int overallSentCounter = 0;
        List<Annotation> annotationAllBQ = new ArrayList();
        List<Annotation> annotationAllAK = new ArrayList();
        for (String f : mapAnnotationBQ.keySet()) {
            if (mapAnnotationAK.containsKey(f)) {
               ReportCommonMethods.fuseAnnotationLists(mapAnnotationAK.get(f), overallSentCounter, annotationAllAK);
                ReportCommonMethods.fuseAnnotationLists(mapAnnotationBQ.get(f), overallSentCounter, annotationAllBQ);
                overallSentCounter += mapAnnotationAK.get(f).getSentences().size();
            }
        }

        System.out.println("Annotation size universal BQ : " + annotationAllBQ.size());
        System.out.println("Annotation size universal AK : " + annotationAllAK.size());

        AnnotationFile aAK = new AnnotationFile("overall", "overall", null, annotationAllAK);
        AnnotationFile aBQ = new AnnotationFile("overall", "overall", null, annotationAllBQ);

        // build the common annotations with excat match
        Map<Triplet, Annotation> annotationSpanMapAK = aAK.getAnnotationSpanMap();
        Map<Triplet, Annotation> annotationSpanMapBQ = aBQ.getAnnotationSpanMap();
        List<Annotation> annotationAllBQCommon = new ArrayList();
        List<Annotation> annotationAllAKCommon = new ArrayList();
        for (Triplet sp : annotationSpanMapAK.keySet()) {
            if (annotationSpanMapBQ.containsKey(sp)) {
                annotationAllBQCommon.add(annotationSpanMapBQ.get(sp));
                annotationAllAKCommon.add(annotationSpanMapAK.get(sp));
            }
        }

        AnnotationFile aAKMatch = new AnnotationFile("overallAndMatch", "overallAndMatch", null, annotationAllAKCommon);
        AnnotationFile aBQMatch = new AnnotationFile("overallAndMatch", "overallAndMatch", null, annotationAllBQCommon);

        for (int j = 0; j < Settings.termClasses.length; j++) {

            double[] pope = 
                    ReportCommonMethods.getKappaForAnnotationClass(
                            aAKMatch.getAnnotationMapPerClassCategory(), aBQMatch.getAnnotationMapPerClassCategory(), Settings.termClasses[j]);  
            double kappaClassSpe = (pope[0] - pope[1]) / (1 - pope[1]);
            
            //double fscoreBound = 
            lines[j] += " & " + getTableLineVertical(Settings.termClasses[j], aBQ) + " (" + getTableLineVertical(Settings.termClasses[j], aBQMatch) + ")" + "&"
                    + getTableLineVertical(Settings.termClasses[j], aAK) + " (" + getTableLineVertical(Settings.termClasses[j], aAKMatch) + ") & " +  
                    
                    nf.format(kappaClassSpe);
            
            
            
        }
        lines[lines.length - 1] += " & " + aBQ.getAnnotationList().size() + " (" + aBQMatch.getAnnotationList().size() + ")" + " & " + aAK.getAnnotationList().size()
                + " (" + aAKMatch.getAnnotationList().size() + ")";

        for (int i = 0;
                i < lines.length - 1; i++) {
            System.out.println(Settings.termClasses[i] + " " + lines[i] + "\\\\\\hline");
        }

        System.out.println(
                "Total\\# " + lines[lines.length - 1]);


    }
    
   
   
    private static int getTableLineVertical(String termClasses, AnnotationFile af) {
        if (af.getAnnotationMapPerClassCategory().containsKey(termClasses)) {
            return af.getAnnotationMapPerClassCategory().get(termClasses).size();
        } else {
            return 0;
        }

    }

  

}
