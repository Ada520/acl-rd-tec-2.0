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

import info.atmykitchen.objects.Annotation;
import info.atmykitchen.objects.AnnotationFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author bq
 */
public class GetCorpusStat {

    public static void main(String[] ss) throws IOException, ParserConfigurationException, Exception {
        int count = 0;
        
        
        String[] lines = new String[Settings.termClasses.length + 1];
        for (int i = 0; i < lines.length; i++) {
            lines[i]="";
            
        }
        
        TreeMap<String, AnnotationFile> mapAnnotationBQ = ReportCommonMethods.loadAnntoationFileToMapByFileName(ss[0]);
        TreeMap<String, AnnotationFile> mapAnnotationAK = ReportCommonMethods.loadAnntoationFileToMapByFileName(ss[1]);
        
        System.out.println("Abstract , sentneces, terms, lr, lrp, mea, mode, other, tech, tool");
        getStatForProfile(mapAnnotationBQ);
        getStatForProfile(mapAnnotationAK);
        getStatForRedundant(mapAnnotationBQ, mapAnnotationAK);
        getStatForUniqueRedundant(mapAnnotationBQ, mapAnnotationAK);
        
       
        
    }
    
    private static void getStatForUniqueRedundant(TreeMap<String, AnnotationFile> mapAnnotationBQ, TreeMap<String, AnnotationFile> mapAnnotationAK){
       
        Set<String> fileSet = new TreeSet();
        List<Annotation> annotationListFinal = 
                new ArrayList();
        Set<String> sent = new TreeSet();
        
        for (String afile : mapAnnotationBQ.keySet()) {
            fileSet.add(afile);
            if (mapAnnotationAK.containsKey(afile)) {
                List<Annotation> annotationList = mapAnnotationBQ.get(afile).getAnnotationList();
                List<Annotation> annotationList1 = mapAnnotationAK.get(afile).getAnnotationList();
                int x = annotationList.size() + annotationList1.size();
                sent.addAll( mapAnnotationBQ.get(afile).getSentences());
                TreeSet<Annotation> annotationSet = new TreeSet<>();
                annotationSet.addAll(annotationList);
                annotationSet.addAll(annotationList1);
                annotationListFinal.addAll(annotationSet);
            }else{
                annotationListFinal.addAll( mapAnnotationBQ.get(afile).getAnnotationList());
                sent.addAll( mapAnnotationBQ.get(afile).getSentences());
            }
        }
        
        for (String afile : mapAnnotationAK.keySet()) {
            fileSet.add(afile);
            if (!mapAnnotationBQ.containsKey(afile)) {
                annotationListFinal.addAll(mapAnnotationAK.get(afile).getAnnotationList());
                sent.addAll( mapAnnotationAK.get(afile).getSentences());
            }
        }
        List<String> sentenListUn = new ArrayList(sent);

        AnnotationFile afAllRed = new AnnotationFile("", "", sentenListUn, annotationListFinal);
        

        String line = (fileSet.size() ) + "\t&\t";
        line += afAllRed.getSentences().size() + "\t&\t" + afAllRed.getAnnotationList().size();

        for (String classTern : afAllRed.getAnnotationMapPerClassCategory().keySet()) {
            line += "\t&\t" + afAllRed.getAnnotationMapPerClassCategory().get(classTern).size();
        }
        System.out.println(line);

    }
    private static void getStatForRedundant(TreeMap<String, AnnotationFile> mapAnnotationBQ, TreeMap<String, AnnotationFile> mapAnnotationAK){
         List<Annotation> annotatinMapToList = ReportCommonMethods.annotatinMapToList(mapAnnotationBQ);
        List<Annotation> annotatinMapToList2 = ReportCommonMethods.annotatinMapToList(mapAnnotationAK);
        annotatinMapToList2.addAll(annotatinMapToList);
        List<String> annotatinMapToListSentence = ReportCommonMethods.annotatinMapToListSentence(mapAnnotationBQ);
        List<String> annotatinMapToListSentence2 = ReportCommonMethods.annotatinMapToListSentence(mapAnnotationAK);
        annotatinMapToListSentence2.addAll(annotatinMapToListSentence);
        AnnotationFile afAllRed = new AnnotationFile("", "", annotatinMapToListSentence2, annotatinMapToList2);
        System.out.println();

        String line = (mapAnnotationBQ.size() + mapAnnotationAK.size()) + "\t&\t";
        line += afAllRed.getSentences().size() + "\t&\t" + afAllRed.getAnnotationList().size();

        for (String classTern : afAllRed.getAnnotationMapPerClassCategory().keySet()) {
            line += "\t&\t" + afAllRed.getAnnotationMapPerClassCategory().get(classTern).size();
        }
        System.out.println(line);

    }
    
    private static void getStatForProfile(TreeMap<String, AnnotationFile> mapAnnotationBQ) {
        
        List<Annotation> annotatinMapToList = ReportCommonMethods.annotatinMapToList(mapAnnotationBQ);
        
        AnnotationFile ad = new AnnotationFile("","", ReportCommonMethods.annotatinMapToListSentence(mapAnnotationBQ), annotatinMapToList);
        String line = mapAnnotationBQ.size() + "\t&\t";
        line+=ad.getSentences().size() + "\t&\t";
        line+=annotatinMapToList.size();
        for(String classTern : ad.getAnnotationMapPerClassCategory().keySet()){
            line+=  "\t&\t" + ad.getAnnotationMapPerClassCategory().get(classTern).size() ;
        }
        System.out.println(line);

    }
    


    
   
}
