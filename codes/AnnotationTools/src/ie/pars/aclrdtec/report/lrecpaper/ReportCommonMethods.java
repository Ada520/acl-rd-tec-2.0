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

import info.atmykitchen.common.IOMethods;
import info.atmykitchen.objects.Annotation;
import info.atmykitchen.objects.AnnotationFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author bq
 */
public class ReportCommonMethods {

    static TreeMap<String, AnnotationFile> loadAnntoationFileToMapByFileName(String path) throws Exception {
        TreeMap<String, AnnotationFile> mapAnnotationFileName = new TreeMap();
        List<File> akFiles = IOMethods.getAllFilesRecursive(new File(path));
        for (File ak : akFiles) {
            AnnotationFile antFile = IOMethods.loadAnnotationFile(ak);
            mapAnnotationFileName.put(antFile.getPublicationYear() + "---" + antFile.getAclid(), antFile);
        }
        return mapAnnotationFileName;
    }

    public static List<Annotation> annotatinMapToList(TreeMap<String, AnnotationFile> anFileMap) {
        List<Annotation> annotationList = new ArrayList();
        for (AnnotationFile af : anFileMap.values()) {
            annotationList.addAll(af.getAnnotationList());
        }
        return annotationList;
    }

    public static List<String> annotatinMapToListSentence(TreeMap<String, AnnotationFile> anFileMap) {
        List<String> annotationList = new ArrayList();
        for (AnnotationFile af : anFileMap.values()) {
            annotationList.addAll(af.getSentences());
        }
        return annotationList;
    }
    public static void fuseAnnotationLists(AnnotationFile aFile, int overallSentCounter, List<Annotation> annotationAll) {

        Map<Integer, List<Annotation>> annotationMapSentence = aFile.getAnnotationMapSentence();

        for (Integer sent : annotationMapSentence.keySet()) {
            List<Annotation> annotations = annotationMapSentence.get(sent);

            for (Annotation an : annotations) {
                an.updateSentenceNumber(an.getSentenceNumber() + overallSentCounter);
                annotationAll.add(an);
            }
        }

    }

    public static double getKappaForAnnotationMap(int countAllTerms,
            Map<String, List<Annotation>> annotationMapRef,
            Map<String, List<Annotation>> annotationMapSecond, String[] termClasses) {
        double po = 0.0;
        double pe = 0.0;

      
        for (String claasTerm : termClasses) {

            double[] pope = getKappaForAnnotationClass(annotationMapRef, annotationMapSecond, claasTerm);
            po += pope[0];
            pe += pope[1];
        }

        po = po / countAllTerms;
        pe = pe / (countAllTerms * countAllTerms);

        //System.out.println("pe: " + pe);
        //System.out.println("po: " + po);
        double kappa = (po - pe) / (1 - pe);

        return kappa;
    }

    public static double[] getKappaForAnnotationClass(Map<String, List<Annotation>> annotationMapRef,
            Map<String, List<Annotation>> annotationMapSecond, String termClass) {
        int countClassAllAK = 0;
        int countClassAllBQ = 0;
        int countClassCommonAKBQ = 0;
        double po = 0.0;
        double pe =0.0;
        if (annotationMapRef.containsKey(termClass)) {
            countClassAllAK = annotationMapRef.get(termClass).size();
        }
        if (annotationMapSecond.containsKey(termClass)) {
            countClassAllBQ = annotationMapSecond.get(termClass).size();
        }
        if (annotationMapSecond.containsKey(termClass) && annotationMapRef.containsKey(termClass)) {
            List<Annotation> listAnnotationBQ = annotationMapSecond.get(termClass);
            List<Annotation> listAnnAK = annotationMapRef.get(termClass);
            for (Annotation at : listAnnotationBQ) {
                for (int j = 0; j < listAnnAK.size(); j++) {
                    if (listAnnAK.get(j).hasSameBoundaryAs(at)
                            && listAnnAK.get(j).getType().equals(at.getType())) {
                        countClassCommonAKBQ++;
                        break;
                    }
                }
            }
        }
        System.out.println("\t" + termClass + " A1#:" + countClassAllBQ
                + " A2#" + countClassAllAK + " AO#:"
                + countClassCommonAKBQ
        );
        po += countClassCommonAKBQ;
        pe += countClassAllAK * countClassAllBQ;
        double[] ret = {po,pe};
        return ret;
    }
    
    public static int getCountCommonAnnotationSpanForClass(Map<String, List<Annotation>> annotationMapRef,
            Map<String, List<Annotation>> annotationMapSecond, String claasTerm) {
        int countClassCommonAKBQ=0;
        if (annotationMapSecond.containsKey(claasTerm) && annotationMapRef.containsKey(claasTerm)) {
            List<Annotation> listAnnotationBQ = annotationMapSecond.get(claasTerm);
            List<Annotation> listAnnAK = annotationMapRef.get(claasTerm);
            for (Annotation at : listAnnotationBQ) {
                for (int j = 0; j < listAnnAK.size(); j++) {
                    if (listAnnAK.get(j).hasSameBoundaryAs(at)
                            && listAnnAK.get(j).getType().equals(at.getType())) {
                        countClassCommonAKBQ++;
                        break;
                    }
                }
            }
        }
        return countClassCommonAKBQ;
    }

}
