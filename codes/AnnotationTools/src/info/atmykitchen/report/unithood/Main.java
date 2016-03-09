/*
 * The MIT License
 *
 * Copyright 2015 Behrang QasemiZadeh <me at atmykitchen.info>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.atmykitchen.report.unithood;


import info.atmykitchen.common.BasicStat;
import info.atmykitchen.objects.SimpleTermAnnotation;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Generate report of annotation we did so far
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class Main {

    public static void main(String[] sugar) throws Exception {
//        if(sugar.length!=3){
//            System.out.println("Give me the right param");
//        }
        //int roundHist = 4;
        String base = 
                "C:\\Users\\bq\\Dropbox\\_collaborations\\_akbq\\acl_rd_tec\\"
                + "annotation-history\\"
                + "3";

//        String fileRoot1 = base + "2" +"\\ak\\"; //sugar[0] ;// 
//        //"/acl_rd_tec/annotation-history/" + roundHist +
//        //   base+   "\\ak\\H05-1012_abstr.xml";
//        String fileRoot2
//                = base + "3\\ak\\";// //   sugar[1];//
//        // + roundHist + 
//        //         base+ "\\bq\\H05-1012_abstr.xml";
         String fileRoot1 = base +"\\ak\\"; //sugar[0] ;// 
        //"/acl_rd_tec/annotation-history/" + roundHist +
        //   base+   "\\ak\\H05-1012_abstr.xml";
        String fileRoot2
                = base + "\\bq\\";// //   sugar[1];//
        // + roundHist + 
        //         base+ "\\bq\\H05-1012_abstr.xml";
        System.out.println(fileRoot1);
        System.out.println(fileRoot2);
        AnnotatorProfile ak = new AnnotatorProfile(fileRoot1);
        AnnotatorProfile bq = new AnnotatorProfile(fileRoot2);

        System.out.println("Only for term boundaries");
        getFscoreForOnlyTermBoundaries(ak,bq);
       // System.out.println("For those with agreed boundaries");
        System.out.println("Only  for agreed terms");
        getFscoreForAgreedTerms(ak,bq);
        
        System.out.println("For everything");
       getFscoreForOverall(ak,bq);
       //writeSideBySide("compare-terms", ak,bq );
       

    }
    
    private static void getFscoreForOverall( AnnotatorProfile ak,  AnnotatorProfile bq){
         double sum = 0.0;
          ArrayList<Double> doubleList = new ArrayList<>();
        for (String file : ak.getAnnotationStatPerFile().keySet()) {
            ArrayList<SimpleTermAnnotation> listAnnAK = ak.getAnnotationStatPerFile().get(file).getListSimpleAnnotation();
            ArrayList<SimpleTermAnnotation> listAnnBQ = bq.getAnnotationStatPerFile().get(file).getListSimpleAnnotation();
            TreeSet<String> bqSet = new TreeSet();
            TreeSet<String> akSet = new TreeSet();
            for (int i = 0; i < Math.min(listAnnAK.size(), listAnnBQ.size()); i++) {
                bqSet.add(listAnnBQ.get(i).toString());
                akSet.add(listAnnAK.get(i).toString());
            }
              if (listAnnAK.size() > listAnnBQ.size()) {
                for (int i = Math.min(listAnnAK.size(), listAnnBQ.size()); i < listAnnAK.size(); i++) {
                   akSet.add(listAnnAK.get(i).toString());
                }

            } else {
                for (int i = Math.min(listAnnAK.size(), listAnnBQ.size()); i < listAnnBQ.size(); i++) {
                    bqSet.add(listAnnBQ.get(i).toString());
                }
            }
            double computeFMScore = computeFMScore(akSet,bqSet);
            sum+=computeFMScore;
           // System.out.println(file+"\t"+ computeFMScore);
            doubleList.add(computeFMScore);
        }
        System.out.println("Sum average = " + (sum/ak.getAnnotationStatPerFile().keySet().size()));
    Double[] toArray = doubleList.toArray(new Double[doubleList.size()]);
        System.out.println("Mean: " + BasicStat.mean(toArray));
        System.out.println("MAX: " + BasicStat.max(toArray));
         System.out.println("StdDev: " + BasicStat.stddev(toArray));  
         System.out.println("Variance: " + BasicStat.var(toArray));       
    }

    private static void getFscoreForOnlyTermBoundaries( AnnotatorProfile ak,  AnnotatorProfile bq){
        ArrayList<Double> doubleList = new ArrayList<>();
        double sum = 0.0;
        for (String file : ak.getAnnotationStatPerFile().keySet()) {
            ArrayList<SimpleTermAnnotation> listAnnAK = ak.getAnnotationStatPerFile().get(file).getListSimpleAnnotation();
            ArrayList<SimpleTermAnnotation> listAnnBQ = bq.getAnnotationStatPerFile().get(file).getListSimpleAnnotation();
            TreeSet<String> bqSet = new TreeSet();
            TreeSet<String> akSet = new TreeSet();
            for (int i = 0; i < Math.min(listAnnAK.size(), listAnnBQ.size()); i++) {
                bqSet.add(listAnnBQ.get(i).getTermString());
                akSet.add(listAnnAK.get(i).getTermString());
            }
              if (listAnnAK.size() > listAnnBQ.size()) {
                for (int i = Math.min(listAnnAK.size(), listAnnBQ.size()); i < listAnnAK.size(); i++) {
                   akSet.add(listAnnAK.get(i).getTermString());
                }

            } else {
                for (int i = Math.min(listAnnAK.size(), listAnnBQ.size()); i < listAnnBQ.size(); i++) {
                    bqSet.add(listAnnBQ.get(i).getTermString());
                }
            }
            double computeFMScore = computeFMScore(akSet,bqSet);
            sum+=computeFMScore;
            doubleList.add(computeFMScore);
                    
            System.out.println(file+"\t"+ computeFMScore);
            
        }
        System.out.println("Sum average = " + (sum/ak.getAnnotationStatPerFile().keySet().size()));
        Double[] toArray = doubleList.toArray(new Double[doubleList.size()]);
        System.out.println("Mean: " + BasicStat.mean(toArray ));
        System.out.println("MAX: " + BasicStat.max(toArray));
         System.out.println("StdDev: " + BasicStat.stddev(toArray));  
         System.out.println("Variance: " + BasicStat.var(toArray));       
    
    }

    
     
    private static void getFscoreForAgreedTerms( AnnotatorProfile ak,  AnnotatorProfile bq){
         ArrayList<Double> doubleList = new ArrayList<>();
         double sum = 0.0;
        for (String file : ak.getAnnotationStatPerFile().keySet()) {
           // ArrayList<SimpleTermAnnotation> listAnnAK = ak.getAnnotationStatPerFile().get(file).getListSimpleAnnotation();
            // ArrayList<SimpleTermAnnotation> listAnnBQ = bq.getAnnotationStatPerFile().get(file).getListSimpleAnnotation();
            TreeSet<String> bqSet = new TreeSet();
            TreeSet<String> akSet = new TreeSet();
            
            AnnotationFileStat afAKProf = ak.getAnnotationStatPerFile().get(file);
            AnnotationFileStat afBQProf = bq.getAnnotationStatPerFile().get(file);
            TreeMap<String, TreeSet<String>> termAnnotationMapAK = afAKProf.getTermAnnotationMap();
            TreeMap<String, TreeSet<String>> termAnnotationMapBQ = afBQProf.getTermAnnotationMap();
            for (String term : termAnnotationMapAK.keySet()) {
                if (termAnnotationMapBQ.containsKey(term)) {
                    bqSet.add(term + "-" + termAnnotationMapBQ.get(term).first());
                    akSet.add(term + "-" + termAnnotationMapAK.get(term).first());
                }
            }
          
            double computeFMScore = computeFMScore(akSet, bqSet);
            sum += computeFMScore;
            doubleList.add(computeFMScore);
           // System.out.println(file + "\t" + computeFMScore);
            
        }
        System.out.println("Sum average = " + (sum/ak.getAnnotationStatPerFile().keySet().size()));
            Double[] toArray = doubleList.toArray(new Double[doubleList.size()]);
        System.out.println("Mean: " + BasicStat.mean(toArray));
        System.out.println("MAX: " + BasicStat.max(toArray));
         System.out.println("StdDev: " + BasicStat.stddev(toArray));  
         System.out.println("Variance: " + BasicStat.var(toArray));       

    }

 
    
    private static <T> double computeFMScore(TreeSet<T> a, TreeSet<T> b) {

        //assume that *a* is the gold referencet, 
        int truePositives = 0; /// this means that a term is in both *a* and *b*
        // int trueNegative = 0; --> does not exist in our evaluation since only terms are annotated
        int falsePositives = 0; /// count of terms appear in *b* but not in *a* is a false positive
        int falseNegatives = 0; /// count of terms apper in *a* but not in *b* is a false negative

        for (T term : a) {
            if (!b.contains(term)) {
                falseNegatives++;

            } else {
                truePositives++;
            }
        }

        for (T term : b) {
            if (!a.contains(term)) {
                falsePositives ++;
            }

        }

//        System.out.println("TP: " + truePositives);
//        System.out.println("FP: " + falsePositives);
//        System.out.println("FN: " + falseNegatives);
        double precision = (truePositives * 1.0) / (truePositives + falsePositives);
        double recall = (truePositives * 1.0) / (truePositives + falseNegatives);
        double fscore = 0;
        if (recall + precision > 0) {
            fscore = (2 * (precision * recall)) / (precision + recall);
        }

// The Matthews correlation coefficient 
//        double mcc = truePositives -(falsePositives*falseNegatives)/
//                Math.sqrt((truePositives+falsePositives)*(truePositives+falseNegatives)*falsePositives*falseNegatives);
//        
        //System.out.println("MCC " + mcc);
//        System.out.println("===");
//        System.out.println("Prec: " + precision);
//        System.out.println("Recall: " + recall);
//        System.out.println("F-score: " + fscore);
        return fscore;

    }

    private static void writeSideBySide(String fileName, AnnotatorProfile ak, AnnotatorProfile bq) throws Exception {
        PrintWriter pwOut = new PrintWriter(new FileWriter(fileName));
        pwOut.println();
        for (String file : ak.getAnnotationStatPerFile().keySet()) {
            pwOut.println("----");
            pwOut.println("file " + file);
            pwOut.println("----");
            ArrayList<SimpleTermAnnotation> listAnnAK = ak.getAnnotationStatPerFile().get(file).getListSimpleAnnotation();
            ArrayList<SimpleTermAnnotation> listAnnBQ = bq.getAnnotationStatPerFile().get(file).getListSimpleAnnotation();
            for (int i = 0; i < Math.min(listAnnAK.size(), listAnnBQ.size()); i++) {
                pwOut.println(listAnnAK.get(i).toString() + " \t :: " + listAnnBQ.get(i).toString());
                //System.out.println(listAnnBQ.get(i) +" \tvs\t" + listAnnAK.get(i));
            }
            if (listAnnAK.size() > listAnnBQ.size()) {
                for (int i = Math.min(listAnnAK.size(), listAnnBQ.size()); i < listAnnAK.size(); i++) {
                    pwOut.println(listAnnAK.get(i).toString() + " \t :: ");
                }

            } else {
                for (int i = Math.min(listAnnAK.size(), listAnnBQ.size()); i < listAnnBQ.size(); i++) {
                    pwOut.println(" \t :: " + listAnnBQ.get(i).toString());
                }
            }
        }
        pwOut.close();
    }

}
