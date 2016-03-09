/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.atmykitchen.report.unithood;

import info.atmykitchen.common.IOMethods;
import info.atmykitchen.common.MapCounter;
import info.atmykitchen.objects.SimpleTermAnnotation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Build annotation profile from a set of files in a directory
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class AnnotatorProfile {

    private final TreeMap<String, AnnotationFileStat> annotationStatPerFile;
    private final MapCounter<String> mpTerm;
    private final MapCounter<SimpleTermAnnotation> mpSimAnno;
    private final String name;

    public AnnotatorProfile(String fileRoot) throws Exception {

        List<String> annotationFiles = IOMethods.getAnnotationFiles(fileRoot);
        annotationStatPerFile
                = new TreeMap();
        mpTerm = new MapCounter<>();
        mpSimAnno = new MapCounter<>();
        //     allAnnotatedTerms = new ArrayList();
        for (String fileName : annotationFiles) {
            AnnotationFileStat afs = new AnnotationFileStat();
            afs.loadAnnotations(fileRoot + fileName);
            mpTerm.add(afs.getTermCountMap());
            mpSimAnno.add(afs.getSimpleAnnotationMap());
            //afs.getAnnotations(fileRoot + "/" + fileName);
            annotationStatPerFile.put(afs.getId(), afs);
        }

        Path p = Paths.get(fileRoot);
        this.name= p.getFileName().toString();
        

    }

//    public List<String> getAllAnnotatedTerms() {
//        return allAnnotatedTerms;
//    }
    public TreeMap<String, AnnotationFileStat> getAnnotationStatPerFile() {
        return annotationStatPerFile;
    }

    public MapCounter<SimpleTermAnnotation> getMpSimAnnoAll() {
        return mpSimAnno;
    }

    public MapCounter<String> getMpTermAll() {
        return mpTerm;
    }

    public void printAllTerms() {
        this.mpTerm.keySet().stream().forEach((term) -> {
            System.out.println("\t" + term + "\t" + mpTerm.get(term));
        });
    }

    public void printAllAnnotations() {
        this.mpSimAnno.keySet().stream().forEach((smp) -> {
            System.out.println("\t" + smp.toString() + "\t" + mpSimAnno.get(smp));
        });
    }

    public void writeFileAllTerms(String file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(file));
        pw.println("### Term\tFrequency");
        for (String term : this.mpTerm.keySet()) {
            pw.println(term + "\t\t:" + mpTerm.get(term));
        }

        pw.close();
    }

    public void writeFileAllAnnotation(String file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(file));
        pw.println("### Term\t Category\tFrequency");
        for (SimpleTermAnnotation termAnnot : this.mpSimAnno.keySet()) {
            pw.println(termAnnot.toString() + "\t" + mpSimAnno.get(termAnnot));
        }

        pw.close();
    }

    public void getDiffTermList(AnnotatorProfile secondProfile, PrintWriter writer) throws FileNotFoundException {
        writer.println("# Comparison of annotation profiles");
        writer.println("* Profile 1: " + this.name);
        writer.println("* Profile 2: " + secondProfile.getName());
        double computeFMScore = computeFMScore( this.getMpTermAll(), secondProfile.getMpTermAll());
        writer.println("## OVERALL COMPUTED F-SCORE IS: " + computeFMScore);
        TreeSet<String> diffMap = getDiffMap(this.mpTerm, secondProfile.mpTerm);
       
        writer.println("* Terms that are only annotated by " + this.name);
        for (String ss : diffMap) {
            writer.println("\t* " + ss);

        }
        writer.println();
        TreeSet<String> diffMap2 = getDiffMap(secondProfile.mpTerm, this.mpTerm);
        writer.println("* Terms that are only by " + secondProfile.name);
        for (String ss : diffMap2) {
            writer.println("\t* " + ss);
        }
    }

    public void getDiffTermListPerFile(AnnotatorProfile secondProfile, PrintWriter writer) throws FileNotFoundException {

        for (String paperid : this.getAnnotationStatPerFile().keySet()) {
            writer.println("## Analysis of " + paperid);
            double fscore = computeFMScore(this.getAnnotationStatPerFile().get(paperid).getTermCountMap(),
                    secondProfile.getAnnotationStatPerFile().get(paperid).getTermCountMap());
            writer.println("Computed F-Score for this file: " + fscore);
            TreeMap<String, Integer> firstTermCountMap = this.annotationStatPerFile.get(paperid).getTermCountMap();
            TreeMap<String, Integer> secondTermCountMap = secondProfile.getAnnotationStatPerFile().get(paperid).getTermCountMap();

            TreeSet<String> diffMap = getDiffMap(firstTermCountMap, secondTermCountMap);

            writer.println("* Terms that are only in annotations from *" + this.name +"*");
            for (String ss : diffMap) {
                writer.println("\t * " + ss);

            }

            TreeSet<String> diffMap2 = getDiffMap(secondTermCountMap, firstTermCountMap);
            writer.println("* Terms that are only in annotations from *" + secondProfile.name +"*");
            
            for (String ss : diffMap2) {
                writer.println("\t * " + ss);
            }
            writer.println("");
            
        }
        
    }

    private TreeSet<String> getDiffMap(TreeMap at, TreeMap bt) {
        TreeSet<String> termInOne = new TreeSet();
        for (Object t : at.keySet()) {
            if (!bt.containsKey(t)) {
                termInOne.add(t.toString());
            }
        }
        return termInOne;
    }

    public String getName() {
        return name;
    }

    
    /**
     * Compute F-Score for different inputs in the form of TreeMap
     * @param <T>
     * @param a
     * @param b
     * @return 
     */
       public <T> double computeFMScore(TreeMap<T, Integer> a  , TreeMap<T, Integer> b ){

        //assume that *a* is the gold referencet, 
        int truePositives = 0; /// this means that a term is in both *a* and *b*
        // int trueNegative = 0; --> does not exist in our evaluation since only terms are annotated
        int falsePositives = 0; /// count of terms appear in *b* but not in *a* is a false positive
        int falseNegatives = 0; /// count of terms apper in *a* but not in *b* is a false negative

        for (T term : a.keySet()) {
            int goldCount = a.get(term);
            if (!b.containsKey(term)) {
                falseNegatives += goldCount;
                
            } else {
                Integer testSetSeen = b.get(term);
                if (testSetSeen == goldCount) {
                    truePositives += goldCount;
                } else {
                    if (testSetSeen < goldCount) {
                        truePositives += testSetSeen;
                        falseNegatives += (goldCount - testSetSeen);
                    } else {
                        truePositives += goldCount;
                        falsePositives += (testSetSeen - goldCount);
                    }
                }
            } 
        }
        
        for(T term: b.keySet()){
            if(!a.containsKey(term)){
              falsePositives+= b.get(term);
            }
            
        }

//        System.out.println("TP: " + truePositives);
//        System.out.println("FP: " + falsePositives);
//        System.out.println("FN: " + falseNegatives);
        
        double precision= (truePositives*1.0 )/(truePositives+falsePositives);
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
}
