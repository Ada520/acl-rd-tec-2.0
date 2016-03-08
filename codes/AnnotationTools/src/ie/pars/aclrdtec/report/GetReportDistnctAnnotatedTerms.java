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
package ie.pars.aclrdtec.report;

import info.atmykitchen.common.IOMethods;
import info.atmykitchen.objects.Annotation;
import info.atmykitchen.objects.AnnotationFile;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
public class GetReportDistnctAnnotatedTerms {

    public static void main(String[] ss) throws IOException, ParserConfigurationException, Exception {

        List<Annotation> al = new ArrayList<Annotation>();
        Set<String> annotatedTermAK = new TreeSet();
         Set<String> annotatedTermAKStringOnly = new TreeSet();
        Set<String> annotatedTermBQ = new TreeSet();
        Set<String> annotatedTermBQStringOnly = new TreeSet();

        
        int count = 0;
        
        List<File> bqFiles = IOMethods.getAllFilesRecursive(new File(ss[0]));
        List<File> akFiles = IOMethods.getAllFilesRecursive(new File(ss[1]));
        TreeMap<String, String> distinicFile = new TreeMap<String, String>();
//           Set<String> distinicFileBQ = new TreeSet<String>();
//           Set<String> distinicFileAK = new TreeSet<String>();
        TreeMap<String, AnnotationFile> mapAnnotationAK = new TreeMap();

        for (File ak : akFiles) {
            AnnotationFile antFile = IOMethods.loadAnnotationFile(ak);
           al.addAll(antFile.getAnnotationList());
            //    distinicFileAK.add(ak.getName());
            //annotatedTermAK 
           annotatedTermAKStringOnly.addAll(antFile.getTermStringsSet());
           
            annotatedTermAK.addAll(antFile.getTermStringsAnnotationSet());
            
            distinicFile.put(antFile.getPublicationYear() + antFile.getAclid(),
                    antFile.getPublicationYear() + "|" + antFile.getAclid() + "|" + antFile.getTitle()
            );
            mapAnnotationAK.put(antFile.getPublicationYear() + antFile.getAclid(), antFile);
        }

        for (File bq : bqFiles) {

            //  System.out.println(bq.getName());
            AnnotationFile antFile = IOMethods.loadAnnotationFile(bq);
            al.addAll(antFile.getAnnotationList());
            // distinicFileAK.add(bq.getName());
            String distinctEntry = antFile.getPublicationYear() + antFile.getAclid();
            annotatedTermBQStringOnly.addAll(antFile.getTermStringsSet());
            if (distinicFile.containsKey(distinctEntry)) {
                // System.out.println(distinctEntry);
                //mapAnnotationBQ.put(antFile.getPublicationYear() + antFile.getAclid(), antFile);
                annotatedTermBQ.addAll(antFile.getTermStringsAnnotationSet());
            }
        }
        System.out.println(distinicFile.size());

        for (String term : annotatedTermBQ) {
            if (!annotatedTermAK.contains(term)) {
                System.out.println(term);
            }
        }
        System.out.println("-----");

        int both = 0;
        double sumBound = 0.0;
        double sumClassBased = 0.0;
        NumberFormat nf = new DecimalFormat("#.##");

        TreeSet<String> allTerms = new TreeSet<>();
        
        allTerms.addAll(annotatedTermAK);
        allTerms.addAll(annotatedTermBQ);
        
        System.out.println("size type " + allTerms.size());
           annotatedTermAKStringOnly.addAll(annotatedTermBQStringOnly);
           System.out.println("Size of type " + annotatedTermAKStringOnly.size());
           System.out.println("Occurances  " + al.size());

    }

    public static String toDisplayCase(String s) {

        final String ACTIONABLE_DELIMITERS = " '-/"; // these cause the character following
        // to be capitalized

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
        }
        return sb.toString();
    }

}
