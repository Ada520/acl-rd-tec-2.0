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
import info.atmykitchen.common.BasicStat;
import info.atmykitchen.objects.Annotation;
import info.atmykitchen.objects.AnnotationFile;
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
public class KappaAverageFileGetStatForClassDist {

    public static void main(String[] ss) throws IOException, ParserConfigurationException, Exception {
        int count = 0;

        String[] lines = new String[Settings.termClasses.length + 1];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = "";

        }
        //int i = 4;


        TreeMap<String, AnnotationFile> mapAnnotationBQ = ReportCommonMethods.loadAnntoationFileToMapByFileName(ss[0]);
        TreeMap<String, AnnotationFile> mapAnnotationAK = ReportCommonMethods.loadAnntoationFileToMapByFileName(ss[1]);

        NumberFormat nf = new DecimalFormat("#.###");

        ArrayList<Double> kappaList = new ArrayList<>();
        ArrayList<Double> kappaListPo = new ArrayList<>();
        for (String f : mapAnnotationBQ.keySet()) {
            System.out.println(f);
            if (mapAnnotationAK.containsKey(f)) {
                AnnotationFile aAK = mapAnnotationAK.get(f);
                AnnotationFile aBQ = mapAnnotationBQ.get(f);

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

                Map<String, List<Annotation>> annotationMapAK = aAKMatch.getAnnotationMapPerClassCategory();
                Map<String, List<Annotation>> annotationMapBQ = aBQMatch.getAnnotationMapPerClassCategory();
                int countAllTerms = annotationAllAKCommon.size();
                if (countAllTerms > 1) {
                    double k = ReportCommonMethods.getKappaForAnnotationMap(countAllTerms, annotationMapBQ, annotationMapAK, Settings.termClasses);
                    System.out.println(
                            nf.format(k));
                    if (!Double.isNaN(k)) {
                        kappaList.add(k);
                    }
                    //kappaListPo.add(k[1]);
                }
            }
        }

        Double[] toArrayOveral = kappaList.toArray(new Double[kappaList.size()]);

//        for(Double d: toArrayOveral){
//        System.out.println(d);}
        double boundaryMea = BasicStat.mean(toArrayOveral);
        double boudnaryVar = BasicStat.var(toArrayOveral);

        Double[] toArrayOveralPositive = kappaListPo.toArray(new Double[kappaListPo.size()]);
//        System.out.println("F");
//        for(Double d: toArrayOveralPositive){
//        System.out.println(d);}
        double boundaryMeaPo = BasicStat.mean(toArrayOveralPositive);
        double boudnaryVarPo = BasicStat.var(toArrayOveralPositive);

//        double boundaryMea = BasicStat.mean(toArrayOveral);
//        double boudnaryVar = BasicStat.var(toArrayOveral);
        System.out.println("* " + nf.format(boundaryMea) + " " + nf.format(boundaryMeaPo) + " & " + nf.format(boudnaryVar) + " " + nf.format(boudnaryVarPo)
        );

    }
}
