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
import info.atmykitchen.objects.AnnotationFile;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author bq
 */
public class WebpageReportPerDistincAnnotationFile {
    

    public static void main(String[] ss) throws IOException, ParserConfigurationException, Exception {

        int count = 0;
       
        List<File> bqFiles = IOMethods.getAllFilesRecursive(new File(ss[0]));
        List<File> akFiles = IOMethods.getAllFilesRecursive(new File(ss[1]));
        TreeMap<String, String> distinicFile = new TreeMap<String, String>();
//           Set<String> distinicFileBQ = new TreeSet<String>();
//           Set<String> distinicFileAK = new TreeSet<String>();
           TreeMap<String, AnnotationFile> mapAnnotationAK = new TreeMap();
            TreeMap<String, AnnotationFile> mapAnnotationBQ = new TreeMap();
        for (File ak : akFiles) {
            AnnotationFile antFile = IOMethods.loadAnnotationFile(ak);
           // distinicFileAK.add(ak.getName());
            distinicFile.put(antFile.getPublicationYear()+"---"+antFile.getAclid(),antFile.getPublicationYear()+"|"+antFile.getAclid()
                    +"|"+toDisplayCase(antFile.getTitle()));
            mapAnnotationAK.put(antFile.getPublicationYear()+"---"+antFile.getAclid(), antFile);
     
        }

        for (File bq : bqFiles) {
            AnnotationFile antFile = IOMethods.loadAnnotationFile(bq);
            //distinicFileAK.add(bq.getName());
            distinicFile.put(antFile.getPublicationYear()  +"---"+antFile.getAclid(), antFile.getPublicationYear()+"|"+antFile.getAclid()
                    + "|" + toDisplayCase(antFile.getTitle()));
            mapAnnotationBQ.put(antFile.getPublicationYear()+"---" + antFile.getAclid(), antFile);

        }
        System.out.println(distinicFile.size());
        
        int both=0;
        double sumBound=0.0;
        double sumClassBased=0.0;
          NumberFormat nf = new DecimalFormat("#.##");
        for (String f : distinicFile.keySet()) {
            String rest = "| ";
            if (mapAnnotationBQ.containsKey(f)) {
                rest += getTickMark(f.split("---")[1],"bq");
            }
            rest += " | ";
            if (mapAnnotationAK.containsKey(f)) {
                 rest += getTickMark(f.split("---")[1],"ak");
            }

            if (mapAnnotationAK.containsKey(f) && mapAnnotationBQ.containsKey(f)) {
                both++;
                AnnotationFile getAK = mapAnnotationAK.get(f);
                AnnotationFile getBQ = mapAnnotationBQ.get(f);
                if(!getAK.areSentenceStringsIdentical(getBQ))
                {
                    System.err.println("Error in sentence matching " + getAK.getAclid());
                }
                double computeFMScoreTermBoundary = getAK.computeFMScoreTermBoundary(getBQ);
                sumBound += computeFMScoreTermBoundary;

                String formatBnd = nf.format(computeFMScoreTermBoundary);    
                double computeFMScoreAndClass = getAK.computeFMScoreAllClassAssignment(getBQ);
                sumClassBased+=computeFMScoreAndClass;
                rest += "| " + formatBnd + " | " + nf.format(computeFMScoreAndClass);
                
            } else {
               rest+= "||";
            }
            //uncomment this
            System.out.println(distinicFile.get(f).replace("-", "&#8209;") + rest);
        }
      
        System.out.println("* Total number of abstracts annotated by at least one annotaotr: " +distinicFile.size() ); 
        System.out.println("* Total number of files annotated by the first annotator: " + akFiles.size());
        System.out.println("* Total number of files annotated by the second annotator: " +bqFiles.size());
        System.out.println("* Total number of files annotated by both annotator: " + both);
        System.out.println("* Average inter annotator agreement on annotated boundaries: " + nf.format(sumBound/both));
        System.out.println("* Average inter annotator agreement on annotated boundaries and their assigned class:" + nf.format(sumClassBased/both));

    }
    
     private static String getTickMark(String docID, String annotator) {
        String urlBaseDoc = "<a href=\"/lr/corpora/run.cgi/view?q=aword%2C%3Cterm+%2F%3E+within+%3Cdoc+id%3D%22"+docID+"%22+%2F%3E;"
                + "corpname=aclrdtec2"+annotator+";"
                + "viewmode=kwic;attrs=word&attr_allpos=kw&ctxattrs=word&structs=term&refs=%3Dterm.class%2C%3Dterm.id"
                + "%2C%3Dterm.annotatorid"
                + "&gdexconf=&attr_tooltip=attr_tooltip;fromp=1\">&#10003;</a>";
        return urlBaseDoc;
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
